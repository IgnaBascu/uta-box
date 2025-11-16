package com.utabox.core_service.dto;

import java.math.BigDecimal;

// DTO para mapear la respuesta de catalogo-service
public class ProductoDTO {
    
    private BigDecimal precio;    
    private String tipo;

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


}