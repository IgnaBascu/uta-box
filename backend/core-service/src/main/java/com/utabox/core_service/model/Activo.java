package com.utabox.core_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "activos")
public class Activo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_activo")
    private Integer idActivo;

    @Column(name = "nombre_sala", nullable = false, length = 255)
    private String nombreSala; // ej. "Sala VIP 101"

    @Column(nullable = false, length = 20)
    private String estado; // ej. "disponible", "mantenimiento"

    // --- Relación ---
    // Muchos Activos (salas) pertenecen a un Producto (tipo de sala)
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_sala_id", nullable = false)
    private TipoSala tipoSala;

    // --- Getters y Setters ---

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getIdActivo() {
        return idActivo;
    }

    public void setIdActivo(Integer idActivo) {
        this.idActivo = idActivo;
    }

    public TipoSala getTipoSala() {
        return tipoSala;
    }

    public void setTipoSala(TipoSala tipoSala) {
        this.tipoSala = tipoSala;
    }

}
