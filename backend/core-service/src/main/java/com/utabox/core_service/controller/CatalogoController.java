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

import com.utabox.core_service.dto.ProductoRequestDTO;
import com.utabox.core_service.model.Producto;
import com.utabox.core_service.model.TipoSala; // Importa el nuevo modelo
import com.utabox.core_service.repository.ProductoRepository;
import com.utabox.core_service.repository.TipoSalaRepository; // Importa el nuevo repo

import jakarta.validation.Valid;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("api/productos") // Prefijo para todos los endpoints
public class CatalogoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TipoSalaRepository tipoSalaRepository; // Inyecta el repo de TipoSala

    // Endpoint GET para obtener listado de salas (AHORA USA TIPOSALA)
    @GetMapping("/salas")
    public ResponseEntity<List<TipoSala>> getTiposDeSala() {
        List<TipoSala> salas = tipoSalaRepository.findAll();
        return ResponseEntity.ok(salas);
    }

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