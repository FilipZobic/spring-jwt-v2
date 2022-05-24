package com.zobicfilip.springjwtv2.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.apache.commons.lang3.tuple.Pair;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Set;
import java.util.UUID;

public interface JWTService {
    String generateAccessToken(UUID userId, Set<String> roles, String username);

    String generateRefreshToken(UUID userId);

    Pair<String, String> generateRefreshAndAccessToken(UUID userId, Set<String> roles, String username);

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

    Jws<Claims> readRefreshToken(String token) throws JwtException;

    Jws<Claims> readAccessToken(String token) throws JwtException;
}
