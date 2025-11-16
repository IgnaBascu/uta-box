package com.utabox.catalogo_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.utabox.catalogo_service.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByTipoOrderByIdProductoAsc(String tipo); // Busca productos por tipo
}
