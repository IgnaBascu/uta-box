package com.utabox.reservas_service.dto;

import java.math.BigDecimal;

// DTO para mapear la respuesta de catalogo-service
public class ProductoDTO {
    
    private BigDecimal precio;    

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}