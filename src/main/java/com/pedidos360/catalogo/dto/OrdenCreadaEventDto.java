package com.pedidos360.catalogo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrdenCreadaEventDto(Long ordenId, String usuarioEmail, List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(Long productoId, Integer cantidad) {}
}
