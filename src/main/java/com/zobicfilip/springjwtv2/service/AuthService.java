package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.AuthLoginDTO;
import com.zobicfilip.springjwtv2.dto.AuthRegistrationDTO;
import org.apache.commons.lang3.tuple.Pair;

public interface AuthService {
    Pair<String, String> registerUser(AuthRegistrationDTO userDto);

//    Pair<String, String> loginUser(AuthLoginDTO userDto);
}
