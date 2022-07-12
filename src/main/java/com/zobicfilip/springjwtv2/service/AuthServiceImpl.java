package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
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
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final RoleUserRepository roleUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    @Override
    @SneakyThrows
    @Transactional
    public Pair<String, String> registerUser(AuthSignUpDTO userDto) {

        userRepository.findUserByEmailOrUsername(userDto.getEmail(), userDto.getUsername())
                .ifPresent(usr -> { throw new RegistrationFailedException(userDto, usr, RegErrorType.ALREADY_EXISTS); });

        Role userRole = roleRepository
                .findRoleByTitle("ROLE_USER")
                .orElseThrow(() -> new InternalError("ROLE_USER does not exist but should"));

//        UUID userId = UUID.randomUUID();

        // TODO fix this
        User user = User.builder()
//                .id(userId)
//                .roles(List.of(new RoleUser(roleUserCompKey))) THIS does not work there could be a workaround but i did not find it
                .countryTag(userDto.getCountryTag())
                .dateOfBirth(userDto.getDateOfBirth())
                .email(userDto.getEmail())
                .enabled(true)
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();
        user = this.userRepository.save(user);
        RoleUserCompKey roleUserCompKey = new RoleUserCompKey(userRole.getTitle(), user.getId());
        RoleUser roleUser = roleUserRepository.save(new RoleUser(roleUserCompKey));

        // # 3Way TODO find a better way for it not to be null
        /// ????
        // # 2Way
        roleUser = roleUserRepository.save(roleUser);
        user.addRole(roleUser);
        roleUser.setUser(user);
        roleUser.setRole(userRole);
        // # 1Way
//        user.addRole(roleUser);
//        user = this.userRepository.save(user);
        return jwtService.generateRefreshAndAccessToken(user.getId(),
                user.getRolesAndAuthoritiesFormatted(),
                user.getUsername(), user.getEmail());
    }
}
