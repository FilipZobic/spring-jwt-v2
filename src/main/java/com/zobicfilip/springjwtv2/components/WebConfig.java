package com.zobicfilip.springjwtv2.components;

import com.zobicfilip.springjwtv2.model.UserAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(new GenericEnumCaseInsensitiveConvertor<>(Sort.Direction.class));
//        registry.addConverter(new GenericEnumCaseInsensitiveConvertor<>(UserAttributes.class));
        registry.addConverter(new UserAttributesCaseInsensitiveConvertor());
        registry.addConverter(new SortDirectionCaseInsensitiveConvertor());
    }
}
