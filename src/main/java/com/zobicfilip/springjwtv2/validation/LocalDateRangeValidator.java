package com.zobicfilip.springjwtv2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateRangeValidator implements ConstraintValidator <LocalDateRange, LocalDate> {

    private LocalDate before;

    private LocalDate after;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) return true;

        boolean beforePass = before == null || before.isAfter(value);
        boolean afterPass = after == null || after.isBefore(value);

        return beforePass && afterPass;
    }

    @Override
    public void initialize(LocalDateRange constraintAnnotation) {
        try {
            before = LocalDate.parse(constraintAnnotation.before(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {}
        try {
            after = LocalDate.parse(constraintAnnotation.after(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {}
    }
}
