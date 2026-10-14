package com.pedidos360.catalogo.service;

import com.pedidos360.catalogo.dto.OrdenCreadaEventDto;
import com.pedidos360.catalogo.exception.StockException;
import com.pedidos360.catalogo.model.OrdenProcesada;
import com.pedidos360.catalogo.model.Producto;
import com.pedidos360.catalogo.repository.OrdenProcesadaRepository;
import com.pedidos360.catalogo.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final ProductoRepository productoRepository;
    private final OrdenProcesadaRepository ordenProcesadaRepository;

    public StockService(ProductoRepository productoRepository, OrdenProcesadaRepository ordenProcesadaRepository) {
        this.productoRepository = productoRepository;
        this.ordenProcesadaRepository = ordenProcesadaRepository;
    }

    // Todo o nada: si falta stock de un producto no se descuenta ninguno
    @Transactional
    public void descontarStock(OrdenCreadaEventDto evento) {
        if (evento.ordenId() == null || evento.items() == null || evento.items().isEmpty()) {
            throw new StockException("Evento de orden sin id o sin ítems");
        }
        if (ordenProcesadaRepository.existsById(evento.ordenId())) {
            log.warn("La orden #{} ya descontó stock, se ignora el evento duplicado", evento.ordenId());
            return;
        }

        for (OrdenCreadaEventDto.Item item : evento.items()) {
            if (item.productoId() == null || item.cantidad() == null || item.cantidad() <= 0) {
                throw new StockException("Ítem inválido en la orden #" + evento.ordenId() + ": " + item);
            }
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new StockException("No existe el producto #" + item.productoId()));
            if (producto.getStock() < item.cantidad()) {
                throw new StockException("Stock insuficiente de '" + producto.getNombre() + "': hay "
                        + producto.getStock() + ", se pidieron " + item.cantidad());
            }
            producto.setStock(producto.getStock() - item.cantidad());
            log.info("Stock de '{}' descontado en {} (queda {})", producto.getNombre(), item.cantidad(), producto.getStock());
        }

        ordenProcesadaRepository.save(new OrdenProcesada(evento.ordenId()));
    }
}
