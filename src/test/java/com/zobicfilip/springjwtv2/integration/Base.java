package com.zobicfilip.springjwtv2.integration;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import com.zobicfilip.springjwtv2.service.AuthService;
import com.zobicfilip.springjwtv2.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.annotation.PostConstruct;


import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class Base {

    protected AuthSignUpDTO user;
    protected AuthSignUpDTO moderator;
    protected AuthSignUpDTO admin;
    protected String rootUri;
    protected TestRestTemplate testRestTemplate;
    @LocalServerPort protected int localPort;
    @Autowired protected AuthService authService;
    @Autowired protected JWTService<Jws<Claims>> jwtService;
    @Autowired protected UserRepository userRepository;
    protected static final String PASSWORD = "aaaA1!";
    protected static final String SIGN_UP_URL = "/api/auth/signUp";
    protected static final String SIGN_IN_URL = "/api/auth/signIn";
    protected static final String REFRESH_ACCESS_TOKEN_URL = "/api/auth/refresh";

    @BeforeEach
    @Transactional
    public void registerUserModeratorAdmin() {
        user = createNormalUser("user", "ROLE_USER");
        moderator = createNormalUser("moderator", "ROLE_MODERATOR");
        moderator.setEmail("moderator" + UUID.randomUUID().toString().substring(0,6) + "@gmail.com");
        admin = createNormalUser("admin", "ROLE_ADMIN");

        authService.registerUser(user);
        authService.registerUser(moderator);
        authService.registerUser(admin);
    }

    @AfterEach
    @Transactional
    public void deleteUserModeratorAdmin() {
        userRepository.deleteByUsername(admin.getUsername());
        userRepository.deleteByUsername(moderator.getUsername());
        userRepository.deleteByUsername(user.getUsername());
    }


    @PostConstruct
    public void initialize() {
        rootUri = "http://localhost:" + localPort;
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder = restTemplateBuilder.rootUri(rootUri);
        restTemplateBuilder = restTemplateBuilder.requestFactory(()
                -> {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setOutputStreaming(false);
            return requestFactory;
        });
        this.testRestTemplate = new TestRestTemplate(restTemplateBuilder, null, null); // Note restTemplateBuilder after each method returns a new instance -_-
    }



    protected static AuthSignUpDTO createNormalUser(String containInName, String role) {
        String aRV = UUID.randomUUID().toString().substring(0, 6);
        String uniqueValue = Strings.concat(containInName, aRV) + "test";
        return AuthSignUpDTO.builder()
                .countryTag("US")
                .dateOfBirth(LocalDate.of(1990, 7, 1))
                .email( uniqueValue + "@gmail.com")
                .password(PASSWORD)
                .username(uniqueValue)
                .role(role)
                .build();
    }

    protected UUID getIDFromAccessToken(String accessToken) {
        return UUID.fromString(
                jwtService.readAccessToken(accessToken.replace("Bearer", "")
                ).getBody().getSubject()
        );
    }
}
