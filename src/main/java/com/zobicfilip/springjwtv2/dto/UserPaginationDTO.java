package com.zobicfilip.springjwtv2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserPaginationDTO (UUID id,
                                 String username,
                                 String email,
                                 String countryTag,
                                 LocalDate dateOfBirth,
                                 Map<String, Set<String>> rolesAndAuthorities,
                                 @JsonInclude(JsonInclude.Include.NON_NULL) UserPaginationDetailsDTO details) {

    public record UserPaginationDetailsDTO (LocalDateTime createdAt, LocalDateTime updatedAt, Boolean enabled) {}
}
