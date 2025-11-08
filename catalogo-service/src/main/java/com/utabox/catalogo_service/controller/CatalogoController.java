package com.utabox.catalogo_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utabox.catalogo_service.model.Producto;
import com.utabox.catalogo_service.repository.ProductoRepository;

@RestController
@RequestMapping("api/productos") // Prefijo para todos los endpoints
public class CatalogoController {
    
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/salas") // Endpoint publico
    public ResponseEntity<List<Producto>> getTiposDeSala() {
        
        List<Producto> salas = productoRepository.findByTipoOrderByIdProductoAsc("sala");   
        
        return ResponseEntity.ok(salas);
    }



}
