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

//    @NotBlank(message = "Username is required")
//    @Size(message = "Username should be between 5 and 20 characters", min = 5, max = 20, groups = Second.class)
//    @UsernameRegex(groups = Third.class) //"^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$"
//    @UniqueUsername(groups = Fourth.class) // checks database needs autowire
    private String username;

//    @NotBlank(message = "Email is required")
//    @EmailRegex(grops = Second.class)
//    @ValidDNS(groups = Third.class)
//    @UniqueEmail(groups = Fourth.class)
    private String email;

//    @NotBlank(message = "Username is required")
//    @PasswordRegex(groups = Second.class)
    private String password;

    @NotBlank(message = "Country is required")
    @CountryCode(type = CountryCode.Type.ALPHA_2, message = "Country code must be valid and in alpha2 format example: US", groups = Second.class)
    private String countryTag;

    @NotNull(message = "Date of birth is required")
    @LocalDateRange(message = "Date must not be less then 1900-01-01", after = "1899-12-31", groups = Second.class)
    private LocalDate dateOfBirth;
}
