package com.zobicfilip.springjwtv2.util;

import com.zobicfilip.springjwtv2.exception.SecurityContextAuthenticationNotFoundException;
import com.zobicfilip.springjwtv2.exception.TokenNotFoundInHeaderException;
import com.zobicfilip.springjwtv2.model.ExpandedUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class Util {

    private static ExpandedUserDetails getUserDetails(Object principal) throws SecurityContextAuthenticationNotFoundException  {
        if (principal instanceof ExpandedUserDetails principleUser) {
            return principleUser;
        } else {
            throw new SecurityContextAuthenticationNotFoundException("Principle is not instance of SecurityContextPrinciple");
        }
    }

    public static ExpandedUserDetails getUserDetails(Authentication authentication) throws SecurityContextAuthenticationNotFoundException  {
        if (authentication == null) throw new SecurityContextAuthenticationNotFoundException("Authentication is null");
        return getUserDetails(authentication.getPrincipal());
    }

    public static ExpandedUserDetails getUserDetails() throws SecurityContextAuthenticationNotFoundException {
        return getUserDetails(SecurityContextHolder.getContext().getAuthentication());
    }

    public static String getAccessTokenFromHeader(HttpServletRequest request) throws TokenNotFoundInHeaderException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) throw new TokenNotFoundInHeaderException("No authorization header found");
        if (!authorization.contains("Bearer ")) throw new TokenNotFoundInHeaderException("Invalid token request header format");
        return authorization.substring("Bearer ".length());
    }
}
