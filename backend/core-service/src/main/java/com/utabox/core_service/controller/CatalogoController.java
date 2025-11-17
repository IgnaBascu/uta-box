package com.utabox.core_service.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utabox.core_service.model.TipoSala; 
import com.utabox.core_service.repository.TipoSalaRepository; 

import com.utabox.core_service.dto.ProductoRequestDTO;
import com.utabox.core_service.model.Producto;
import com.utabox.core_service.repository.ProductoRepository;

import jakarta.validation.Valid;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("api/productos") // Prefijo para todos los endpoints
public class CatalogoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TipoSalaRepository tipoSalaRepository; 

    // -- CRUD DE SALAS --

    // Endpoint GET para obtener listado de salas
    @GetMapping("/salas")
    public ResponseEntity<List<TipoSala>> getTiposDeSala() {
        List<TipoSala> salas = tipoSalaRepository.findAll();
        return ResponseEntity.ok(salas);
    }

    // Endpoint POST para crear un nuevo tipo de sala (No activos)
    @PostMapping("/salas")
    public ResponseEntity<TipoSala> crearTipoSala(@Valid @RequestBody @NonNull TipoSala tipoSala) {
        TipoSala salaGuardada = tipoSalaRepository.save(tipoSala);
        return ResponseEntity.status(201).body(salaGuardada);
    }

    // Endpoint PUT para actualizar los datos de tipo de sala
    @PutMapping("/salas/{id}")
    public ResponseEntity<TipoSala> actualizarTipoSala(@PathVariable @NonNull Integer id, @Valid @RequestBody TipoSala salaActualizada) {
        
        Optional<TipoSala> salaExistente = tipoSalaRepository.findById(id);
        if (salaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TipoSala dbSala = salaExistente.get();
        dbSala.setNombre(salaActualizada.getNombre());
        dbSala.setDescripcion(salaActualizada.getDescripcion());
        dbSala.setTematica(salaActualizada.getTematica());
        dbSala.setEquipamiento(salaActualizada.getEquipamiento());
        dbSala.setCapacidad(salaActualizada.getCapacidad());
        dbSala.setPrecioHora(salaActualizada.getPrecioHora());
        dbSala.setImagenUrl(salaActualizada.getImagenUrl());

        TipoSala salaGuardada = tipoSalaRepository.save(dbSala);
        return ResponseEntity.ok(salaGuardada);
    }

    // Endpoint DELETE para borrar algún tipo de sala
    // DELETE (NUEVO)
    @DeleteMapping("/salas/{id}")
    public ResponseEntity<Void> eliminarTipoSala(@PathVariable @NonNull Integer id) {
        if (!tipoSalaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            // Acá falta lógica para que el borrado sea con los activos (Cascada) <---- OJO!
            tipoSalaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Error si hay FK constraints (ej. un 'activo' usa esta sala)
            return ResponseEntity.status(409).build(); 
        }
    }

    // -- CRUD DE PRODUCTOS (Consumibles y Bebidas) --

    // GET /api/productos (Devuelve TODOS los consumibles)
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        // Filtramos para no incluir el tipo 'sala' si aún existe en esta tabla
        List<Producto> productos = productoRepository.findByTipoOrderByIdProductoAsc("comida");
        productos.addAll(productoRepository.findByTipoOrderByIdProductoAsc("bebida"));
        return ResponseEntity.ok(productos);
    }

    // Endpoint POST para agregar productos (consumibles)
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {

        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(dto.getNombre());
        nuevoProducto.setDescripcion(dto.getDescripcion());
        nuevoProducto.setPrecio(dto.getPrecio());
        nuevoProducto.setStock(dto.getStock());
        nuevoProducto.setTipo(dto.getTipo()); // 'comida' o 'bebida'

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        nuevoProducto.setFechaCreacion(ahora);
        nuevoProducto.setFechaActualizacion(ahora);

        Producto productoGuardado = productoRepository.save(nuevoProducto);
        return ResponseEntity.status(201).body(productoGuardado);
    }

    // Endpoint PUT para actualizar un producto (consumible)
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable @NonNull Integer id,
            @RequestBody Producto productoActualizado) {

        Optional<Producto> productoExistente = productoRepository.findById(id);

        if (productoExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Producto dbProducto = productoExistente.get();
        dbProducto.setNombre(productoActualizado.getNombre());
        dbProducto.setDescripcion(productoActualizado.getDescripcion());
        dbProducto.setPrecio(productoActualizado.getPrecio());
        dbProducto.setStock(productoActualizado.getStock());
        dbProducto.setTipo(productoActualizado.getTipo());
        dbProducto.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        Producto productoGuardado = productoRepository.save(dbProducto);
        return ResponseEntity.ok(productoGuardado);
    }

    // Endpoint DELETE para borrar un producto (consumible)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable @NonNull Integer id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(409).build();
        }
    }

    // Endpoint GET para devolver producto especifico (consumible)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoPorId(@PathVariable @NonNull Integer id) {
        Optional<Producto> producto = productoRepository.findById(id);

        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto.get());
    }

}