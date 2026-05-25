package com.natwest.tc.controller;

import com.natwest.tc.exceptions.InvalidSignalDelayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

//    @ExceptionHandler(value = InvalidSignalDelayException.class)
    public void handleInvalidSignalDelayException(InvalidSignalDelayException ex) {
//        return ResponseEntity
    }
}
