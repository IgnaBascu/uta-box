package com.utabox.reservas_service.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    // El ID del usuario que hace la reserva (viene del JWT)
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    // El ID de la sala física (de la tabla 'activos')
    @Column(name = "activo_id", nullable = false)
    private Integer activoId;

    @Column(name = "fecha_inicio", nullable = false)
    private Timestamp fechaInicio;

    @Column(name = "fecha_termino", nullable = false)
    private Timestamp fechaTermino;

    @Column(name = "fecha_reserva_creada", nullable = false, updatable = false)
    private Timestamp fechaReservaCreada;

    @Column(nullable = false, length = 15)
    private String estado; // ej. "confirmada", "cancelada"

    @Column(name = "precio_total_sala", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotalSala;

    // --- Relación ---
    // Una Reserva puede tener muchos Pedidos de Consumibles
    @JsonManagedReference
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL)
    private List<PedidosConsumibles> consumibles;

    // Getters & Setters

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getActivoId() {
        return activoId;
    }

    public void setActivoId(Integer activoId) {
        this.activoId = activoId;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Timestamp fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Timestamp getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(Timestamp fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public Timestamp getFechaReservaCreada() {
        return fechaReservaCreada;
    }

    public void setFechaReservaCreada(Timestamp fechaReservaCreada) {
        this.fechaReservaCreada = fechaReservaCreada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getPrecioTotalSala() {
        return precioTotalSala;
    }

    public void setPrecioTotalSala(BigDecimal precioTotalSala) {
        this.precioTotalSala = precioTotalSala;
    }

    public List<PedidosConsumibles> getConsumibles() {
        return consumibles;
    }

    public void setConsumibles(List<PedidosConsumibles> consumibles) {
        this.consumibles = consumibles;
    }

    
    
}
