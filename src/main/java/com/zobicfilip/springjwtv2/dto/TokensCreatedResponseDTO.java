package com.zobicfilip.springjwtv2.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokensCreatedResponseDTO {
    private String accessToken;
    private String refreshToken;
}
