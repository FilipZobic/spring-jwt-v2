package com.zobicfilip.springjwtv2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO extends AuthSignUpDTO {

    private String role;
}
