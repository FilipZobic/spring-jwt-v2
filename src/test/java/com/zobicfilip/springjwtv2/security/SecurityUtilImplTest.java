package com.zobicfilip.springjwtv2.security;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLData;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SecurityUtilImplTest {
    SecurityUtilImpl securityUtil;
    String accessTokenMinutesAlive = "15";
    String refreshTokenMinutesAlive = "10080";

    @BeforeEach
    void beforeEach() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String secret = UUID.randomUUID().toString();
        Environment environment = Mockito.mock(Environment.class);
        when(environment.resolvePlaceholders("${security.access.alive.minutes}"))
                .thenReturn(accessTokenMinutesAlive);
        when(environment.resolvePlaceholders("${security.refresh.alive.minutes}"))
                .thenReturn(refreshTokenMinutesAlive);

        this.securityUtil = new SecurityUtilImpl(environment);
        Field secretKey = securityUtil.getClass().getDeclaredField("secretKey");
        secretKey.setAccessible(true);
        secretKey.set(securityUtil, secret);
        secretKey.setAccessible(false);

        Method postConstruct = securityUtil.getClass().getDeclaredMethod("postConstruct");
        postConstruct.setAccessible(true);
        postConstruct.invoke(securityUtil);
        postConstruct.setAccessible(false);
    }

    @Test
    void getKey_isNotNull_keyNotNull_whenStateValid() {
        Assertions.assertNotNull(securityUtil.getKey());
    }

    @Test
    void getKeyWithPassword_keyNotNullOrNotEqualToKeyWithoutPassword_whenStateIsValid() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        SecretKey normalKey = securityUtil.getKey();
        SecretKey keyEncodedWithPassword = securityUtil.getKey(encodedPassword);
        Assertions.assertNotEquals(normalKey, keyEncodedWithPassword);
    }

    @Test
    void getAccessTokenLifespan_durationSameAsDefined_whenStateValid() {
        long accessTokenLifespan = securityUtil.getAccessTokenLifespan();
        Duration expected = Duration.of(Long.parseLong(accessTokenMinutesAlive), ChronoUnit.MINUTES);
        Duration actual = Duration.of(accessTokenLifespan, ChronoUnit.MILLIS);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getRefreshTokenLifespan_durationSameAsDefined_whenStateValid() {
        long refreshTokenLifespan = securityUtil.getRefreshTokenLifespan();
        Duration expected = Duration.of(Long.parseLong(refreshTokenMinutesAlive), ChronoUnit.MINUTES);
        Duration actual = Duration.of(refreshTokenLifespan, ChronoUnit.MILLIS);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getCutoffDate_isNotNullAndIsRefreshTokenTimesTwoDurationInThePast_whenNotDefined() {
        Date cutoffDate = securityUtil.getCutoffDate();
        long durationMinutes = Long.parseLong(refreshTokenMinutesAlive)*2;
        Date expectedDate = new Date(System.currentTimeMillis() - durationMinutes * 60 * 1000);
        expectedDate = DateUtils.setHours(expectedDate, 0);
        expectedDate = DateUtils.setMinutes(expectedDate, 0);
        expectedDate = DateUtils.setSeconds(expectedDate, 0);
        expectedDate = DateUtils.setMilliseconds(expectedDate, 0);
        Assertions.assertEquals(expectedDate, cutoffDate);
    }
}