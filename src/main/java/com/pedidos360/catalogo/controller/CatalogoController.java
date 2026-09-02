package com.pedidos360.catalogo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoController {

    @GetMapping("/public")
    public String publico() {
        return "Catálogo Público: Puedes ver los productos sin autenticación.";
    }

    @GetMapping("/privado")
    public String privado() {
        return "Catálogo Privado: Estás viendo detalles confidenciales de los productos (Requiere Token).";
    }
}
