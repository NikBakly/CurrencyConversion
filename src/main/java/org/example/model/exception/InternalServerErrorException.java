package org.example.model.exception;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

    public InternalServerErrorException(String message) {
        super(message);
    }
}
