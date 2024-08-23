package org.ganeshjadhav.exception;

public class OrderCanNotBeModifiedNowException extends RuntimeException {
    public OrderCanNotBeModifiedNowException() {
        super();
    }

    public OrderCanNotBeModifiedNowException(String message) {
        super(message);
    }
}
