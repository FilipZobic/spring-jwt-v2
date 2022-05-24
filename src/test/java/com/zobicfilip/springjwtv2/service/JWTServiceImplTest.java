package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.security.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JWTServiceImplTest {

    static SecurityUtil securityUtil;
    static String secret;
    static UUID userId;
    static Set<String> userPermissions;
    static String username;
    
    static JWTServiceJjwtImpl primaryJwtService;

    @BeforeAll
    static void beforeAll() {
        secret = UUID.randomUUID().toString();
        securityUtil = mockSecurityUtil(secret);
        userId = UUID.randomUUID();
        userPermissions = new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        username = "user";
        primaryJwtService = new JWTServiceJjwtImpl(securityUtil);
    }

    static SecurityUtil mockSecurityUtil(String secret) {
        SecurityUtil util = Mockito.mock(SecurityUtil.class);
        long lifeSpanAccess = 10L;
        when(util.getAccessTokenLifespan())
                .thenReturn(lifeSpanAccess * 1000 * 60);
        when(util.getCutoffDate())
                .thenReturn(new Date(System.currentTimeMillis()/1000-lifeSpanAccess*1000*60));
        when(util.getKey())
                .thenReturn(Keys.hmacShaKeyFor(secret.getBytes()));
        long lifeSpanRefresh = 1080L;
        when(util.getRefreshTokenLifespan())
                .thenReturn(lifeSpanRefresh * 1000 * 60);
        return util;
    }

    @Test
    void generateAccessToken_tokenIsValid_whenPassedExpirationDateAndNonExpiredAndValidSignatureAndDataIsConsistent() {
        // TOKEN GENERATION>
        
        String accessToken = primaryJwtService.generateAccessToken(userId, userPermissions, username);
        // <TOKEN GENERATION
        // ASSERTIONS>
        boolean tokenIsValid = primaryJwtService.validateAccessToken(accessToken);
        Assertions.assertTrue(tokenIsValid);

        Jws<Claims> claimsJws = primaryJwtService.readAccessToken(accessToken);
        JwsHeader header = claimsJws.getHeader();
        Claims body = claimsJws.getBody();

        assertInstanceOf(ArrayList.class, body.get("permissions"));
        ArrayList tokenPermissions = (ArrayList) body.get("permissions");
        Object[] tokenPermissionArray = tokenPermissions.stream().sorted().toArray();
        Object[] userPermissionArray = userPermissions.stream().sorted().toArray();
        Assertions.assertArrayEquals(userPermissionArray, tokenPermissionArray);

        Assertions.assertEquals(username, body.get("username"));
        // <ASSERTIONS
    }

    @Test
    void generateAccessToken_tokenIsNotValid_whenMismatchSignature() {
        SecurityUtil secondarySecurityUtil = mockSecurityUtil(secret + "99929201");
        JWTServiceJjwtImpl secondaryJwtService = new JWTServiceJjwtImpl(secondarySecurityUtil);
        
        String secondaryAccessToken = secondaryJwtService.generateAccessToken(userId, userPermissions, username);

        boolean isSecondaryTokenInPrimaryServiceValid = primaryJwtService.validateAccessToken(secondaryAccessToken);
        Assertions.assertFalse(isSecondaryTokenInPrimaryServiceValid);
        assertThrowsExactly(SignatureException.class, () -> primaryJwtService.readAccessToken(secondaryAccessToken));
    }

    @Test
    void generateRefreshToken() {
    }

    @Test
    void generateRefreshAndAccessToken_refreshAndAccessTokensAreValid_whenGeneratedCorrectly() {
        Pair<String, String> refreshAndAccessToken = primaryJwtService.generateRefreshAndAccessToken(userId, userPermissions, username);
        Assertions.assertTrue(
                primaryJwtService.validateRefreshToken(refreshAndAccessToken.getLeft())
        );
        Assertions.assertTrue(
                primaryJwtService.validateAccessToken(refreshAndAccessToken.getRight())
        );
    }

    @Test
    void validateAccessToken_accessTokenIsValid_whenTokenIsGeneratedCorrectly() {
        String primaryAccessToken = primaryJwtService.generateAccessToken(userId, userPermissions, username);
        boolean actual = primaryJwtService.validateAccessToken(primaryAccessToken);
        Assertions.assertTrue(actual);
    }

    @Test
    void validateRefreshToken_accessTokenIsNotValid_whenTokenIsGeneratedCorrectlyAndIsTamperedWith() {
        String tamperedToken = primaryJwtService.generateAccessToken(userId, userPermissions, username) + "2";
        boolean actual = primaryJwtService.validateAccessToken(tamperedToken);
        Assertions.assertFalse(actual);
    }
}