package com.zobicfilip.springjwtv2.security;

import com.zobicfilip.springjwtv2.model.SecurityProperties;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class SecurityUtilImpl implements SecurityUtil {

    private final String secretKey;
    @Getter private final long accessLifespan;
    @Getter private final long refreshLifespan;
    @Getter private final SecretKey key;

    @Getter private final Date cutoffDate;

    @Autowired
    public SecurityUtilImpl(SecurityProperties properties) throws ParseException {
        this.secretKey = properties.secret();
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes());
        this.accessLifespan = properties.accessLifespan() * 1000 * 60;
        this.refreshLifespan = properties.refreshLifespan() * 1000 * 60;

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isBlank(properties.cutoffDate())) {
            this.cutoffDate = dateFormatter.parse(
                    dateFormatter.format(new Date(System.currentTimeMillis() - refreshLifespan *2))
            );
            return;
        }
        this.cutoffDate = dateFormatter.parse(properties.cutoffDate());
    }

    @Override
    public SecretKey getKey(String password) {
        return Keys.hmacShaKeyFor(
                Strings.concat(this.secretKey, password).getBytes()
        );
    }
}
