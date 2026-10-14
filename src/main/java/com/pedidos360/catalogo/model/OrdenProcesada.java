package com.pedidos360.catalogo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

// Órdenes ya procesadas, para no descontar stock dos veces
@Entity
public class OrdenProcesada {

    @Id
    private Long ordenId;
    private LocalDateTime fecha;

    public OrdenProcesada() {}

    public OrdenProcesada(Long ordenId) {
        this.ordenId = ordenId;
        this.fecha = LocalDateTime.now();
    }

    public Long getOrdenId() { return ordenId; }
    public LocalDateTime getFecha() { return fecha; }
}
