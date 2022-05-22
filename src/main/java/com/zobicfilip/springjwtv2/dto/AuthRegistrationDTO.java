package com.zobicfilip.springjwtv2.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegistrationDTO {

    // TODO add validation

    private String username;

    private String email;

    private String password;

    private String countryTag; // alpha2

    private LocalDate dateOfBirth;
}
