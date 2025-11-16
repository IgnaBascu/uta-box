package com.utabox.core_service.repository;

import com.utabox.core_service.model.TipoSala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoSalaRepository extends JpaRepository<TipoSala, Integer> {
}