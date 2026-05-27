package com.natwest.tc.controller;

import com.natwest.tc.dto.response.ErrorResponse;
import com.natwest.tc.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(value = IntersectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleIntersectionNotFoundException(IntersectionNotFoundException ex) {
        final ErrorResponse response = new ErrorResponse();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = InvalidSignalStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSignalStateException(InvalidSignalStateException ex) {
        final ErrorResponse response = new ErrorResponse();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = InvalidDirectionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDirectionException(InvalidDirectionException ex) {
        final ErrorResponse response = new ErrorResponse();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = ConflictStateUpdateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDirectionException(ConflictStateUpdateException ex) {
        final ErrorResponse response = new ErrorResponse();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = IntersectionLimitReachedException.class)
    public ResponseEntity<ErrorResponse> handleIntersectionLimitException(IntersectionLimitReachedException ex) {
        final ErrorResponse response = new ErrorResponse();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(final Exception ex) {
        final ErrorResponse response = new ErrorResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage("Internal Server Error");

        return ResponseEntity.badRequest().body(response);
    }
}
