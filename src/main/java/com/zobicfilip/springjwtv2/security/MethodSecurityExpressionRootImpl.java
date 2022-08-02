package com.zobicfilip.springjwtv2.security;

import com.zobicfilip.springjwtv2.exception.SecurityContextAuthenticationNotFoundException;
import com.zobicfilip.springjwtv2.model.ExpandedUserDetails;
import com.zobicfilip.springjwtv2.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class MethodSecurityExpressionRootImpl extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;

    public MethodSecurityExpressionRootImpl(Authentication authentication) {
        super(authentication);
    }

    public boolean isSelf(UUID id) {
        try {
            return Util.getUserDetails().getUserId().equals(id);
        } catch (SecurityContextAuthenticationNotFoundException e) {
            log.error("SecurityContext should exist at this point", e);
            return false;
        }
    }

    public boolean hasAnyAuthorityCustom(String rootAuth, String ...authorities) {
        ExpandedUserDetails userDetails;
        try {
            userDetails = Util.getUserDetails();
        } catch (SecurityContextAuthenticationNotFoundException e) {
            log.error("SecurityContext should exist at this point", e);
            return false;
        }

        Set<String> expectedAuthorities = Set.of(authorities);
        Set<String> secContextAuthorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        return secContextAuthorities.contains("**")
                || secContextAuthorities.contains(rootAuth)
                || expectedAuthorities.stream().anyMatch(secContextAuthorities::contains);
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }
}
