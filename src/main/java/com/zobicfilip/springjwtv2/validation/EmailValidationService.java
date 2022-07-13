package com.zobicfilip.springjwtv2.validation;

public interface EmailValidationService {

    boolean emailDomainIsRegistered(String email);

    boolean emailPatternValid(String email);

    boolean emailBelongsToTrustedDomain(String email);
}
