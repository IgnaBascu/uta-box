package com.utabox.core_service.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.utabox.core_service.dto.ProductoRequestDTO;
import com.utabox.core_service.dto.ReservaRequestDTO;
import com.utabox.core_service.model.PedidosConsumibles;
import com.utabox.core_service.model.Producto;
import com.utabox.core_service.model.Reserva;
import com.utabox.core_service.model.TipoSala;
import com.utabox.core_service.repository.PedidosConsumiblesRepository;
import com.utabox.core_service.repository.ProductoRepository;
import com.utabox.core_service.repository.ReservaRepository;
import com.utabox.core_service.repository.TipoSalaRepository;

import org.springframework.web.bind.annotation.RequestParam; // Para leer "?cantidad="
import org.springframework.http.HttpStatus; // Para poder devolver 409 Conflict

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/productos") // Prefijo para todos los endpoints
public class CatalogoController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PedidosConsumiblesRepository pedidosConsumiblesRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TipoSalaRepository tipoSalaRepository;

    @GetMapping("/activo/{id}")
    public ResponseEntity<List<Reserva>> getAgendaDeSala(@PathVariable Integer id) {
        List<Reserva> agenda = reservaRepository.findByActivoId(id);
        return ResponseEntity.ok(agenda);
    }

    @PostMapping("/reservar")
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaRequestDTO request,
            @RequestHeader("X-Usuario-Id") Integer usuarioId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        // --- 1. VALIDAR DISPONIBILIDAD (Sin cambios) ---
        Integer overlappingCount = reservaRepository.countOverlappingReservations(
                request.getActivoId(),
                request.getFechaInicio(),
                request.getFechaTermino());

        if (overlappingCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El horario seleccionado para esta sala ya no está disponible.");
        }

        // --- 2. OBTENER DATOS DE LA SALA (¡Refactorizado!) ---
        // Asumimos que request.getActivoId() es el ID de la SALA FÍSICA (Activo)
        // (Si no es así, ajusta esta lógica)

        // 1. Busca el Activo (Sala física)
        // Activo activo = activoRepository.findById(request.getActivoId())...

        // *** NOTA: Tu DTO de reserva pide 'activoId', pero tu lógica de precio
        // *** usa 'productoId'. Vamos a asumir que el ID que llega en el DTO
        // *** es el ID del TIPO DE SALA (ej. 3 = Budokan).

        Integer tipoSalaId = request.getActivoId(); // Asumimos que esto es el ID de TipoSala

        TipoSala sala = tipoSalaRepository.findById(tipoSalaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se pudo encontrar la sala con ID: " + tipoSalaId));

        if (sala.getPrecioHora() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo obtener el precio de la sala.");
        }

        // --- 3. CÁLCULO DE PRECIO DE SALA (Sin cambios) ---
        BigDecimal precioPorHoraSala = sala.getPrecioHora(); // Usamos el precio del TipoSala
        Instant inicio = request.getFechaInicio().toInstant();
        Instant termino = request.getFechaTermino().toInstant();

        long minutosReservados = ChronoUnit.MINUTES.between(inicio, termino);
        BigDecimal horasReservadas = new BigDecimal(minutosReservados)
                .divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);

        BigDecimal precioSalaCalculado = precioPorHoraSala.multiply(horasReservadas);

        // --- 4. INICIALIZAR RESERVA (Sin cambios) ---
        BigDecimal totalConsumibles = BigDecimal.ZERO;
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setUsuarioId(usuarioId);
        nuevaReserva.setPrecioTotalSala(precioSalaCalculado);
        nuevaReserva.setActivoId(request.getActivoId()); // Guardamos el ID del Activo (sala física)
        nuevaReserva.setFechaInicio(request.getFechaInicio());
        nuevaReserva.setFechaTermino(request.getFechaTermino());
        nuevaReserva.setFechaReservaCreada(new Timestamp(System.currentTimeMillis()));
        nuevaReserva.setEstado("confirmada");

        // --- 5. PROCESAR CONSUMIBLES (¡Refactorizado!) ---
        List<PedidosConsumibles> listaConsumibles = new ArrayList<>();
        if (request.getConsumibles() != null && !request.getConsumibles().isEmpty()) {

            for (var itemDto : request.getConsumibles()) {

                // 5a. OBTENER DATOS DEL CONSUMIBLE (Local)
                Producto consumibleProducto = productoRepository.findById(itemDto.getProductoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Consumible no encontrado ID: " + itemDto.getProductoId()));

                // 5b. VALIDAR TIPO (Sin cambios)
                String tipo = consumibleProducto.getTipo();
                if ("sala".equals(tipo)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto ID: "
                            + itemDto.getProductoId() + " es una 'sala' y no puede ser añadido como consumible.");
                }

                // 5c. REDUCIR STOCK (Local)
                if (consumibleProducto.getStock() < itemDto.getCantidad()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "No hay stock suficiente para el producto ID: " + itemDto.getProductoId());
                }
                consumibleProducto.setStock(consumibleProducto.getStock() - itemDto.getCantidad());
                // (No olvides la fecha de actualización si la tienes en tu modelo Producto)
                productoRepository.save(consumibleProducto); // Guardamos el stock actualizado

                // 5d. CALCULAR SUBTOTALES (Sin cambios)
                BigDecimal precioUnitario = consumibleProducto.getPrecio();
                BigDecimal cantidad = new BigDecimal(itemDto.getCantidad());
                BigDecimal subtotalItem = precioUnitario.multiply(cantidad);
                totalConsumibles = totalConsumibles.add(subtotalItem);

                // 5e. PREPARAR PEDIDO (Sin cambios)
                PedidosConsumibles consumible = new PedidosConsumibles();
                consumible.setProductoId(itemDto.getProductoId());
                consumible.setCantidad(itemDto.getCantidad());
                consumible.setPrecioUnitarioOriginal(precioUnitario);
                consumible.setReserva(nuevaReserva);
                listaConsumibles.add(consumible);
            }
        }
    
        // --- 6. SETEAR TOTALES FINALES (Sin cambios) ---
        BigDecimal granTotal = precioSalaCalculado.add(totalConsumibles);
        nuevaReserva.setPrecioTotalConsumibles(totalConsumibles);
        nuevaReserva.setPrecioTotalGeneral(granTotal);

        // --- 7. GUARDAR todo EN BD (Sin cambios) ---
        Reserva reservaGuardada = reservaRepository.save(nuevaReserva);

        if (!listaConsumibles.isEmpty()) {
            pedidosConsumiblesRepository.saveAll(listaConsumibles);
            reservaGuardada.setConsumibles(listaConsumibles);
        }

        return ResponseEntity.status(201).body(reservaGuardada);
    }

    // Endpoint GET para obtener listado de salas
    @GetMapping("/salas")
    public ResponseEntity<List<TipoSala>> getTiposDeSala() { // <-- Devuelve TipoSala
        List<TipoSala> salas = tipoSalaRepository.findAll(); // <-- Llama al nuevo repo
        return ResponseEntity.ok(salas);
    }

    // GET /api/productos (Devuelve TODOS los productos, para el Admin)
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    // Endpoint POST para agregar productos
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {

        // Convertimos el DTO (petición) a la Entidad (base de datos)
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(dto.getNombre());
        nuevoProducto.setDescripcion(dto.getDescripcion());
        nuevoProducto.setPrecio(dto.getPrecio());
        nuevoProducto.setStock(dto.getStock());
        nuevoProducto.setTipo(dto.getTipo());

        // Seteo fechas del servidor
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        nuevoProducto.setFechaCreacion(ahora);
        nuevoProducto.setFechaActualizacion(ahora);

        Producto productoGuardado = productoRepository.save(nuevoProducto);

        // Devolvemos 201 Created y el producto creado
        return ResponseEntity.status(201).body(productoGuardado);
    }

    // Endpoint PUT para actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id,
            @RequestBody Producto productoActualizado) {

        Optional<Producto> productoExistente = productoRepository.findById(id);

        if (productoExistente.isEmpty()) {
            // Si no lo encuentra, devuelve 404 Not Found
            return ResponseEntity.notFound().build();
        }

        Producto dbProducto = productoExistente.get();

        // Actualizamos los campos que pueden cambiar
        dbProducto.setNombre(productoActualizado.getNombre());
        dbProducto.setDescripcion(productoActualizado.getDescripcion());
        dbProducto.setPrecio(productoActualizado.getPrecio());
        dbProducto.setStock(productoActualizado.getStock());
        dbProducto.setTipo(productoActualizado.getTipo());
        dbProducto.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        // El id y la fechaCreacion no se tocan

        Producto productoGuardado = productoRepository.save(dbProducto);

        return ResponseEntity.ok(productoGuardado);
    }

    // Endpoint DELETE para borrar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        if (!productoRepository.existsById(id)) {
            // Si no existe, devuelve 404
            return ResponseEntity.notFound().build();
        }

        // Si el producto esta ligado a activos, se deben borrar primero los activos o
        // dará error de llaves

        try {
            productoRepository.deleteById(id);
            // Devuelve 204 No Content (éxito, pero no devuelve cuerpo)
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Si falla (ej. por la clave foránea), devuelve 409 Conflict
            return ResponseEntity.status(409).build();
        }
    }

    // Endpoint GET para devolver producto especifico
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoPorId(@PathVariable Integer id) {
        Optional<Producto> producto = productoRepository.findById(id);

        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(producto.get());
    }

    // Esta es la API que será llamada por el WebClient de reservas-service
    @PutMapping("/{id}/reducir-stock")
    public ResponseEntity<Void> reducirStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) { // Recibe el ID y la cantidad (ej. ?cantidad=4)

        Optional<Producto> productoOpt = productoRepository.findById(id);

        if (productoOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 si el producto no existe
        }

        Producto producto = productoOpt.get();

        // 1. Verificamos si hay stock
        if (producto.getStock() < cantidad) {
            // 2. Si no hay stock, devuelve 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // 3. Si hay stock, lo reducimos y guardamos
        producto.setStock(producto.getStock() - cantidad);
        producto.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        productoRepository.save(producto);

        return ResponseEntity.ok().build(); // Devuelve 200 OK (Éxito)
    }

}
