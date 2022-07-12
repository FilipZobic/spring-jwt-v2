package com.zobicfilip.springjwtv2.dev.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@Profile(value = "dev")
@RequestMapping("/dev/api/test")
public class TestController {

    @GetMapping("/exception")
    public void exceptionResponse() throws Throwable {
        double dice = Math.random();

        if (dice < 0.33) {
            throw new Throwable();
        } else if (dice < 0.66) {
            throw new Exception();
        } else {
            throw new RuntimeException();
        }
    }

    @GetMapping("/authentication/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> authTestUser() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("message", "Authentication works");
        return ResponseEntity.ok(
                body
        );
    }
    @GetMapping("/authentication/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> authTestAdmin() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("message", "Authentication works");
        return ResponseEntity.ok(
                body
        );
    }
}
