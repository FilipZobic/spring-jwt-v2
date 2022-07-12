package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import org.apache.commons.lang3.tuple.Pair;

public interface AuthService {
    Pair<String, String> registerUser(AuthSignUpDTO userDto);
}
