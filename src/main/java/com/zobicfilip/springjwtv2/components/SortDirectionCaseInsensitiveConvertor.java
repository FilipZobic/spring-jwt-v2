package com.zobicfilip.springjwtv2.components;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;

public class SortDirectionCaseInsensitiveConvertor implements Converter<String, Sort.Direction> {
    @Override
    public Sort.Direction convert(String source) {
        // TODO GlobalException handler is not handling ConversionFailedException.class
        return Sort.Direction.valueOf(source.toUpperCase());
    }
}
