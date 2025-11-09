package com.utabox.reservas_service.model;

import java.math.BigDecimal;
import jakarta.persistence.*;


@Entity
@Table(name = "pedidos_consumibles")
public class PedidosConsumibles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_consumible")
    private Integer idPedidoConsumible;

    // El ID del producto (ej. Papas Fritas) de la tabla 'productos'
    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_original", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioOriginal;

    // --- Relación ---
    // Muchos pedidos pertenecen a una Reserva
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    // Getters & Setters

    public Integer getIdPedidoConsumible() {
        return idPedidoConsumible;
    }

    public void setIdPedidoConsumible(Integer idPedidoConsumible) {
        this.idPedidoConsumible = idPedidoConsumible;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioOriginal() {
        return precioUnitarioOriginal;
    }

    public void setPrecioUnitarioOriginal(BigDecimal precioUnitarioOriginal) {
        this.precioUnitarioOriginal = precioUnitarioOriginal;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    
    
}
