package com.natwest.tc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConflictStateUpdateException extends RuntimeException {
    public ConflictStateUpdateException(String message) {
        super(message);
    }
}
