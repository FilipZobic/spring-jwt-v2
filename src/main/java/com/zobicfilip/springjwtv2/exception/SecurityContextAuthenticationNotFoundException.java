package com.zobicfilip.springjwtv2.exception;

public class SecurityContextAuthenticationNotFoundException extends Exception {
    public SecurityContextAuthenticationNotFoundException(String message) {
        super(message);
    }
}
