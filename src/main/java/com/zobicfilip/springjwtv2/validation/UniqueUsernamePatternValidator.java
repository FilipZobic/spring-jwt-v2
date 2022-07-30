package com.zobicfilip.springjwtv2.validation;

import com.zobicfilip.springjwtv2.exception.SecurityContextAuthenticationNotFoundException;
import com.zobicfilip.springjwtv2.service.UserService;
import com.zobicfilip.springjwtv2.util.Util;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountNotFoundException;

public class UniqueUsernamePatternValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private UserService userService;
    private boolean ignoreRuleIfSameAsSecurityContextUsername;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        if (ignoreRuleIfSameAsSecurityContextUsername) {
            try {
                return Util.getUserDetails().getUsername().equals(value);
            } catch (SecurityContextAuthenticationNotFoundException ignored) {}
        }
        try {
            userService.findUserByUsername(value);
            return false;
        } catch (AccountNotFoundException e) {
            return true;
        }
    }

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        this.ignoreRuleIfSameAsSecurityContextUsername = constraintAnnotation.ignoreRuleIfSameAsSecurityContextUsername();
    }
}
