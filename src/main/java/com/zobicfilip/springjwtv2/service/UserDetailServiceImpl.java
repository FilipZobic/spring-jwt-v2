package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.model.PrincipleUser;
import com.zobicfilip.springjwtv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.zobicfilip.springjwtv2.model.User user = userRepository.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException(""));
        return new PrincipleUser(user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                true,
                true,
                true,
                user.getRolesAndAuthoritiesFormatted()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()),
                user.getId(),
                user.getEmail()
                );
    }
}
