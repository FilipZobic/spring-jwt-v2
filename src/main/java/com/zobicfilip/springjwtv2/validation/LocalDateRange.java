package com.zobicfilip.springjwtv2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER, })
@Retention(RUNTIME)
@Repeatable(LocalDateRange.List.class)
@Documented
@Constraint(validatedBy = LocalDateRangeValidator.class)
public @interface LocalDateRange {

    String message() default "Date range constraint violation";
    String before() default "";
    String after() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({ FIELD, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        LocalDateRange[] value();
    }
}
