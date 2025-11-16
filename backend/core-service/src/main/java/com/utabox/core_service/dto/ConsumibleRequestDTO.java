package com.utabox.core_service.dto;

public class ConsumibleRequestDTO {
    
    // El ID del producto (ej. 4 para "Papas Fritas")
    private Integer productoId;

    // Cuántos quiere
    private Integer cantidad;

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

    
}
