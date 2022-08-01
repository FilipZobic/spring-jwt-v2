package com.zobicfilip.springjwtv2.validation;

import com.zobicfilip.springjwtv2.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountNotFoundException;
import java.util.UUID;

public class UserExistsValidator implements ConstraintValidator<UserExists, UUID> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        try {
            userService.findUserById(value);
            return true;
        } catch (AccountNotFoundException e) {
            return false;
        }
    }
}
