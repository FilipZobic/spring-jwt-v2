package com.zobicfilip.springjwtv2.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zobicfilip.springjwtv2.dto.AuthSignInDTO;
import com.zobicfilip.springjwtv2.dto.GenericExceptionResponseDTO;
import com.zobicfilip.springjwtv2.dto.TokensCreatedResponseDTO;
import com.zobicfilip.springjwtv2.exception.SecurityContextAuthenticationNotFoundException;
import com.zobicfilip.springjwtv2.model.ExpandedUserDetails;
import com.zobicfilip.springjwtv2.service.JWTService;
import com.zobicfilip.springjwtv2.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class UsernameAndPasswordSignInSecurityFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTService tokenService;

    private final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

    public UsernameAndPasswordSignInSecurityFilter(AuthenticationManager authenticationManager, JWTService tokenService) {
        super(authenticationManager);
        this.setFilterProcessesUrl("/api/auth/signIn");
        this.tokenService = tokenService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            log.info("Attempting user authentication");
            AuthSignInDTO authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), AuthSignInDTO.class);
            String email = authenticationRequest.getEmail();
            String password = authenticationRequest.getPassword();
            if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) throw new IOException();
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    password);
            return getAuthenticationManager().authenticate(authentication);
        } catch (IOException io) {
            log.warn("Could not read credentials");
            throw new AuthenticationCredentialsNotFoundException("Could not read credentials");
        }
    }

    @Override // TODO use local
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        log.warn("Failed authenticating user message: {}", failed.getMessage());
        mapper.writeValue(response.getOutputStream(), new GenericExceptionResponseDTO("Authentication failed", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), HttpStatus.UNAUTHORIZED.value()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        ExpandedUserDetails principleUser;
        try {
            principleUser = Util.getUserDetails(authResult);
        } catch (SecurityContextAuthenticationNotFoundException e) {
            log.error("Error security context is not populated after successfulAuthentication");
            throw new RuntimeException(e);
        }
        String email = principleUser.getEmail();
        UUID id = principleUser.getUserId();
        String username = principleUser.getUsername();

        String accessToken = this.tokenService.generateAccessToken(id, authResult.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()),
                username, email);
        String refreshToken = this.tokenService.generateRefreshToken(id);

        String bearerToken = "Bearer " + accessToken;
        TokensCreatedResponseDTO responseBody = new TokensCreatedResponseDTO(bearerToken, refreshToken);;
        response.addHeader(SecurityUtil.ACCESS_HEADER_TOKEN_NAME, bearerToken);
        response.addHeader(SecurityUtil.REFRESH_HEADER_TOKEN_NAME,  refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        log.info("Successfully authenticated user id: {}", id);
        mapper.writeValue(response.getOutputStream(), responseBody);
    }
}
