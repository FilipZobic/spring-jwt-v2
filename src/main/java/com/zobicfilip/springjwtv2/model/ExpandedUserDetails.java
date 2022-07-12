package com.zobicfilip.springjwtv2.model;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface ExpandedUserDetails extends UserDetails {

    UUID getUserId();

    String getEmail();
}
