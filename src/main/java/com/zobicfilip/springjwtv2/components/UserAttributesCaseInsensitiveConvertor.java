package com.zobicfilip.springjwtv2.components;

import com.zobicfilip.springjwtv2.model.UserAttributes;
import org.springframework.core.convert.converter.Converter;

public class UserAttributesCaseInsensitiveConvertor implements Converter<String, UserAttributes> {
    @Override
    public UserAttributes convert(String source) {
        // TODO GlobalException handler is not handling ConversionFailedException.class
        return UserAttributes.of(source.toUpperCase());
    }
}
