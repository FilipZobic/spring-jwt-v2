package com.zobicfilip.springjwtv2.integration;

import com.zobicfilip.springjwtv2.dto.AuthSignInDTO;
import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import com.zobicfilip.springjwtv2.dto.TokensCreatedResponseDTO;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import com.zobicfilip.springjwtv2.service.AuthService;
import com.zobicfilip.springjwtv2.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.transaction.Transactional;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.annotation.PostConstruct;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseRest extends Base {

    protected AuthSignUpDTO user;
    protected AuthSignUpDTO moderator;
    protected AuthSignUpDTO admin;
    protected List<AuthSignUpDTO> userList;
    protected String rootUri;
    protected TestRestTemplate testRestTemplate;
    protected Validator validator;

    public TokensCreatedResponseDTO loginUser(String email, String password) {
        AuthSignInDTO authSignInDTO = new AuthSignInDTO(email, password);
        ResponseEntity<TokensCreatedResponseDTO> responseEntity = testRestTemplate.postForEntity(SIGN_IN_URL, authSignInDTO, TokensCreatedResponseDTO.class);
        return responseEntity.getBody();
    }

    @LocalServerPort protected int localPort;
    @Autowired protected AuthService authService;
    @Autowired protected JWTService<Jws<Claims>> jwtService;
    @Autowired protected UserRepository userRepository;
    protected static final String PASSWORD = "aaaA1!";
    protected static final String SIGN_UP_URL = "/api/auth/signUp";
    protected static final String SIGN_IN_URL = "/api/auth/signIn";
    protected static final String REFRESH_ACCESS_TOKEN_URL = "/api/auth/refresh";

    public AuthSignUpDTO insertUserInDB(String containsInName, String role) {
        AuthSignUpDTO toReturn = createNormalUser(containsInName, role);
        authService.registerUser(toReturn);
        return toReturn;
    }

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

        userList = List.of(user, moderator, admin);
    }

    protected Optional<AuthSignUpDTO> getUser(String role) {
        return userList.stream().filter(u -> u.getRole().equalsIgnoreCase(role)).findFirst();
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
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//            requestFactory.setOutputStreaming(false);
//            return requestFactory;
            HttpClient httpClient = HttpClientBuilder.create().build();
            return new HttpComponentsClientHttpRequestFactory(httpClient);
        });
        this.testRestTemplate = new TestRestTemplate(restTemplateBuilder, null, null); // Note restTemplateBuilder after each method returns a new instance instead of modifying original -_-
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        } catch (Throwable e) {
            throw new RuntimeException("Failed creating validator");
        }
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
