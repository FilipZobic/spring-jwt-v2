package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.AuthRegistrationDTO;
import com.zobicfilip.springjwtv2.model.User;

public interface AuthService {

    User registerUser(AuthRegistrationDTO userDto);
}
