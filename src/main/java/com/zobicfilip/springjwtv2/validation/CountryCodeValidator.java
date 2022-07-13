package com.zobicfilip.springjwtv2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CountryCodeValidator implements ConstraintValidator<CountryCode, String> {

    private CountryCode.Type codeType;

    @Autowired
    private CountryValidationService countryValidationService;


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (codeType.equals(CountryCode.Type.ALPHA_2)) {
            return countryValidationService.isAlpha2Valid(value);
        }
        return countryValidationService.isAlpha3Valid(value);
    }

    @Override
    public void initialize(CountryCode constraintAnnotation) {
        this.codeType = constraintAnnotation.type();
    }
}
