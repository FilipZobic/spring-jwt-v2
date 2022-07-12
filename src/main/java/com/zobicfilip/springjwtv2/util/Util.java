package com.zobicfilip.springjwtv2.util;

import com.zobicfilip.springjwtv2.exception.TokenNotFoundException;
import com.zobicfilip.springjwtv2.model.PrincipleUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class Util {

    private static PrincipleUser getPrincipleUser(Object principal) {
        if (principal instanceof PrincipleUser principleUser) {
            return principleUser;
        } else {
            throw new RuntimeException("Principle is not instance of Principle user");
        }
    }

    public static PrincipleUser getPrincipleUser(Authentication authentication) {
        return getPrincipleUser(authentication.getPrincipal());
    }

    public static PrincipleUser getPrincipleUser() {
        return getPrincipleUser(SecurityContextHolder.getContext().getAuthentication());
    }

    public static String getAccessTokenFromHeader(HttpServletRequest request) throws TokenNotFoundException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) throw new TokenNotFoundException("No authorization header found");
        if (!authorization.contains("Bearer ")) throw new TokenNotFoundException("Invalid token request header format");
        return authorization.substring("Bearer ".length());
    }
}
