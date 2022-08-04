package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.dto.BadParameterInputDTO;
import com.zobicfilip.springjwtv2.dto.GenericExceptionResponseDTO;
import com.zobicfilip.springjwtv2.exception.FailedProfilePictureOperationException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FailedProfilePictureOperationException.class)
    public ResponseEntity<GenericExceptionResponseDTO> handleExceptions(FailedProfilePictureOperationException exception, WebRequest webRequest) {
        return new ResponseEntity<>(
                new GenericExceptionResponseDTO(
                        exception.getMessage(),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        exception.getStatus().value()),
                exception.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GenericExceptionResponseDTO> handleExceptions(AccessDeniedException exception, WebRequest webRequest) {
        log.warn("Access denied");
        return new ResponseEntity<>(
                new GenericExceptionResponseDTO(
                        "Forbidden",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        HttpStatus.FORBIDDEN.value()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<GenericExceptionResponseDTO> handleExceptions(MaxUploadSizeExceededException exception, WebRequest webRequest) {
        log.warn("Maximum multipart file size upload exceeded size: {}", exception.getMaxUploadSize());
        return new ResponseEntity<>(
                new GenericExceptionResponseDTO(
                        "File size exceeds constraint",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GenericExceptionResponseDTO> handleExceptions(ConstraintViolationException exception, WebRequest webRequest) {
        log.warn("ConstraintViolationException message: {}", exception.getMessage());
        StringBuilder sb = new StringBuilder(exception.getMessage());
        int indexOf = exception.getMessage().indexOf(":");
        if (indexOf != -1) {
            sb.delete(0, indexOf+1);
        }
        return new ResponseEntity<>(
                new GenericExceptionResponseDTO(
                        sb.toString().trim(),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<GenericExceptionResponseDTO> handleExceptions(UnsupportedOperationException exception, WebRequest webRequest) {
        log.error("Unsupported operation accessed on URL: {}", webRequest.getContextPath());
        return new ResponseEntity<>(
                new GenericExceptionResponseDTO(
                        "Operation not yet supported",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    // TODO use local
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<GenericExceptionResponseDTO> handleExceptions(Throwable exception, WebRequest webRequest) {
        log.error("Unhandled error name: {} message {}", exception.getClass().getName(), exception.getMessage(), exception);
        return new ResponseEntity<>(
                new GenericExceptionResponseDTO(
                        "Something went wrong",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BadParameterInputDTO badParameterInputDTO = new BadParameterInputDTO(ex);
        log.warn("Validation constraint violation count: {} errors: {}", ex.getErrorCount(), badParameterInputDTO.getValidation());
        return new ResponseEntity<>(badParameterInputDTO, HttpStatus.BAD_REQUEST);
    }
}
