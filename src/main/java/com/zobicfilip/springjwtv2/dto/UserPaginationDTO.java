package com.zobicfilip.springjwtv2.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserPaginationDTO (UUID id,
                                 String username,
                                 String email,
                                 String countryTag,
                                 LocalDate dateOfBirth,
                                 Map<String, Set<String>> rolesAndAuthorities) { }
