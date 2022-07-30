package com.zobicfilip.springjwtv2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UsernameRegexValidator implements ConstraintValidator<UsernameRegex, String> {

    @Autowired
    private UsernameValidationService usernameValidationService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return usernameValidationService.usernamePatternValid(value);
    }
}
