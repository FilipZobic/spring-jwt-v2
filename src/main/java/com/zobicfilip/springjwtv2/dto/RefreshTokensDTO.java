package com.zobicfilip.springjwtv2.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record RefreshTokensDTO(@NotBlank(message = "Refresh token is required") String refreshToken) {
}
