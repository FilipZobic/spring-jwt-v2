package com.zobicfilip.springjwtv2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PasswordEncoderConfiguration {

    @Bean
    protected PasswordEncoder delegatingPasswordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        String bcryptIdentifier = "bcrypt";
        encoders.put(bcryptIdentifier, new BCryptPasswordEncoder());

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(bcryptIdentifier, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(bcryptIdentifier));

        return passwordEncoder;
    }
}
