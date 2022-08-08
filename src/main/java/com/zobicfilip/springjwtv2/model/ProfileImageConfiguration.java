package com.zobicfilip.springjwtv2.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Set;

@ConfigurationProperties("user.profile.image")
@ConfigurationPropertiesScan
public record ProfileImageConfiguration (Set<Integer> allowedWidths, Set<Integer> allowedHeights, long maxSize) {
}
