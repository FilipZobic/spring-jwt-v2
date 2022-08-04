package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import static com.zobicfilip.springjwtv2.dto.ConstraintOrder.ValidationSequence;

import com.zobicfilip.springjwtv2.dto.RefreshTokensDTO;
import com.zobicfilip.springjwtv2.dto.TokensCreatedResponseDTO;
import com.zobicfilip.springjwtv2.security.SecurityUtil;
import com.zobicfilip.springjwtv2.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<TokensCreatedResponseDTO> signUp(@Validated(ValidationSequence.class) @RequestBody AuthSignUpDTO signUpDTO, HttpServletResponse response) {

        Pair<String, String> pair = authService.registerUser(signUpDTO);

        TokensCreatedResponseDTO tokenResponse = generateBodyAndPopulateHeader(response, pair.getKey(), pair.getValue());

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokensCreatedResponseDTO> signUp(@Validated @RequestBody RefreshTokensDTO refreshTokensDTO, HttpServletResponse response) throws AccountNotFoundException {

        Pair<String, String> pair = authService.generateTokens(refreshTokensDTO.refreshToken());

        TokensCreatedResponseDTO tokenResponse = generateBodyAndPopulateHeader(response, refreshTokensDTO.refreshToken(), pair.getValue());

        return ResponseEntity.ok(tokenResponse);
    }

    private TokensCreatedResponseDTO generateBodyAndPopulateHeader(HttpServletResponse response, String refreshToken, String accessToken) {
        String bearerToken = "Bearer " + accessToken;
        response.addHeader(SecurityUtil.REFRESH_HEADER_TOKEN_NAME, refreshToken);
        response.addHeader(SecurityUtil.ACCESS_HEADER_TOKEN_NAME, bearerToken);
        return new TokensCreatedResponseDTO(bearerToken, refreshToken);
    }
}
