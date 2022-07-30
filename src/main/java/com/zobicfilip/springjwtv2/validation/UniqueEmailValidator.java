package com.zobicfilip.springjwtv2.validation;

import com.zobicfilip.springjwtv2.exception.SecurityContextAuthenticationNotFoundException;
import com.zobicfilip.springjwtv2.service.UserService;
import com.zobicfilip.springjwtv2.util.Util;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountNotFoundException;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {


    private boolean ignoreRuleIfSameAsSecurityContextEmail;
    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        if (ignoreRuleIfSameAsSecurityContextEmail) {
            try {
                return Util.getUserDetails().getEmail().equals(value);
            } catch (SecurityContextAuthenticationNotFoundException ignored) {}
        }
        try {
            userService.findUserByEmail(value);
            return false;
        } catch (AccountNotFoundException e) {
            return true;
        }
    }

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        this.ignoreRuleIfSameAsSecurityContextEmail = constraintAnnotation.ignoreRuleIfSameAsSecurityContextEmail();
    }
}
