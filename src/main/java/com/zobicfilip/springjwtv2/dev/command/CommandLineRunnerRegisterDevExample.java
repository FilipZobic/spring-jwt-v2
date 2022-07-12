package com.zobicfilip.springjwtv2.dev.command;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import com.zobicfilip.springjwtv2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile(value = "dev")
@RequiredArgsConstructor
public class CommandLineRunnerRegisterDevExample implements CommandLineRunner {

    private final AuthService authService;

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        String username = "example";
        String email = "example@gmail.com";
            String password = "123456";
        if (userRepository.findUserByEmailOrUsername(email, username).isEmpty()) {
            AuthSignUpDTO dto = new AuthSignUpDTO(username, email, password, "RS", LocalDate.now());
            authService.registerUser(dto);
        }
    }
}
