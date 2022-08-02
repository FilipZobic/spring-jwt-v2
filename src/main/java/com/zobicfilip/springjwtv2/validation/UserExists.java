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
@Repeatable(UserExists.List.class)
@Documented
@Constraint(validatedBy = UserExistsValidator.class)
public @interface UserExists {
    // TODO add support username, password, id

    String message() default "User does not exist";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({ FIELD, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        UserExists[] value();
    }
}
