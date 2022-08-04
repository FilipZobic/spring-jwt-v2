package com.zobicfilip.springjwtv2.integration;

import com.zobicfilip.springjwtv2.dto.*;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ITAuthenticationTest extends Base {

    @Test
    public void registerUser_userIsCreated_whenNotViolatingAnyAuthConstraints() {
        AuthSignUpDTO userDto = createNormalUser("hello", null);

        ResponseEntity<TokensCreatedResponseDTO> responseEntity = testRestTemplate.postForEntity
                (SIGN_UP_URL, userDto, TokensCreatedResponseDTO.class);


        TokensCreatedResponseDTO responseDTO = responseEntity.getBody();

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getAccessToken());
        assertNotNull(responseDTO.getRefreshToken());
        assertTrue(responseDTO.getAccessToken().contains("Bearer "));
        assertFalse(responseDTO.getRefreshToken().contains("Bearer "));

        UUID userId = getIDFromAccessToken(responseDTO.getAccessToken());

        userRepository.deleteById(userId);
    }

    @Test
    public void registerUser_userIsNotCreated_whenViolatingUniqueUserConstraintEmailAndUsername() throws NoSuchFieldException {
        AuthSignUpDTO duplicateUserDto = createNormalUser("", null);
        duplicateUserDto.setUsername(user.getUsername());
        duplicateUserDto.setEmail(user.getEmail());

        ResponseEntity<BadParameterInputDTO> responseEntity = testRestTemplate.postForEntity
                (SIGN_UP_URL, duplicateUserDto, BadParameterInputDTO.class);

        BadParameterInputDTO responseDto = responseEntity.getBody();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(responseDto);
        assertNotNull(responseDto.getValidation());
        // Better this way if for some reason name of field changes IDE picks it up
        assertTrue(responseDto.getValidation().errors().containsKey(AuthSignUpDTO.class.getDeclaredField("username").getName()));
        assertTrue(responseDto.getValidation().errors().containsKey(AuthSignUpDTO.class.getDeclaredField("email").getName()));
    }


    @Test
    public void registerUser_userNotCreated_whenSettingSecureFieldsWhenUnauthenticated() throws NoSuchFieldException {
        AuthSignUpDTO userDto = createNormalUser("hello", "ROLE_ADMIN");
        userDto.setEnabled(false);

        ResponseEntity<BadParameterInputDTO> responseEntity = testRestTemplate.postForEntity
                (SIGN_UP_URL, userDto, BadParameterInputDTO.class);

        BadParameterInputDTO responseDto = responseEntity.getBody();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(responseDto);
        assertNotNull(responseDto.getValidation());
        // Better this way if for some reason name of field changes IDE picks it up
        assertTrue(responseDto.getValidation().errors().containsKey(AuthSignUpDTO.class.getDeclaredField("role").getName()));
        assertTrue(responseDto.getValidation().errors().containsKey(AuthSignUpDTO.class.getDeclaredField("enabled").getName()));
    }
    
    @Test
    public void registerUser_userNotCreated_whenSettingSecureFieldsWhenNoAuthority() throws NoSuchFieldException {
        // Phase 1
        ResponseEntity<TokensCreatedResponseDTO> moderatorResponseEntity = testRestTemplate.postForEntity(SIGN_IN_URL, new AuthSignInDTO(moderator.getEmail(), moderator.getPassword()), TokensCreatedResponseDTO.class);
        TokensCreatedResponseDTO body = moderatorResponseEntity.getBody();
        assertEquals(moderatorResponseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(body);
        assertNotNull(body.getAccessToken());
        assertNotNull(body.getRefreshToken());

        // Phase 2
        AuthSignUpDTO userDto = createNormalUser("hello", "ROLE_ADMIN");
        userDto.setEnabled(false);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Authorization", List.of(body.getAccessToken()));
        ResponseEntity<BadParameterInputDTO> responseEntity = testRestTemplate.exchange(this.rootUri + SIGN_UP_URL, HttpMethod.POST, new HttpEntity<>(userDto, httpHeaders), BadParameterInputDTO.class);

        BadParameterInputDTO responseDto = responseEntity.getBody();
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
        assertNotNull(responseDto);
        assertNotNull(responseDto.getValidation());
        // Better this way if for some reason name of field changes IDE picks it up
        assertTrue(responseDto.getValidation().errors().containsKey(AuthSignUpDTO.class.getDeclaredField("role").getName())); // Moderator does not have permission for role field only ** which belongs to ROLE_ADMIN
        assertFalse(responseDto.getValidation().errors().containsKey(AuthSignUpDTO.class.getDeclaredField("enabled").getName())); // Moderator has permission for enabled field
    }

    @Test
    public void registerUser_adminLoginAndNewUserIsCreated_whenSettingSecureFieldsWithRequiredAuthority()  {
        // Phase 1
        ResponseEntity<TokensCreatedResponseDTO> adminResponseEntity = testRestTemplate.postForEntity(SIGN_IN_URL, new AuthSignInDTO(admin.getEmail(), admin.getPassword()), TokensCreatedResponseDTO.class);
        TokensCreatedResponseDTO body = adminResponseEntity.getBody();
        assertEquals(HttpStatus.OK, adminResponseEntity.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getAccessToken());
        assertNotNull(body.getRefreshToken());

        // Phase 2
        AuthSignUpDTO userDto = createNormalUser("hello", "ROLE_ADMIN");
        userDto.setEnabled(false);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Authorization", List.of(body.getAccessToken()));
        ResponseEntity<TokensCreatedResponseDTO> responseEntity = testRestTemplate.exchange(this.rootUri + SIGN_UP_URL, HttpMethod.POST, new HttpEntity<>(userDto, httpHeaders), TokensCreatedResponseDTO.class);
        TokensCreatedResponseDTO newUserBody = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(newUserBody);
        assertNotNull(newUserBody.getAccessToken());
        assertNotNull(newUserBody.getRefreshToken());

        // Cleanup
        UUID newUserId = getIDFromAccessToken(newUserBody.getAccessToken());
        userRepository.deleteById(newUserId);
    }

    @Test
    public void loginUser_userGetsValidAccessAndRefreshToken_whenCredentialsCorrect() {
        AuthSignInDTO authSignInDTO = new AuthSignInDTO(user.getEmail(), user.getPassword());
        ResponseEntity<TokensCreatedResponseDTO> responseEntity = testRestTemplate.postForEntity(SIGN_IN_URL, authSignInDTO, TokensCreatedResponseDTO.class);
        TokensCreatedResponseDTO body = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getRefreshToken());
        assertNotNull(body.getAccessToken());
        assertFalse(body.getRefreshToken().contains("Bearer "));
        assertTrue(body.getAccessToken().contains("Bearer "));
    }

    @Test
    public void registerUser_userIsNotCreated_whenPassingJWSParserFilterWhileHavingAuthorizationHeaderSetInBadFormat() {
        AuthSignUpDTO userDto = createNormalUser("hello", null);
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of(Strings.concat("dummyValue", UUID.randomUUID().toString())));
        ResponseEntity<GenericExceptionResponseDTO> responseEntity = testRestTemplate.exchange(rootUri + SIGN_UP_URL, HttpMethod.POST, new HttpEntity<>(userDto, headers), GenericExceptionResponseDTO.class);
        GenericExceptionResponseDTO body = responseEntity.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.message());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), body.status());
        assertNotNull(body.timestamp());
        DateTimeFormatter.ISO_DATE_TIME.parse(body.timestamp());
    }

    @Test
    public void loginUser_userUnauthorized_whenPasswordIncorrect() {
        AuthSignInDTO authSignInDTO = new AuthSignInDTO(user.getEmail(), user.getPassword() + "incorrectPassword");
        ResponseEntity<GenericExceptionResponseDTO> responseEntity = testRestTemplate.postForEntity(SIGN_IN_URL, authSignInDTO, GenericExceptionResponseDTO.class);
        GenericExceptionResponseDTO body = responseEntity.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.message());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), body.status());
        assertNotNull(body.timestamp());
        DateTimeFormatter.ISO_DATE_TIME.parse(body.timestamp());
    }

    @Test
    public void loginUser_userUnauthorized_whenUserDoesNotExist() {
        AuthSignInDTO authSignInDTO = new AuthSignInDTO(createNormalUser("doesntExist", null).getEmail(), user.getPassword());
        ResponseEntity<GenericExceptionResponseDTO> responseEntity = testRestTemplate.postForEntity(SIGN_IN_URL, authSignInDTO, GenericExceptionResponseDTO.class);
        GenericExceptionResponseDTO body = responseEntity.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.message());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), body.status());
        assertNotNull(body.timestamp());
        DateTimeFormatter.ISO_DATE_TIME.parse(body.timestamp());
    }

    @Test
    public void refreshToken_getNewAccessTokenAndOldRefreshToken_whenRefreshTokenValid() { // TODO add expired refresh test & access token
        // Phase 1
        ResponseEntity<TokensCreatedResponseDTO> adminResponseEntity = testRestTemplate.postForEntity(SIGN_IN_URL, new AuthSignInDTO(user.getEmail(), user.getPassword()), TokensCreatedResponseDTO.class);
        TokensCreatedResponseDTO body = adminResponseEntity.getBody();
        assertEquals(HttpStatus.OK, adminResponseEntity.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getAccessToken());
        assertNotNull(body.getRefreshToken());

        // Phase 2
        ResponseEntity<TokensCreatedResponseDTO> responseEntity = testRestTemplate.postForEntity(REFRESH_ACCESS_TOKEN_URL, new RefreshTokensDTO(body.getRefreshToken()), TokensCreatedResponseDTO.class);
        TokensCreatedResponseDTO newTokenResponse = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(newTokenResponse);
        assertNotNull(newTokenResponse.getRefreshToken());
        assertNotNull(newTokenResponse.getAccessToken());
        assertEquals(body.getRefreshToken(), newTokenResponse.getRefreshToken());
        assertNotEquals(body.getAccessToken(), newTokenResponse.getAccessToken());
    }
}