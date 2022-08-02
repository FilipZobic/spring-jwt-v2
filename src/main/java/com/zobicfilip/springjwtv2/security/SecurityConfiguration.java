package com.zobicfilip.springjwtv2.security;

import com.zobicfilip.springjwtv2.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, JWTService<Jws<Claims>> jwtService) throws Exception {
        http
                .authorizeHttpRequests(authz -> {
                    authz.antMatchers("/api/auth/signIn", "/api/auth/signUp", "/api/auth/refresh").permitAll()
                            .anyRequest().authenticated();
                })
                .csrf().disable()
                .cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilter(new UsernameAndPasswordSignInSecurityFilter(authenticationManager, jwtService))
                .addFilterBefore(new JwtParserFilter(jwtService), UsernameAndPasswordSignInSecurityFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(Environment environment) {

        return (web) -> {
            if (Arrays.asList(environment.getActiveProfiles())
                    .contains("dev")) {
                web.ignoring().antMatchers("/dev/api/test/exception");
            }
        };
    }

    // OLD WAY
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailServiceImpl userDetailsService) {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder);
//        provider.setUserDetailsService(userDetailsService);
//        return provider;
//    }

//    @Override
//    public void setDaoAuthenticationProvider(AuthenticationManagerBuilder builder,DaoAuthenticationProvider authenticationProvider) throws Exception {
//        builder.authenticationProvider(authenticationProvider);
//    }

//    @Override
//    public MethodSecurityExpressionHandler createExpressionHandler() {
//        // we can set expressions here
//        return new MethodSecurityExpressionHandlerImpl();
//    }

    // NEW WAY
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public static MethodSecurityExpressionHandler createExpressionHandler() {
        // we can set expressions here
        return new MethodSecurityExpressionHandlerImpl();
    }
}
