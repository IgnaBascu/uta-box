package com.utabox.reservas_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.utabox.reservas_service.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>{
    
    // Busca todas las reservas para una sala específica (activoId)
    List<Reserva> findByActivoId(Integer activoId);
}
