package com.utabox.reservas_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utabox.reservas_service.model.Reserva;
import com.utabox.reservas_service.repository.ReservaRepository;

@RestController
@RequestMapping("api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    // Endpoint GET para que usuario pueda ver la agenda de una sala en particular
    @GetMapping("/activo/{id}")
    public ResponseEntity<List<Reserva>> getAgendaDeSala(@PathVariable Integer id) {
        // Usamos el método que creamos en el repositorio
        List<Reserva> agenda = reservaRepository.findByActivoId(id);
        
        return ResponseEntity.ok(agenda);
    }

    // (Aquí irá el POST /api/reservar más adelante)
    
}
