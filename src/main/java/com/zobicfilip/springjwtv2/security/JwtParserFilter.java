package com.zobicfilip.springjwtv2.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zobicfilip.springjwtv2.dto.GenericExceptionResponseDTO;
import com.zobicfilip.springjwtv2.model.PrincipleUser;
import com.zobicfilip.springjwtv2.service.JWTService;
import com.zobicfilip.springjwtv2.util.Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class JwtParserFilter extends OncePerRequestFilter {

    private final JWTService<Jws<Claims>> jwtService;

    public JwtParserFilter(JWTService<Jws<Claims>> jwtService) {
        this.jwtService = jwtService;
    }

    private final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

    private final static Set<String> ignoreEndpoints = Set.of("/api/auth/signUp", "/api/auth/signIn", "/api/auth/refresh");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String endpoint = request.getRequestURI();
        if ((ignoreEndpoints.contains(endpoint) || SecurityContextHolder.getContext().getAuthentication() != null)
                && request.getHeader("Authorization") == null) {
            filterChain.doFilter(request, response);
        } else {
            try {
                String accessToken = Util.getAccessTokenFromHeader(request);
                Jws<Claims> claims = jwtService.readAccessToken(accessToken);

                PrincipleUser principleUser = jwtService.parseClaimsToAuthenticationPrinciple(claims);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principleUser, null, principleUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                log.info("SecurityContext UserId: [{}]", principleUser.getUserId().toString());
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                String message = "Unauthorized";
                if (e instanceof ExpiredJwtException expiredJwtException) {
                    message = "Token is expired";
                } else if (e instanceof MalformedJwtException malformedJwtException) {
                    message = "Token is invalid";
                }
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                log.warn(message);
                mapper.writeValue(response.getOutputStream(), new GenericExceptionResponseDTO(message, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), HttpStatus.UNAUTHORIZED.value()));
            }
        }
    }
}
