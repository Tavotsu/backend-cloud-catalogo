package com.pedidos360.catalogo.config;

import com.pedidos360.catalogo.model.Producto;
import com.pedidos360.catalogo.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// Productos iniciales (mismos nombres que el catálogo del front)
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner cargarProductos(ProductoRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            List<Producto> productos = List.of(
                    new Producto("Burger 360 Doble Cheddar", 7990.0, 50),
                    new Producto("Pizza Napolitana Familiar 360", 11490.0, 30),
                    new Producto("Sushi Roll Tempura Ebi (12 cortes)", 8990.0, 40),
                    new Producto("Bowl Salmón & Palta", 9490.0, 25),
                    new Producto("Tacos Al Pastor Artesanales (x3)", 6990.0, 40),
                    new Producto("Crispy Wings BBQ & Honey (8 uds)", 7490.0, 35),
                    new Producto("Papel Higiénico Confort Doble Hoja", 4290.0, 100),
                    new Producto("Fideos Spaghetti Carozzi N° 5", 1190.0, 200),
                    new Producto("Queso Gouda Laminado Colun 500g", 4890.0, 60),
                    new Producto("Leche Entera 1L (Pack x6)", 5490.0, 80),
                    new Producto("Aceite Vegetal Maravilla (900ml)", 2490.0, 90),
                    new Producto("Bebida Coca-Cola Zero", 1500.0, 150),
                    new Producto("Papas Fritas Rústicas", 2990.0, 70),
                    new Producto("Postre Brownie Fudgy", 2490.0, 5));
            repository.saveAll(productos);
            log.info("Catálogo inicial cargado con {} productos", productos.size());
        };
    }
}
