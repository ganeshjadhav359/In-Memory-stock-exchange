package org.ganeshjadhav.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super();
    }

    public NotFoundException(String userDoesNotExist) {
        super(userDoesNotExist);
    }
}
