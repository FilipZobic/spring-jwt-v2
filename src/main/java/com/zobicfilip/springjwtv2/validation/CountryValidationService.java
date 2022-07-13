package com.zobicfilip.springjwtv2.validation;

public interface CountryValidationService {

    boolean isAlpha2Valid(String alpha2Code);

    boolean isAlpha3Valid(String alpha3Code);
}
