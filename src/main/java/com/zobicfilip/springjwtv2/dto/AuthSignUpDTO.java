package com.zobicfilip.springjwtv2.dto;


import com.zobicfilip.springjwtv2.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import static com.zobicfilip.springjwtv2.dto.ConstraintOrder.*;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Data
@Validated
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthSignUpDTO {

    @NotBlank(message = "Username is required")
    @Size(message = "Username should be between 5 and 20 characters", min = 5, max = 20, groups = Second.class)
    @UsernameRegex(groups = Third.class)
    @UniqueUsername(ignoreRuleIfSameAsSecurityContextUsername = false, groups = Fourth.class) // checks database needs autowire
    private String username;

    @NotBlank(message = "Email is required")
    @EmailConstraint(onlyRegisteredDomains = false, onlyTrustedDomains = false, groups = Second.class)
    @UniqueEmail(ignoreRuleIfSameAsSecurityContextEmail = false, groups = Fourth.class)
    private String email;

    @NotBlank(message = "Username is required")
    @PasswordPattern(groups = Second.class)
    private String password;

    @NotBlank(message = "Country is required")
    @CountryCode(type = CountryCode.Type.ALPHA_2, message = "Country code must be valid and in alpha2 format example: US", groups = Second.class)
    private String countryTag;

    @NotNull(message = "Date of birth is required")
    @LocalDateRange(message = "Date must not be less then 1900-01-01", after = "1899-12-31", groups = Second.class)
    private LocalDate dateOfBirth;

    @HasAuthorityToSet(authorities = {"**", "USER_**", "USER_ALL_ENABLED_U", "USER_ALL_U"})
    private Boolean enabled;

//    TODO @RolesCanGrantRoles(role = "ROLE_MODERATOR", canSetValue = {"ROLE_USER", "ROLE_MODERATOR"}) or something like that
    @HasAuthorityToSet(authorities = {"**"}) // needed check otherwise moderator could set admin we could work around this
    private String role;
}
