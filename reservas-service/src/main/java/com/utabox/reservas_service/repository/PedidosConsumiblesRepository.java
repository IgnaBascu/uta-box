package com.utabox.reservas_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.utabox.reservas_service.model.PedidosConsumibles;

@Repository
public interface PedidosConsumiblesRepository extends JpaRepository<PedidosConsumibles, Integer>{
    // Nada por ahora
    
}
