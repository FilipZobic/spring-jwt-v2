package com.zobicfilip.springjwtv2.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties("security.token")
@ConfigurationPropertiesScan
public final record SecurityProperties (long accessLifespan, long refreshLifespan, String secret, String cutoffDate) {
}
