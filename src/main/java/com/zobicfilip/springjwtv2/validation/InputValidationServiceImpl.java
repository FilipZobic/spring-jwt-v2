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

    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{6,}$");

    @Override
    public boolean emailDomainExists(String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean emailPatternValid(String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean emailBelongsToTrustedDomain(String email) {
        throw new UnsupportedOperationException();
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
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.find();
    }
}
