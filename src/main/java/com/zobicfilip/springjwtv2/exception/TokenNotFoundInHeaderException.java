package com.zobicfilip.springjwtv2.exception;

public class TokenNotFoundInHeaderException extends Exception{
    public TokenNotFoundInHeaderException(String message) {
        super(message);
    }
}
