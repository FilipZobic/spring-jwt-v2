package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.AuthRegistrationDTO;
import com.zobicfilip.springjwtv2.exception.RegErrorType;
import com.zobicfilip.springjwtv2.exception.RegistrationFailedException;
import com.zobicfilip.springjwtv2.keys.RoleUserCompKey;
import com.zobicfilip.springjwtv2.model.Role;
import com.zobicfilip.springjwtv2.model.RoleUser;
import com.zobicfilip.springjwtv2.model.User;
import com.zobicfilip.springjwtv2.repository.RoleRepository;
import com.zobicfilip.springjwtv2.repository.RoleUserRepository;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final RoleUserRepository roleUserRepository;

    @Override
    @Transactional
    public User registerUser(AuthRegistrationDTO userDto) {

        userRepository.findUserByEmailOrUsername(userDto.getEmail(), userDto.getUsername())
                .ifPresent(usr -> { throw new RegistrationFailedException(userDto, usr, RegErrorType.ALREADY_EXISTS); });

        Role userRole = roleRepository
                .findRoleByTitle("ROLE_USER")
                .orElseThrow(() -> new InternalError("ROLE_USER does not exist but should"));

//        UUID userId = UUID.randomUUID();

        User user = User.builder()
//                .id(userId)
//                .roles(List.of(new RoleUser(roleUserCompKey)))
                .countryTag(userDto.getCountryTag())
                .dateOfBirth(userDto.getDateOfBirth())
                .email(userDto.getEmail())
                .enabled(true)
                .username(userDto.getUsername())
                .password(userDto.getPassword()) // TODO encrypt password
                .build();
        user = this.userRepository.save(user);
        RoleUserCompKey roleUserCompKey = new RoleUserCompKey(userRole.getTitle(), user.getId());
        RoleUser roleUser = roleUserRepository.save(new RoleUser(roleUserCompKey));
        roleUser = roleUserRepository.save(roleUser);
        user.addRole(roleUser);
//        user = this.userRepository.save(user);
        return user;
    }
}
