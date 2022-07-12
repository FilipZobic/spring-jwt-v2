package com.zobicfilip.springjwtv2.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class PrincipleUser extends User implements ExpandedUserDetails {

    @Getter private final UUID userId;

    @Getter private final String email;

    public PrincipleUser(String username, String password, Collection<? extends GrantedAuthority> authorities, UUID userId, String email) {
        super(username, password, authorities);
        this.userId = userId;
        this.email = email;
    }

    public PrincipleUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String userId, String email) {
        super(username, password, authorities);
        this.userId = UUID.fromString(userId);
        this.email = email;
    }

    public PrincipleUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, UUID userId, String email) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.email = email;
    }
}
