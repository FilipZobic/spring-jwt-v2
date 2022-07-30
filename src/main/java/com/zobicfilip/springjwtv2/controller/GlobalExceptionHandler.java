package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.dto.BadParameterInputDTO;
import com.zobicfilip.springjwtv2.dto.ExceptionDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDTO> handleExceptions(AccessDeniedException exception, WebRequest webRequest) {
        return new ResponseEntity<>(
                new ExceptionDTO(
                        "Forbidden",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        HttpStatus.FORBIDDEN.value()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ExceptionDTO> handleExceptions(UnsupportedOperationException exception, WebRequest webRequest) {
        return new ResponseEntity<>(
                new ExceptionDTO(
                        "Operation not yet supported",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    // TODO use local
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionDTO> handleExceptions(Throwable exception, WebRequest webRequest) {
        exception.printStackTrace();
        return new ResponseEntity<>(
                new ExceptionDTO(
                        "Something went wrong",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(new BadParameterInputDTO(ex), HttpStatus.BAD_REQUEST);
    }
}
