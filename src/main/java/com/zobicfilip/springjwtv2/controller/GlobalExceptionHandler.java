package com.zobicfilip.springjwtv2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleExceptions(AccessDeniedException exception, WebRequest webRequest) {
        var response = new HashMap<String, Object>();
        response.put("message", "Forbidden");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        response.put("status", HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // TODO use local
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleExceptions(Throwable exception, WebRequest webRequest) {
        var response = new HashMap<String, Object>();
        response.put("message", "Something went wrong");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
