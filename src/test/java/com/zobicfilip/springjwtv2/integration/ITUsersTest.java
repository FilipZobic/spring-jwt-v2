package com.zobicfilip.springjwtv2.integration;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import com.zobicfilip.springjwtv2.dto.GenericExceptionResponseDTO;
import com.zobicfilip.springjwtv2.dto.TokensCreatedResponseDTO;
import com.zobicfilip.springjwtv2.dto.UserPaginationDTO;
import com.zobicfilip.springjwtv2.integration.dto.PageResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ITUsersTest extends BaseRest {
    protected static final String USER_PAGINATION_URL = "/api/users";

    public static Stream<Arguments> credentialAndPermissions() {
        return Stream.of(
          Arguments.of(
                  "ROLE_USER", false, HttpStatus.OK
          ),
            Arguments.of(
                    "ROLE_ADMIN", true, HttpStatus.OK
            ),
                Arguments.of(
                        "ROLE_USER", true, HttpStatus.FORBIDDEN
                ),
                Arguments.of(
                        "ROLE_MODERATOR", true, HttpStatus.OK
                )
        );
    }
    @Test
    public void userPagination_willReturnFirst50UsersWithOptionalDetails_whenUserWithAuthoritiesSetsFlag() {
        TokensCreatedResponseDTO tokenDto = loginUser(user.getEmail(), user.getPassword());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Authorization", List.of(tokenDto.getAccessToken()));

        // can't use Page interface because of responseEntity
        Map<String, Object> params = new HashMap<>();
        params.put("expandedDetails", true);

        String uriString = UriComponentsBuilder.fromHttpUrl(this.rootUri + USER_PAGINATION_URL).queryParam("expandedDetails", "{expandedDetails}").build().toUriString();

        ResponseEntity<GenericExceptionResponseDTO> paginationResponse = testRestTemplate.exchange(uriString,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                GenericExceptionResponseDTO.class, params);
        GenericExceptionResponseDTO body = paginationResponse.getBody();
        Assertions.assertEquals(HttpStatus.FORBIDDEN, paginationResponse.getStatusCode());
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.status());
        Assertions.assertNotNull(body.timestamp());
        Assertions.assertNotNull(body.message());
    }

    @ParameterizedTest()
    @MethodSource("credentialAndPermissions")
    public void userPagination_contractIsValidWillReturnFirst50UsersWithOptionalDetails_whenUserWithAuthoritiesSetsFlag(String role, boolean willAskForDetails, HttpStatus expectedStatus) {

        AuthSignUpDTO user = this.getUser(role).orElseThrow(RuntimeException::new);

        TokensCreatedResponseDTO tokenDto = loginUser(user.getEmail(), user.getPassword());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Authorization", List.of(tokenDto.getAccessToken()));

        // can't use Page interface because of responseEntity
        Map<String, Object> params = new HashMap<>();
        params.put("expandedDetails", willAskForDetails);

        String uriString = UriComponentsBuilder.fromHttpUrl(this.rootUri + USER_PAGINATION_URL).queryParam("expandedDetails", "{expandedDetails}").build().toUriString();

        ResponseEntity<PageResponseDTO<UserPaginationDTO>> paginationResponse = testRestTemplate.exchange
                (uriString,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    new ParameterizedTypeReference<PageResponseDTO<UserPaginationDTO>>() {}, params);

        if (expectedStatus != HttpStatus.OK) {
            Assertions.assertEquals(expectedStatus, paginationResponse.getStatusCode());
            return;
        }

        PageResponseDTO<UserPaginationDTO> body = paginationResponse.getBody();
        Assertions.assertEquals(HttpStatus.OK, paginationResponse.getStatusCode());
        Assertions.assertNotNull(body);
        var violations = validator.validate(body);
        Assertions.assertEquals(0, violations.size());
        Assertions.assertEquals(50, body.size());
        Assertions.assertEquals(1,body.totalPages());
        Assertions.assertEquals(body.numberOfElements(),body.numberOfElements());
        UserPaginationDTO.UserPaginationDetailsDTO details = body.content().get(0).details();
        Assertions.assertTrue((details != null && willAskForDetails) || (details == null && !willAskForDetails));
    }
}
