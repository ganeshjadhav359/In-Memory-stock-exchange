package org.ganeshjadhav.exception;

public class OrderNotFound extends RuntimeException {
    public OrderNotFound() {
        super();
    }
    public OrderNotFound(String message) {
        super(message);
    }
}
