package com.zobicfilip.springjwtv2.dto;

import lombok.Getter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

public class BadParameterInputDTO {

    @Getter
    private final String timestamp;

    @Getter
    private final static String message = "Bad request body";

    @Getter
    private final Validation validation;

    private record Validation(Map<String, String> errors) {
    }

    public BadParameterInputDTO(MethodArgumentNotValidException ex) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.validation = new Validation(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
    }
}
