package com.zobicfilip.springjwtv2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class FailedProfilePictureOperationException extends Exception {

    @Getter
    private final HttpStatus status;
    public FailedProfilePictureOperationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
