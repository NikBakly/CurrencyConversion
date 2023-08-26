package org.example.model.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record ApiError(String message,
                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd.MM.yyyy")
                       Date timestamp) {
}
