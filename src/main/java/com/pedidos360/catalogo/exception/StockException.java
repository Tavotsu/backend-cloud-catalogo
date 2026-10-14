package com.pedidos360.catalogo.exception;

// Error de negocio: no se reintenta, el mensaje va a la DLQ
public class StockException extends RuntimeException {

    public StockException(String message) {
        super(message);
    }
}
