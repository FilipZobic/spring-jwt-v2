package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.model.User;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) throws AccountNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new AccountNotFoundException("Account with that email/username does not exist."));
    }
}
