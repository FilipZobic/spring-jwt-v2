package com.zobicfilip.springjwtv2.command;

import com.zobicfilip.springjwtv2.dto.AuthRegistrationDTO;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import com.zobicfilip.springjwtv2.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile(value = "dev")
public class CommandLineRunnerRegisterDevExample implements CommandLineRunner {

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        String username = "example";
        String email = "example@gmail.com";
        String password = "123456";
        if (userRepository.findUserByEmailOrUsername(email, username).isEmpty()) {
            AuthRegistrationDTO dto = new AuthRegistrationDTO(username, email, password, "RS", LocalDate.now());
            authService.registerUser(dto);
        }
    }
}
