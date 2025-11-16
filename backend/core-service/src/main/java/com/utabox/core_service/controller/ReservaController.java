package com.utabox.core_service.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.utabox.core_service.dto.ReservaRequestDTO;
import com.utabox.core_service.model.PedidosConsumibles;
import com.utabox.core_service.model.Producto;
import com.utabox.core_service.model.Reserva;
import com.utabox.core_service.repository.PedidosConsumiblesRepository;
import com.utabox.core_service.repository.ProductoRepository;
import com.utabox.core_service.repository.ReservaRepository;

import java.util.Objects;

@RestController
@RequestMapping("api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PedidosConsumiblesRepository pedidosConsumiblesRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Endpoint GET para ver la agenda de una sala
    @GetMapping("/activo/{id}")
    public ResponseEntity<List<Reserva>> getAgendaDeSala(@PathVariable Integer id) {
        List<Reserva> agenda = reservaRepository.findByActivoId(id);
        return ResponseEntity.ok(agenda);
    }

    // Endpoint POST cliente para crear una reserva
    @PostMapping("/reservar")
    public ResponseEntity<Reserva> crearReserva(
            @RequestBody ReservaRequestDTO request,
            @RequestHeader("X-Usuario-Id") Integer usuarioId, // <-- Añade @NonNull aquí
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        // --- 0. VALIDACIÓN MANUAL DE DATOS REQUERIDOS ---
        if (request.getActivoId() == null || request.getFechaInicio() == null || request.getFechaTermino() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Los campos activoId, fechaInicio y fechaTermino no pueden ser nulos.");
        }

        Integer activoId = Objects.requireNonNull(request.getActivoId(), "activoId no puede ser null");

        // --- 1. VALIDAR DISPONIBILIDAD DE HORARIO (ARREGLO LÍNEA 72) ---
        Integer overlappingCount = reservaRepository.countOverlappingReservations(
                request.getActivoId(),
                request.getFechaInicio(),
                request.getFechaTermino());

        // ¡AQUÍ ESTÁ EL ARREGLO! Comprobamos si es nulo antes de compararlo.
        if (overlappingCount != null && overlappingCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El horario seleccionado para esta sala ya no está disponible.");
        }

        // --- 2. OBTENER DATOS DE LA SALA (ARREGLO LÍNEA 77) ---
        // (La comprobación de null en el Paso 0 arregla este error)
        Producto salaProducto = productoRepository.findById(activoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se pudo obtener el precio de la sala."));

        // --- 3. CÁLCULO DE PRECIO DE SALA (Sin cambios) ---
        BigDecimal precioPorHoraSala = salaProducto.getPrecio();
        Instant inicio = request.getFechaInicio().toInstant();
        Instant termino = request.getFechaTermino().toInstant();
        long minutosReservados = ChronoUnit.MINUTES.between(inicio, termino);
        BigDecimal horasReservadas = new BigDecimal(minutosReservados)
                .divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
        BigDecimal precioSalaCalculado = precioPorHoraSala.multiply(horasReservadas);

        // --- 4. INICIALIZAR RESERVA Y TOTALES (Sin cambios) ---
        BigDecimal totalConsumibles = BigDecimal.ZERO;
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setUsuarioId(usuarioId);
        nuevaReserva.setPrecioTotalSala(precioSalaCalculado);
        nuevaReserva.setActivoId(request.getActivoId());
        nuevaReserva.setFechaInicio(request.getFechaInicio());
        nuevaReserva.setFechaTermino(request.getFechaTermino());
        nuevaReserva.setFechaReservaCreada(new Timestamp(System.currentTimeMillis()));
        nuevaReserva.setEstado("confirmada");

        // --- 5. PROCESAR CONSUMIBLES (ARREGLO LÍNEA 107) ---
        List<PedidosConsumibles> listaConsumibles = new ArrayList<>();

        // Esta lógica SÍ permite que la lista de consumibles sea nula o vacía
        if (request.getConsumibles() != null && !request.getConsumibles().isEmpty()) {

            for (var itemDto : request.getConsumibles()) {

                // ¡AQUÍ ESTÁ EL ARREGLO! Validamos los datos del item ANTES de usarlos.
                if (itemDto.getProductoId() == null || itemDto.getCantidad() == null || itemDto.getCantidad() <= 0) {
                    // Si el cliente envía un consumible malformado, lo rechazamos.
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Los consumibles deben tener un productoId y una cantidad válida (mayor a 0).");
                }

                // 5a. OBTENER DATOS DEL CONSUMIBLE
                Integer productoId = Objects.requireNonNull(
                        itemDto.getProductoId(),
                        "productoId no puede ser null");

                Producto consumibleProducto = productoRepository.findById(productoId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Consumible no encontrado ID: " + productoId));

                // 5b. VALIDAR TIPO
                String tipo = consumibleProducto.getTipo();
                if ("sala".equals(tipo)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto ID: "
                            + itemDto.getProductoId() + " es una 'sala' y no puede ser añadido como consumible.");
                }

                // 5c. REDUCIR STOCK (Ahora es seguro)
                if (consumibleProducto.getStock() < itemDto.getCantidad()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "No hay stock suficiente para el producto ID: " + itemDto.getProductoId());
                }
                consumibleProducto.setStock(consumibleProducto.getStock() - itemDto.getCantidad());
                productoRepository.save(consumibleProducto);

                // 5d. CALCULAR SUBTOTALES
                BigDecimal precioUnitario = consumibleProducto.getPrecio();
                BigDecimal cantidad = new BigDecimal(itemDto.getCantidad());
                BigDecimal subtotalItem = precioUnitario.multiply(cantidad);
                totalConsumibles = totalConsumibles.add(subtotalItem);

                // 5e. PREPARAR PEDIDO
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
}