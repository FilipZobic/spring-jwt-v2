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
@Repeatable(HasAuthorityToSet.List.class)
@Documented
@Constraint(validatedBy = HasAuthorityToSetValidator.class)
public @interface HasAuthorityToSet {

    String message() default "Forbidden to set for current user";

    String[] authorities();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({ FIELD, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        HasAuthorityToSet[] value();
    }
}
