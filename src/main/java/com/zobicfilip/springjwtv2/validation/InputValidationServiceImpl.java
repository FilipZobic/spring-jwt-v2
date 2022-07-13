package com.zobicfilip.springjwtv2.validation;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InputValidationServiceImpl implements InputValidationService {

    private static final Set<String> ISO_ALPHA2_COUNTRIES = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2);

    private static final Set<String> ISO_ALPHA3_COUNTRIES = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA3);

    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{6,128}$");

    private static final Pattern emailPattern= Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    @Override
    public boolean emailDomainIsRegistered(String email) {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean emailBelongsToTrustedDomain(String email) {
        throw new UnsupportedOperationException();
    }
    // TODO maybe move to DomainValidationService interface

    @Override
    public boolean emailPatternValid(String email) {
        return email != null && emailPattern.matcher(email).find();
    }

    @Override
    public boolean isAlpha2Valid(String alpha2Code) {
        return alpha2Code != null
                && alpha2Code.length() == 2
                && ISO_ALPHA2_COUNTRIES.contains(alpha2Code);
    }

    @Override
    public boolean isAlpha3Valid(String alpha3Code) {
        return alpha3Code != null
                &&  alpha3Code.length() == 3
                && ISO_ALPHA3_COUNTRIES.contains(alpha3Code);
    }

    @Override
    public boolean patternValid(String password) {
        if (password == null) return false;
        return passwordPattern.matcher(password).find();
    }
}
