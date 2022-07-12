package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.model.PrincipleUser;
import io.jsonwebtoken.JwtException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.UUID;

public interface JWTService <T>{
    String generateAccessToken(UUID userId, Collection<String> roles, String username, String email);

    String generateRefreshToken(UUID userId);

    Pair<String, String> generateRefreshAndAccessToken(UUID userId, Collection<String> roles, String username, String email);

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

    T readRefreshToken(String token) throws JwtException;

    T readAccessToken(String token) throws JwtException;

    PrincipleUser parseClaimsToAuthenticationPrinciple(T claims);
}
