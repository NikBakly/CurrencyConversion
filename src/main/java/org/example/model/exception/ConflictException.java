package org.example.model.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(Throwable cause) {
        super(cause);
    }
}
