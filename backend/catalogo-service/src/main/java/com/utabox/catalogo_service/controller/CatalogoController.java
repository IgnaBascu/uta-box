package com.utabox.catalogo_service.controller;

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

import com.utabox.catalogo_service.dto.ProductoRequestDTO;
import com.utabox.catalogo_service.model.Producto;
import com.utabox.catalogo_service.repository.ProductoRepository;

import org.springframework.web.bind.annotation.RequestParam; // Para leer "?cantidad="
import org.springframework.http.HttpStatus; // Para poder devolver 409 Conflict

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/productos") // Prefijo para todos los endpoints
public class CatalogoController {

    @Autowired
    private ProductoRepository productoRepository;

    // Endpoint GET para obtener listado de salas
    @GetMapping("/salas")
    public ResponseEntity<List<Producto>> getTiposDeSala() {

        List<Producto> salas = productoRepository.findByTipoOrderByIdProductoAsc("sala");

        return ResponseEntity.ok(salas);
    }

    // Endpoint POST para agregar productos
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {

        // Convertimos el DTO (petición) a la Entidad (base de datos)
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(dto.getNombre());
        nuevoProducto.setDescripcion(dto.getDescripcion());
        nuevoProducto.setPrecio(dto.getPrecio());
        nuevoProducto.setStock(dto.getStock());
        nuevoProducto.setTipo(dto.getTipo());

        // Seteo fechas del servidor
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        nuevoProducto.setFechaCreacion(ahora);
        nuevoProducto.setFechaActualizacion(ahora);

        Producto productoGuardado = productoRepository.save(nuevoProducto);

        // Devolvemos 201 Created y el producto creado
        return ResponseEntity.status(201).body(productoGuardado);
    }

    // Endpoint PUT para actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id,
            @RequestBody Producto productoActualizado) {

        Optional<Producto> productoExistente = productoRepository.findById(id);

        if (productoExistente.isEmpty()) {
            // Si no lo encuentra, devuelve 404 Not Found
            return ResponseEntity.notFound().build();
        }

        Producto dbProducto = productoExistente.get();

        // Actualizamos los campos que pueden cambiar
        dbProducto.setNombre(productoActualizado.getNombre());
        dbProducto.setDescripcion(productoActualizado.getDescripcion());
        dbProducto.setPrecio(productoActualizado.getPrecio());
        dbProducto.setStock(productoActualizado.getStock());
        dbProducto.setTipo(productoActualizado.getTipo());
        dbProducto.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        // El id y la fechaCreacion no se tocan

        Producto productoGuardado = productoRepository.save(dbProducto);

        return ResponseEntity.ok(productoGuardado);
    }

    // Endpoint DELETE para borrar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        if (!productoRepository.existsById(id)) {
            // Si no existe, devuelve 404
            return ResponseEntity.notFound().build();
        }

        // Si el producto esta ligado a activos, se deben borrar primero los activos o
        // dará error de llaves

        try {
            productoRepository.deleteById(id);
            // Devuelve 204 No Content (éxito, pero no devuelve cuerpo)
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Si falla (ej. por la clave foránea), devuelve 409 Conflict
            return ResponseEntity.status(409).build();
        }
    }

    // Endpoint GET para devolver producto especifico
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoPorId(@PathVariable Integer id) {
        Optional<Producto> producto = productoRepository.findById(id);

        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(producto.get());
    }

    // Esta es la API que será llamada por el WebClient de reservas-service
    @PutMapping("/{id}/reducir-stock")
    public ResponseEntity<Void> reducirStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) { // Recibe el ID y la cantidad (ej. ?cantidad=4)

        Optional<Producto> productoOpt = productoRepository.findById(id);

        if (productoOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 si el producto no existe
        }

        Producto producto = productoOpt.get();

        // 1. Verificamos si hay stock 
        if (producto.getStock() < cantidad) {
            // 2. Si no hay stock, devuelve 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // 3. Si hay stock, lo reducimos y guardamos
        producto.setStock(producto.getStock() - cantidad);
        producto.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        productoRepository.save(producto);

        return ResponseEntity.ok().build(); // Devuelve 200 OK (Éxito)
    }

}
