package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.model.User;

import javax.security.auth.login.AccountNotFoundException;

public interface UserService {
    User getUserByEmail(String email) throws AccountNotFoundException;

//    User addRole(UUID userId, String roleTitle);

//    User createUser(UserCreationDTO userCreationDTO);
}
