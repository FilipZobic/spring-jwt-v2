package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.UserCreationDTO;
import com.zobicfilip.springjwtv2.dto.UserPatchDTO;
import com.zobicfilip.springjwtv2.model.User;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.OperationNotSupportedException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public User findUserByEmail(String email) throws AccountNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new AccountNotFoundException("Account with provided email does not exist."));
    }

    @Override
    public User findUserById(UUID userId) throws AccountNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new AccountNotFoundException("Account with provided ID does not exist."));
    }

    @Override
    public User patchUser(UUID userId, String newPassword, Set<String> newRoles, Boolean enabled) throws AccountNotFoundException { // multiple controller entrances
        throw new UnsupportedOperationException();
    }

    @Override
    public User createUser(UserCreationDTO userCreationDTO) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<User> listUsers(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updatePassword(String newPassword, UUID userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updateEnabled(boolean enabled, UUID userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User updateUser(UserPatchDTO userDto, UUID userId) {
        throw new UnsupportedOperationException();
    }
}
