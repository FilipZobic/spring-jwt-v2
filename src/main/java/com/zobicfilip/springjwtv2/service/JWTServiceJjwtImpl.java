package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.security.SecurityUtil;
import com.zobicfilip.springjwtv2.security.SecurityUtilImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JWTServiceJjwtImpl implements JWTService {

    private final SecurityUtil securityUtil;

    @Override
    public String generateAccessToken(UUID userId, Set<String> roles, String username){
        return Jwts.builder()
                .setIssuer("domain")
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("permissions", roles)
                .signWith(securityUtil.getKey())
                .setExpiration(new Date(System.currentTimeMillis()/1000 + securityUtil.getAccessTokenLifespan()))
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
                .setExpiration(new Date(System.currentTimeMillis()/1000 + securityUtil.getRefreshTokenLifespan()))
                .compact();
    }

    @Override
    public Pair<String, String> generateRefreshAndAccessToken(UUID userId, Set<String> roles, String username){
        return Pair.of(
                generateRefreshToken(userId),
                generateAccessToken(userId, roles, username)
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
}
