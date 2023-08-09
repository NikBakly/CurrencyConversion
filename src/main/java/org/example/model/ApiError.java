package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ApiError {
    private final String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd.MM.yyyy")
    private final Date timestamp;

    public ApiError(String message, Date timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
