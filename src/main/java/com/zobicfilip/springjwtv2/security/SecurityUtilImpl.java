package com.zobicfilip.springjwtv2.security;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class SecurityUtilImpl implements SecurityUtil {
    private final Environment environment;
    @Value("${security.token.secret}")
    private String secretKey;

    @Getter private long accessTokenLifespan;
    @Getter private long refreshTokenLifespan;
    @Getter private SecretKey key;

    @Getter private Date cutoffDate;

    @PostConstruct
    @SneakyThrows
    private void postConstruct() {
        this.key = Keys.hmacShaKeyFor(this.secretKey.getBytes());
        this.accessTokenLifespan = Long.parseLong(environment.resolvePlaceholders("${security.access.alive.minutes}")) * 1000 * 60;
        this.refreshTokenLifespan = Long.parseLong(environment.resolvePlaceholders("${security.refresh.alive.minutes}")) * 1000 * 60;
//        this.secretKey = environment.resolvePlaceholders("${security.jwt.secret}");

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.cutoffDate = dateFormatter.parse(environment.resolvePlaceholders("${security.token.cutoffDate}"));
        } catch (IllegalArgumentException e) {
            this.cutoffDate = dateFormatter.parse(
                    dateFormatter.format(
                            new Date(System.currentTimeMillis() - refreshTokenLifespan*2)
                    )
            );
        }
    }

    @Override
    public SecretKey getKey(String password) {
        return Keys.hmacShaKeyFor(
                Strings.concat(this.secretKey, password).getBytes()
        );
    }
}
