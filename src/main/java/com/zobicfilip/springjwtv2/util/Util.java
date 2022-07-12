package com.zobicfilip.springjwtv2.util;

import com.zobicfilip.springjwtv2.exception.TokenNotFoundInHeaderException;
import com.zobicfilip.springjwtv2.model.ExpandedUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class Util {

    private static ExpandedUserDetails getUserDetails(Object principal) {
        if (principal instanceof ExpandedUserDetails principleUser) {
            return principleUser;
        } else {
            throw new RuntimeException("Principle is not instance of SecurityContextPrinciple");
        }
    }

    public static ExpandedUserDetails getUserDetails(Authentication authentication) {
        return getUserDetails(authentication.getPrincipal());
    }

    public static ExpandedUserDetails getUserDetails() {
        return getUserDetails(SecurityContextHolder.getContext().getAuthentication());
    }

    public static String getAccessTokenFromHeader(HttpServletRequest request) throws TokenNotFoundInHeaderException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) throw new TokenNotFoundInHeaderException("No authorization header found");
        if (!authorization.contains("Bearer ")) throw new TokenNotFoundInHeaderException("Invalid token request header format");
        return authorization.substring("Bearer ".length());
    }
}
