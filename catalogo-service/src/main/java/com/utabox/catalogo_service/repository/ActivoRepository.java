package com.utabox.catalogo_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utabox.catalogo_service.model.Activo;

@Repository
public interface ActivoRepository extends JpaRepository<Activo, Integer>{
    
}
