package com.utabox.reservas_service.controller;

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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; // Importante
import org.springframework.web.server.ResponseStatusException;

import com.utabox.reservas_service.dto.ProductoDTO;
import com.utabox.reservas_service.dto.ReservaRequestDTO;
import com.utabox.reservas_service.model.PedidosConsumibles;
import com.utabox.reservas_service.model.Reserva;
import com.utabox.reservas_service.repository.PedidosConsumiblesRepository;
import com.utabox.reservas_service.repository.ReservaRepository;


@RestController
@RequestMapping("api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PedidosConsumiblesRepository pedidosConsumiblesRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Endpoint GET para ver la agenda de una sala
    @GetMapping("/activo/{id}")
    public ResponseEntity<List<Reserva>> getAgendaDeSala(@PathVariable Integer id) {
        List<Reserva> agenda = reservaRepository.findByActivoId(id);
        return ResponseEntity.ok(agenda);
    }

    
    // Endpoint POST cliente para crear una reserva
    @PostMapping("/reservar")
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaRequestDTO request,
    @RequestHeader("X-Usuario-Id") Integer usuarioId ,
    @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        // --- 1. VALIDAR DISPONIBILIDAD DE HORARIO ---
        Integer overlappingCount = reservaRepository.countOverlappingReservations(
                request.getActivoId(),
                request.getFechaInicio(),
                request.getFechaTermino()
        );

        if (overlappingCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El horario seleccionado para esta sala ya no está disponible.");
        }

        // --- 2. OBTENER DATOS DE LA SALA (WebClient GET) ---
        ProductoDTO salaProducto = webClientBuilder.build()
            .get()
            .uri("lb://CATALOGO-SERVICE/api/productos/" + request.getActivoId())
            .header(HttpHeaders.AUTHORIZATION, authHeader)
            .retrieve()
            .bodyToMono(ProductoDTO.class)
            .block();

        if (salaProducto == null || salaProducto.getPrecio() == null) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo obtener el precio de la sala.");
        }

        // --- 3. CÁLCULO DE PRECIO DE SALA (POR HORAS) ---
        BigDecimal precioPorHoraSala = salaProducto.getPrecio();
        Instant inicio = request.getFechaInicio().toInstant();
        Instant termino = request.getFechaTermino().toInstant();
        
        // Calcula minutos y los convierte a horas con 2 decimales
        long minutosReservados = ChronoUnit.MINUTES.between(inicio, termino);
        BigDecimal horasReservadas = new BigDecimal(minutosReservados)
                                         .divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);

        BigDecimal precioSalaCalculado = precioPorHoraSala.multiply(horasReservadas);

        // --- 4. INICIALIZAR RESERVA Y TOTALES ---
        BigDecimal totalConsumibles = BigDecimal.ZERO; // Contador para consumibles
        
        Reserva nuevaReserva = new Reserva();        
        nuevaReserva.setUsuarioId(usuarioId);
        nuevaReserva.setPrecioTotalSala(precioSalaCalculado); // Asigna el precio por hora
        nuevaReserva.setActivoId(request.getActivoId());
        nuevaReserva.setFechaInicio(request.getFechaInicio());
        nuevaReserva.setFechaTermino(request.getFechaTermino());
        nuevaReserva.setFechaReservaCreada(new Timestamp(System.currentTimeMillis()));
        nuevaReserva.setEstado("confirmada");
        
        // --- 5. PROCESAR CONSUMIBLES (Bucle) ---
        List<PedidosConsumibles> listaConsumibles = new ArrayList<>();
        if (request.getConsumibles() != null && !request.getConsumibles().isEmpty()) {
            
            for (var itemDto : request.getConsumibles()) {

                // 5a. OBTENER DATOS DEL CONSUMIBLE (GET)
                ProductoDTO consumibleProducto = webClientBuilder.build()
                    .get()
                    .uri("lb://CATALOGO-SERVICE/api/productos/" + itemDto.getProductoId())
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(ProductoDTO.class)
                    .block();

                if (consumibleProducto == null || consumibleProducto.getPrecio() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo obtener el precio del consumible ID: " + itemDto.getProductoId());
                }
                
                // 5b. VALIDAR TIPO (¡NUEVO!)
                String tipo = consumibleProducto.getTipo();
                if ("sala".equals(tipo)) {
                    // Si es "sala", RECHAZAR.
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto ID: " + itemDto.getProductoId() + " es una 'sala' y no puede ser añadido como consumible.");
                }
                // Si no es "sala" (ej. "comida" o "bebida"), DEJAR PASAR.
                
                // 5c. REDUCIR STOCK (PUT)
                try {
                     webClientBuilder.build()
                        .put()
                        .uri("lb://CATALOGO-SERVICE/api/productos/{id}/reducir-stock?cantidad={cant}",
                             itemDto.getProductoId(),  // {id}
                             itemDto.getCantidad()     // {cant}
                        )
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .retrieve()
                        .toBodilessEntity() // No esperamos cuerpo de respuesta
                        .block();
                } catch (WebClientResponseException e) {
                    if (e.getStatusCode() == HttpStatus.CONFLICT) {
                        // Si catalogo-service nos dijo 409 (Sin Stock)...
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay stock suficiente para el producto ID: " + itemDto.getProductoId());
                    }
                    // Otro error en la llamada
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar stock: " + e.getMessage());
                }

                // 5d. CALCULAR SUBTOTALES
                BigDecimal precioUnitario = consumibleProducto.getPrecio();
                BigDecimal cantidad = new BigDecimal(itemDto.getCantidad());
                
                BigDecimal subtotalItem = precioUnitario.multiply(cantidad);
                totalConsumibles = totalConsumibles.add(subtotalItem); // Sumamos al total


                // 5e. PREPARAR PEDIDO
                PedidosConsumibles consumible = new PedidosConsumibles();
                consumible.setProductoId(itemDto.getProductoId());
                consumible.setCantidad(itemDto.getCantidad());
                consumible.setPrecioUnitarioOriginal(precioUnitario); // Guardamos el precio unitario
                consumible.setReserva(nuevaReserva); 
                listaConsumibles.add(consumible);
            }
        }
        
        // --- 6. SETEAR TOTALES FINALES ---
        BigDecimal granTotal = precioSalaCalculado.add(totalConsumibles);
        
        nuevaReserva.setPrecioTotalConsumibles(totalConsumibles);
        nuevaReserva.setPrecioTotalGeneral(granTotal);
        
        // --- 7. GUARDAR todo EN BD ---
        Reserva reservaGuardada = reservaRepository.save(nuevaReserva);

        if (!listaConsumibles.isEmpty()) {
            pedidosConsumiblesRepository.saveAll(listaConsumibles);
            reservaGuardada.setConsumibles(listaConsumibles);
        }

        return ResponseEntity.status(201).body(reservaGuardada);
    }
}