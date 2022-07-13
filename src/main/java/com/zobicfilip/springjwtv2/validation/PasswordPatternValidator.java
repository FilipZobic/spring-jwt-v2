package com.zobicfilip.springjwtv2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class PasswordPatternValidator implements ConstraintValidator <PasswordPattern, String> {

    @Autowired
    private PasswordValidationService passwordValidationService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return passwordValidationService.patternValid(value);
    }
}
