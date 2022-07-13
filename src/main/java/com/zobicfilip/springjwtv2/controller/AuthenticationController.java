package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import static com.zobicfilip.springjwtv2.dto.ConstraintOrder.ValidationSequence;
import com.zobicfilip.springjwtv2.security.SecurityUtil;
import com.zobicfilip.springjwtv2.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Validated(ValidationSequence.class) @RequestBody AuthSignUpDTO signUpDTO, HttpServletResponse response) {

        Pair<String, String> pair = Pair.of("1", "2");

        response.addHeader(SecurityUtil.REFRESH_HEADER_TOKEN_NAME, pair.getKey());
        response.addHeader(SecurityUtil.ACCESS_HEADER_TOKEN_NAME, pair.getValue());

        HashMap<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put(SecurityUtil.REFRESH_BODY_TOKEN_NAME, pair.getKey());
        tokenResponse.put(SecurityUtil.ACCESS_BODY_TOKEN_NAME, pair.getValue());

        return ResponseEntity.ok(tokenResponse);
    }
}
