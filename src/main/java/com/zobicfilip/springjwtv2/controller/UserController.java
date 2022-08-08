package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.dto.UserPaginationDTO;
import com.zobicfilip.springjwtv2.model.*;
import com.zobicfilip.springjwtv2.service.UserService;
import com.zobicfilip.springjwtv2.validation.CountryCode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyAuthorityCustom('USER_**', 'USER_ALL_SHARED_R', 'USER_ALL_R') && isRequestingDetailsAndHasAnyAuthorityForDetails(#expandedDetails,'USER_**', 'USER_ALL_R')")
    @GetMapping
    public ResponseEntity<Page<UserPaginationDTO>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @Min (value = 0) @Max (value = 100) @RequestParam(required = false, defaultValue = "50") int size,
            @RequestParam(required = false) String email, //contains
            @RequestParam(required = false) String username, // contains
            @CountryCode (type = CountryCode.Type.ALPHA_2, message = "Country does not exist")
                @RequestParam(required = false) String countryTag, // match exactly
            @RequestParam(required = true, defaultValue = "ASC") Sort.Direction order,
            @RequestParam (required = false, defaultValue = "") Set<UserAttributes> sortBy,
            @RequestParam (required = false, defaultValue = "false") boolean expandedDetails
    ) {
        System.out.println("Enter");
        PageRequest pageRequest;
        if (sortBy.isEmpty()) {
            pageRequest = PageRequest.of(
                    page,
                    size);
        } else {
            pageRequest = PageRequest.of(
                    page,
                    size,
                    order,
                    sortBy.stream()
                            .map(a -> a.queryName)
                            .toArray(String[]::new));
        }

        Page<User> userPage = userService.listUsers(pageRequest, username, email, countryTag);
        Page<UserPaginationDTO> response = new PageImpl<>(userPage.getContent().stream().map(a ->
                    UserPaginationDTO.builder()
                            .id(a.getId())
                            .countryTag(a.getCountryTag())
                            .email(a.getEmail())
                            .username(a.getUsername())
                            .dateOfBirth(a.getDateOfBirth())
                            .details( expandedDetails ? new UserPaginationDTO.UserPaginationDetailsDTO(a.getCreatedAt(), a.getUpdatedAt(), a.getEnabled()) : null )
                            .rolesAndAuthorities(
                                    a.getRoles().stream()
                                            .map(RoleUser::getRole)
                                            .collect(Collectors.toMap(
                                                    Role::getTitle,
                                                    role -> role.getPermissions()
                                                            .stream().map(Permission::getTitle)
                                                            .collect(Collectors.toSet())
                                                    ))
                            )
                            .build()).collect(Collectors.toList()),
                pageRequest, userPage.getTotalElements());
        return ResponseEntity.ok(response);
    }
}
