package com.pedidos360.catalogo.service;

import com.pedidos360.catalogo.dto.OrdenCreadaEventDto;
import com.pedidos360.catalogo.exception.StockException;
import com.pedidos360.catalogo.model.OrdenProcesada;
import com.pedidos360.catalogo.model.Producto;
import com.pedidos360.catalogo.repository.OrdenProcesadaRepository;
import com.pedidos360.catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private OrdenProcesadaRepository ordenProcesadaRepository;

    @InjectMocks
    private StockService stockService;

    private static OrdenCreadaEventDto orden(long id, long productoId, int cantidad) {
        return new OrdenCreadaEventDto(id, "cliente@pedidos360.cl",
                List.of(new OrdenCreadaEventDto.Item(productoId, cantidad)));
    }

    @Test
    void descuentaElStockYRegistraLaOrden() {
        Producto pizza = new Producto("Pizza", 11490.0, 30);
        when(productoRepository.findById(2L)).thenReturn(Optional.of(pizza));

        stockService.descontarStock(orden(1, 2, 3));

        assertThat(pizza.getStock()).isEqualTo(27);
        verify(ordenProcesadaRepository).save(any(OrdenProcesada.class));
    }

    @Test
    void stockInsuficienteNoDescuentaNada() {
        Producto brownie = new Producto("Brownie", 2490.0, 2);
        when(productoRepository.findById(14L)).thenReturn(Optional.of(brownie));

        assertThatThrownBy(() -> stockService.descontarStock(orden(2, 14, 5)))
                .isInstanceOf(StockException.class)
                .hasMessageContaining("Stock insuficiente");

        assertThat(brownie.getStock()).isEqualTo(2);
        verify(ordenProcesadaRepository, never()).save(any());
    }

    @Test
    void productoInexistenteEsErrorDeNegocio() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.descontarStock(orden(3, 99, 1)))
                .isInstanceOf(StockException.class)
                .hasMessageContaining("No existe el producto");
    }

    @Test
    void eventoDuplicadoSeIgnora() {
        when(ordenProcesadaRepository.existsById(4L)).thenReturn(true);

        stockService.descontarStock(orden(4, 2, 1));

        verify(productoRepository, never()).findById(any());
        verify(ordenProcesadaRepository, never()).save(any());
    }

    @Test
    void eventoSinItemsEsInvalido() {
        OrdenCreadaEventDto vacio = new OrdenCreadaEventDto(5L, "cliente@pedidos360.cl", List.of());

        assertThatThrownBy(() -> stockService.descontarStock(vacio)).isInstanceOf(StockException.class);
    }
}
