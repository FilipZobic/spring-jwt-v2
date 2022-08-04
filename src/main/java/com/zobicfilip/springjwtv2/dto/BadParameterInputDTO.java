package com.zobicfilip.springjwtv2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class BadParameterInputDTO {

    @Getter
    private String timestamp;

    @Getter
    private final static String message = "Bad request body";

    @Getter
    private Validation validation;


    public record Validation(Map<String, String> errors) {
    }

    public BadParameterInputDTO(MethodArgumentNotValidException ex) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.validation = new Validation(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
    }
}
