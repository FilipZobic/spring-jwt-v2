package com.zobicfilip.springjwtv2.components;

import org.springframework.core.convert.converter.Converter;

@Deprecated()
public class GenericEnumCaseInsensitiveConvertor<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> instance;

    public GenericEnumCaseInsensitiveConvertor(Class<T> instance) {
        this.instance = instance;
    }
    // For some reason this does not work in spring
    @Override
    public T convert(String source) {
        try {
            return T.valueOf(instance, source.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }
}

