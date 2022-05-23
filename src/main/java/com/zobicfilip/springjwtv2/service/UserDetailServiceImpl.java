package com.zobicfilip.springjwtv2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            com.zobicfilip.springjwtv2.model.User user = userService.getUserByEmail(username);
            return new User(user.getUsername(),
                    user.getPassword(),
                    user.getEnabled(),
                    true,
                    true,
                    true,
                    user.getRolesInStringSet()
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet())
                    );
        } catch (AccountNotFoundException exception) {
            throw new UsernameNotFoundException(exception.getMessage());
        }
    }
}
