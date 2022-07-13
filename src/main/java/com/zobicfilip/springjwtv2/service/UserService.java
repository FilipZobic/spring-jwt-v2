package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.UserCreationDTO;
import com.zobicfilip.springjwtv2.dto.UserPatchDTO;
import com.zobicfilip.springjwtv2.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    User patchUser(UUID userId, String newPassword, Set<String> newRoles, Boolean enabled) throws AccountNotFoundException;

    User findUserByEmail(String email) throws AccountNotFoundException; // FOR AUTH

//    User addRole(UUID userId, String roleTitle);

    User createUser(UserCreationDTO userCreationDTO); // ADMIN MODERATOR

    Page<User> listUsers(Pageable pageable); // ADMIN MODERATOR

    boolean updatePassword(String newPassword, UUID userId); // PATCH // USER(self)&ADMIN // MODERATOR

    boolean updateEnabled(boolean enabled, UUID userId); // PATCH // ADMIN // MODERATOR

    User updateUser(UserPatchDTO userDto, UUID userId); // PATCH // USER(self)&ADMIN

    User findUserById(UUID userId) throws AccountNotFoundException;

    // controller should have button that checks security context id, permissions and parameter id
}
