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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // Endpoint GET para que usuario pueda ver la agenda de una sala en particular
    @GetMapping("/activo/{id}")
    public ResponseEntity<List<Reserva>> getAgendaDeSala(@PathVariable Integer id) {
        // Usamos el método que creamos en el repositorio
        List<Reserva> agenda = reservaRepository.findByActivoId(id);
        
        return ResponseEntity.ok(agenda);
    }

    /**
     * Endpoint de CLIENTE para crear una nueva reserva.
     * Cumple con: POST /api/reservar
     */
    @PostMapping("/reservar")
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaRequestDTO request) {

        // --- TAREAS PENDIENTES (¡MUY IMPORTANTE!) ---
        // 1. VALIDAR DISPONIBILIDAD: Debemos comprobar que 
        //    request.getActivoId() no esté reservado entre
        //    request.getFechaInicio() y request.getFechaTermino().
        //    Si lo está, devolvemos un 409 Conflict.
        //
        // 2. OBTENER DATOS DEL TOKEN: Necesitaremos el 'usuario_id' 
        //    que vendrá en el token (lo inyectará el Gateway).
        //
        // 3. LLAMAR A CATALOGO-SERVICE: Necesitamos llamar por API 
        //    a catalogo-service para obtener el precio de la sala 
        //    (activoId) y los precios de los consumibles (productoId).
        // --- FIN TAREAS PENDIENTES ---


        // --- Lógica de guardado (Simplificada por ahora) ---

        // 1. Crear el objeto Reserva principal
        Reserva nuevaReserva = new Reserva();
        
        // (Simulación de datos que vendrían del token y de catalogo-service)
        nuevaReserva.setUsuarioId(999); // ID de usuario (temporal)
        nuevaReserva.setPrecioTotalSala(BigDecimal.valueOf(30000)); // Precio (temporal)
        // (Fin de simulación)

        nuevaReserva.setActivoId(request.getActivoId());
        nuevaReserva.setFechaInicio(request.getFechaInicio());
        nuevaReserva.setFechaTermino(request.getFechaTermino());
        nuevaReserva.setFechaReservaCreada(new Timestamp(System.currentTimeMillis()));
        nuevaReserva.setEstado("confirmada");
        
        // 2. Preparar los consumibles (si es que hay)
        List<PedidosConsumibles> listaConsumibles = new ArrayList<>();
        if (request.getConsumibles() != null && !request.getConsumibles().isEmpty()) {
            
            for (var itemDto : request.getConsumibles()) {
                PedidosConsumibles consumible = new PedidosConsumibles();
                consumible.setProductoId(itemDto.getProductoId());
                consumible.setCantidad(itemDto.getCantidad());
                consumible.setPrecioUnitarioOriginal(BigDecimal.valueOf(5000)); // Precio (temporal)
                
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
