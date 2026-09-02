package com.pedidos360.catalogo.repository;

import com.pedidos360.catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
