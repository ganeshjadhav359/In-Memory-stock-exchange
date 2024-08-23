package org.ganeshjadhav.exception;

public class OrderBookNotFound extends RuntimeException {
    public OrderBookNotFound() {
        super();
    }

    public OrderBookNotFound(String message) {
        super(message);
    }
}
