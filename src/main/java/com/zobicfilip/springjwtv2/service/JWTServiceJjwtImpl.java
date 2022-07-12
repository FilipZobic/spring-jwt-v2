package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.model.PrincipleUser;
import com.zobicfilip.springjwtv2.security.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JWTServiceJjwtImpl implements JWTService<Jws<Claims>> {

    private final SecurityUtil securityUtil;

    @Override
    public String generateAccessToken(UUID userId, Collection<String> roles, String username, String email){
        return Jwts.builder()
                .setIssuer("domain")
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("email", email)
                .claim("permissions", (roles instanceof Set<String> ? roles : new HashSet<>(roles)))
                .signWith(securityUtil.getKey())
                .setExpiration(new Date(System.currentTimeMillis() + securityUtil.getAccessTokenLifespan()))
                .compact();
    }

    @Override
    public String generateRefreshToken(UUID userId){
        return Jwts.builder()
                .setIssuer("domain")
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setSubject(userId.toString())
                .signWith(securityUtil.getKey())
                .setExpiration(new Date(System.currentTimeMillis() + securityUtil.getRefreshTokenLifespan()))
                .compact();
    }

    @Override
    public Pair<String, String> generateRefreshAndAccessToken(UUID userId, Collection<String> roles, String username, String email){
        return Pair.of(
                generateRefreshToken(userId),
                generateAccessToken(userId, roles, username, email)
        );
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(securityUtil.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getIssuedAt()
                    .after(securityUtil.getCutoffDate());
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(securityUtil.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getIssuedAt()
                    .after(securityUtil.getCutoffDate());
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public Jws<Claims> readRefreshToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(securityUtil.getKey())
                .build()
                .parseClaimsJws(token);
    }

    @Override
    public Jws<Claims> readAccessToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(securityUtil.getKey())
                .build()
                .parseClaimsJws(token);
    }

    @Override
    public PrincipleUser parseClaimsToAuthenticationPrinciple(Jws<Claims> claims) {
        Claims body = claims.getBody();
        String username = body.get("username", String.class); // TODO check this out more how its done
        String email = body.get("email", String.class);
        Object permissions = body.get("permissions", Object.class);
        Set<String> authorities = null;
        if (permissions instanceof List<?>) {
            authorities = ((List<String>) permissions).stream().collect(Collectors.toSet());
        } else if (permissions instanceof Set<?>) {
            authorities = (Set<String>) permissions;
        } else {
            throw new RuntimeException("Claim permissions stored in unsupported data structure");
        }
        String id = body.getSubject();
        return new PrincipleUser(username,
                "null",
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()),
                id,
                email);
    }
}
