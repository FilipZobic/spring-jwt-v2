package com.zobicfilip.springjwtv2.security;

import com.zobicfilip.springjwtv2.model.SecurityProperties;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

class SecurityUtilImplTest {
    SecurityUtilImpl securityUtil;
    long accessTokenLifespan = 15L;
    long refreshTokenLifespan = 10080L;

    @BeforeEach
    void beforeEach() throws ParseException {
        String secret = UUID.randomUUID().toString();
        SecurityProperties properties = new SecurityProperties(accessTokenLifespan, refreshTokenLifespan, secret, null);

        this.securityUtil = new SecurityUtilImpl(properties);
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
        long accessTokenLifespan = securityUtil.getAccessLifespan();
        Duration expected = Duration.of(this.accessTokenLifespan, ChronoUnit.MINUTES);
        Duration actual = Duration.of(accessTokenLifespan, ChronoUnit.MILLIS);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getRefreshTokenLifespan_durationSameAsDefined_whenStateValid() {
        long refreshTokenLifespan = securityUtil.getRefreshLifespan();
        Duration expected = Duration.of(this.refreshTokenLifespan, ChronoUnit.MINUTES);
        Duration actual = Duration.of(refreshTokenLifespan, ChronoUnit.MILLIS);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getCutoffDate_isNotNullAndIsRefreshTokenTimesTwoDurationInThePast_whenNotDefined() {
        Date cutoffDate = securityUtil.getCutoffDate();
        long durationMinutes = refreshTokenLifespan*2;
        Date expectedDate = new Date(System.currentTimeMillis() - durationMinutes * 60 * 1000);
        expectedDate = DateUtils.setHours(expectedDate, 0);
        expectedDate = DateUtils.setMinutes(expectedDate, 0);
        expectedDate = DateUtils.setSeconds(expectedDate, 0);
        expectedDate = DateUtils.setMilliseconds(expectedDate, 0);
        Assertions.assertEquals(expectedDate, cutoffDate);
    }
}