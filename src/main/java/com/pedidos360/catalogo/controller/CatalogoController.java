package com.pedidos360.catalogo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoController {

    private final com.pedidos360.catalogo.repository.ProductoRepository repository;

    public CatalogoController(com.pedidos360.catalogo.repository.ProductoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/public")
    public java.util.List<com.pedidos360.catalogo.model.Producto> publico() {
        return repository.findAll();
    }

    @GetMapping("/privado")
    public java.util.List<com.pedidos360.catalogo.model.Producto> privado() {
        return repository.findAll();
    }
}
