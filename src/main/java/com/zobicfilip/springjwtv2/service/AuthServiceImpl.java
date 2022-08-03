package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import com.zobicfilip.springjwtv2.keys.RoleUserCompKey;
import com.zobicfilip.springjwtv2.model.Role;
import com.zobicfilip.springjwtv2.model.RoleUser;
import com.zobicfilip.springjwtv2.model.User;
import com.zobicfilip.springjwtv2.repository.RoleRepository;
import com.zobicfilip.springjwtv2.repository.RoleUserRepository;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final UserService userService;

    private final RoleRepository roleRepository;

    private final RoleUserRepository roleUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService<Jws<Claims>> jwtService;

    @Override
    @SneakyThrows
    @Transactional
    public Pair<String, String> registerUser(/*@Validated */AuthSignUpDTO userDto) {
        // DB Constraints are checked by JPA Validator for all but role

        log.info("Attempting to register user");

        String selectedRole = StringUtils.isNotEmpty(userDto.getRole()) ? userDto.getRole() : "ROLE_USER";
        Role userRole = roleRepository
                .findRoleByTitle(selectedRole)
                .orElseThrow(() -> new InternalError(String.format("Role %s does not exist",selectedRole)));

        // Might not be worth wasted time
//        UUID userId = UUID.randomUUID();

        // Creates User
        User user = User.builder()
                .countryTag(userDto.getCountryTag())
                .dateOfBirth(userDto.getDateOfBirth())
                .email(userDto.getEmail())
                .enabled((userDto.getEnabled() != null ? userDto.getEnabled() : true))
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();
        user = this.userRepository.save(user);

        // Create role permission
        RoleUserCompKey roleUserCompKey = new RoleUserCompKey(userRole.getTitle(), user.getId());
        RoleUser roleUser = roleUserRepository.save(new RoleUser(roleUserCompKey));
        roleUser = roleUserRepository.save(roleUser);

        // Modify the dto so we receive both
        user.addRole(roleUser);
        roleUser.setUser(user);
        roleUser.setRole(userRole);
        log.info("Successfully registered user id: {}", user.getId());
        // TODO fix this to get rid of warning
        return jwtService.generateRefreshAndAccessToken(user.getId(),
                user.getRolesAndAuthoritiesFormatted(),
                user.getUsername(), user.getEmail());
    }

    @Override
    public Pair<String, String> generateTokens(String refreshToken) throws AccountNotFoundException {
        // TODO password reset redis cache check and or verification token check
        UUID userId = UUID.fromString(jwtService.readRefreshToken(refreshToken).getBody().getSubject());
        User user = userService.findUserById(userId);
        return jwtService.generateRefreshAndAccessToken(userId, user.getRolesAndAuthoritiesFormatted(), user.getUsername(), user.getEmail());
    }
}
