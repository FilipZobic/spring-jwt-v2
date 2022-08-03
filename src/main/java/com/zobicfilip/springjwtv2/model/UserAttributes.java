package com.zobicfilip.springjwtv2.model;

public enum UserAttributes {
    EMAIL("email"), USERNAME("username"), COUNTRY_TAG("countryTag");

    public final String queryName;
    UserAttributes(String queryName) {
        this.queryName = queryName;
    }

    public static UserAttributes of(String value) {
        if (value.equalsIgnoreCase(COUNTRY_TAG.queryName)) {
            return COUNTRY_TAG;
        }
        return UserAttributes.valueOf(value);
    }
}
