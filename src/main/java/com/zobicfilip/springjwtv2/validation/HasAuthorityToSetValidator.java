package com.zobicfilip.springjwtv2.validation;

import com.zobicfilip.springjwtv2.exception.SecurityContextAuthenticationNotFoundException;
import com.zobicfilip.springjwtv2.model.ExpandedUserDetails;
import com.zobicfilip.springjwtv2.util.Util;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class HasAuthorityToSetValidator implements ConstraintValidator<HasAuthorityToSet, Object> {

    private Set<String> authorities;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        try {
            ExpandedUserDetails userDetails = Util.getUserDetails();
            Set<String> check = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            boolean anyMatch = authorities.stream().anyMatch(check::contains);
            if (!anyMatch) log.warn("SecurityViolation - Unauthorized user attempted to set secure field");
            return anyMatch;
        } catch (SecurityContextAuthenticationNotFoundException e) {
            log.warn("SecurityViolation - Unauthenticated user attempted to set secure field");
            return false;
        }
    }

    @Override
    public void initialize(HasAuthorityToSet constraintAnnotation) {
        authorities = new HashSet<>(Arrays.asList(constraintAnnotation.authorities()));
    }
}
