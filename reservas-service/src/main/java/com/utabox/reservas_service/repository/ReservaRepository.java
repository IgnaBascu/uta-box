package com.utabox.reservas_service.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.utabox.reservas_service.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>{
    
    // Busca todas las reservas para una sala específica (activoId)
    List<Reserva> findByActivoId(Integer activoId);

    // Cuenta las reservas que se solapan conun rango de tiempo para una sala (Activo) 
    // La lógica es: (MiInicio < TuFin) Y (MiFin > TuInicio) y cuenta solo las confirmadas

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.activoId = :activoId AND r.estado = 'confirmada' AND r.fechaInicio < :fechaTermino AND r.fechaTermino > :fechaInicio")
    Integer countOverlappingReservations(
            @Param("activoId") Integer activoId,
            @Param("fechaInicio") Timestamp fechaInicio,
            @Param("fechaTermino") Timestamp fechaTermino
    );
}
