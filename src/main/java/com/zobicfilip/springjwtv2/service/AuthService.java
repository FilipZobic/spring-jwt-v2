package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import org.apache.commons.lang3.tuple.Pair;

public interface AuthService {
    Pair<String, String> registerUser(AuthSignUpDTO userDto);

    // reset password
    // checkIf disabled(its disabled if missed password more then 10 times) on login
    // checkIf password reset in redis

//    Pair<String, String> loginUser(AuthLoginDTO userDto);
}
