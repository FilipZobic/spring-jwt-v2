package com.zobicfilip.springjwtv2.exception;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import com.zobicfilip.springjwtv2.model.User;
import lombok.Getter;

public class RegistrationFailedException extends RuntimeException{
    // could be record
    @Getter private final AuthSignUpDTO dto;
    @Getter private final User user;
    @Getter private final RegErrorType errorType;
    public RegistrationFailedException(AuthSignUpDTO dto, User user, RegErrorType errorType) {
        super();
        this.dto = dto;
        this.user = user;
        this.errorType = errorType;
    }
}
