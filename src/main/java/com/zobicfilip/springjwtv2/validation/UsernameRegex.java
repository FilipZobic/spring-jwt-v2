package com.zobicfilip.springjwtv2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER, })
@Retention(RUNTIME)
@Repeatable(UsernameRegex.List.class)
@Documented
@Constraint(validatedBy = UsernameRegexValidator.class)
public @interface UsernameRegex {

    String message() default "Username not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({ FIELD, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        UsernameRegex[] value();
    }
}
