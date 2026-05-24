package com.natwest.tc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSignalDelayException extends RuntimeException {
    public InvalidSignalDelayException(final String message) {
        super(message);
    }
}
