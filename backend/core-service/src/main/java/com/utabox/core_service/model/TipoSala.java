package com.utabox.core_service.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "tipos_sala")
public class TipoSala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_sala")
    private Integer idTipoSala;

    private String nombre;
    private String descripcion;
    private String tematica;
    private String equipamiento;
    private Integer capacidad;

    @Column(name = "precio_hora")
    private BigDecimal precioHora;

    @Column(name = "imagen_url")
    private String imagenUrl;

    // --- Relación ---
    // Un TipoSala (plantilla) puede tener muchos Activos (salas físicas)
    @JsonManagedReference
    @OneToMany(mappedBy = "tipoSala", fetch = FetchType.LAZY)
    private List<Activo> activos;

    // --- Getters y Setters ---

    public Integer getIdTipoSala() { return idTipoSala; }
    public void setIdTipoSala(Integer idTipoSala) { this.idTipoSala = idTipoSala; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTematica() { return tematica; }
    public void setTematica(String tematica) { this.tematica = tematica; }
    public String getEquipamiento() { return equipamiento; }
    public void setEquipamiento(String equipamiento) { this.equipamiento = equipamiento; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public BigDecimal getPrecioHora() { return precioHora; }
    public void setPrecioHora(BigDecimal precioHora) { this.precioHora = precioHora; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public List<Activo> getActivos() { return activos; }
    public void setActivos(List<Activo> activos) { this.activos = activos; }
}