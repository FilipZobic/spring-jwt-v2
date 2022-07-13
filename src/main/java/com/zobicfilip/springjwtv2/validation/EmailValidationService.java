package com.zobicfilip.springjwtv2.validation;

public interface EmailValidationService {

    boolean emailDomainExists(String email);

    boolean emailPatternValid(String email);

    boolean emailBelongsToTrustedDomain(String email);
}
