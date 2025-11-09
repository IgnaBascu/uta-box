package com.utabox.reservas_service.dto;

import java.sql.Timestamp;
import java.util.List;

public class ReservaRequestDTO {
    
    // El ID de la sala física (ej. Sala "Budokan")
    private Integer activoId;

    // La fecha/hora de inicio que quiere el cliente
    private Timestamp fechaInicio;

    // La fecha/hora de término que quiere el cliente
    private Timestamp fechaTermino;

    // Una lista de los consumibles que quiere
    private List<ConsumibleRequestDTO> consumibles;

    // Getters & Setters

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

    public List<ConsumibleRequestDTO> getConsumibles() {
        return consumibles;
    }

    public void setConsumibles(List<ConsumibleRequestDTO> consumibles) {
        this.consumibles = consumibles;
    }

    

    
}
