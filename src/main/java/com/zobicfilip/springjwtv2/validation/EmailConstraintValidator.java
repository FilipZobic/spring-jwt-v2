package com.zobicfilip.springjwtv2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailConstraintValidator implements ConstraintValidator<EmailConstraint, String> {

    boolean onlyTrustedDomains;
    boolean onlyRegisteredDomains;

    @Autowired
    private EmailValidationService emailValidationService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return emailValidationService.emailPatternValid(value)
                && (!onlyTrustedDomains || emailValidationService.emailBelongsToTrustedDomain(value))
                && (!onlyRegisteredDomains || emailValidationService.emailDomainIsRegistered(value));
    }

    @Override
    public void initialize(EmailConstraint constraintAnnotation) {
        onlyTrustedDomains = constraintAnnotation.onlyTrustedDomains();
        onlyRegisteredDomains = constraintAnnotation.onlyRegisteredDomains();
    }
}
