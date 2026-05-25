package com.natwest.tc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class IntersectionNotFoundException extends RuntimeException {
    public IntersectionNotFoundException(String message) {
        super(message);
    }
}
