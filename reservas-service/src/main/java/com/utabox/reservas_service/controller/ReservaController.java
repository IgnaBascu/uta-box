package com.utabox.reservas_service.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utabox.reservas_service.dto.ReservaRequestDTO;
import com.utabox.reservas_service.model.PedidosConsumibles;
import com.utabox.reservas_service.model.Reserva;
import com.utabox.reservas_service.repository.PedidosConsumiblesRepository;
import com.utabox.reservas_service.repository.ReservaRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PedidosConsumiblesRepository pedidosConsumiblesRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Endpoint GET para que usuario pueda ver la agenda de una sala en particular
    @GetMapping("/activo/{id}")
    public ResponseEntity<List<Reserva>> getAgendaDeSala(@PathVariable Integer id) {
        // Usamos el método que creamos en el repositorio
        List<Reserva> agenda = reservaRepository.findByActivoId(id);
        
        return ResponseEntity.ok(agenda);
    }

    
    // Endpoint POST cliente para reservar una sala
    @PostMapping("/reservar")
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaRequestDTO request,
    @RequestHeader("X-Usuario-Id") Integer usuarioId ,
    @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        // Validar Disponibilidad
        Integer overlappingCount = reservaRepository.countOverlappingReservations(
                request.getActivoId(),
                request.getFechaInicio(),
                request.getFechaTermino()
        );

        if (overlappingCount > 0) {
            // Devuelve 409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El horario seleccionado para esta sala ya no está disponible.");
        }

        // 2. LLAMAR A CATALOGO-SERVICE para obtener el precio de la SALA 
        ProductoDTO salaProducto = webClientBuilder.build()
            .get()
            .uri("lb://CATALOGO-SERVICE/api/productos/" + request.getActivoId())
            .header(HttpHeaders.AUTHORIZATION, authHeader)
            .retrieve() // Ejecuta la petición
            .bodyToMono(ProductoDTO.class) // Mapea la respuesta a nuestro DTO
            .block(); // Espera la respuesta (bloqueante)

        // 3. Crear el objeto Reserva principal
        Reserva nuevaReserva = new Reserva();        
        
        nuevaReserva.setUsuarioId(usuarioId); // id usuario tomado del header
        nuevaReserva.setPrecioTotalSala(salaProducto.getPrecio()); // Precio (temporal)
        

        nuevaReserva.setActivoId(request.getActivoId());
        nuevaReserva.setFechaInicio(request.getFechaInicio());
        nuevaReserva.setFechaTermino(request.getFechaTermino());
        nuevaReserva.setFechaReservaCreada(new Timestamp(System.currentTimeMillis()));
        nuevaReserva.setEstado("confirmada");
        
        // 2. Preparar los consumibles (si es que hay)
        List<PedidosConsumibles> listaConsumibles = new ArrayList<>();
        if (request.getConsumibles() != null && !request.getConsumibles().isEmpty()) {
            
            for (var itemDto : request.getConsumibles()) {

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

                PedidosConsumibles consumible = new PedidosConsumibles();
                consumible.setProductoId(itemDto.getProductoId());
                consumible.setCantidad(itemDto.getCantidad());
                consumible.setPrecioUnitarioOriginal(consumibleProducto.getPrecio()); // Precio (temporal)
                
                // Le decimos al consumible a qué reserva pertenece
                consumible.setReserva(nuevaReserva); 
                
                listaConsumibles.add(consumible);
            }
        }
        
        // 3. Guardar todo
        // (Guardamos la reserva principal)
        Reserva reservaGuardada = reservaRepository.save(nuevaReserva);

        // (Guardamos los consumibles)
        if (!listaConsumibles.isEmpty()) {
            pedidosConsumiblesRepository.saveAll(listaConsumibles);
            reservaGuardada.setConsumibles(listaConsumibles);
        }

        // Devolvemos 201 Created
        return ResponseEntity.status(201).body(reservaGuardada);

    }
    
}
