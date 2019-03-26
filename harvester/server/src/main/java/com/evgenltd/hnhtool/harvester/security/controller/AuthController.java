package com.evgenltd.hnhtool.harvester.security.controller;

import com.evgenltd.hnhtool.harvester.security.entity.Credentials;
import com.evgenltd.hnhtools.common.Assert;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 26-03-2019 18:59
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    public AuthController(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody final Credentials credentials, final HttpServletRequest request) {

        final String username = credentials.getUsername();
        final String password = credentials.getPassword();
        final String currentUser = getCurrentUser();
        if (!Assert.isEmpty(currentUser) && !Objects.equals(currentUser, username)) {
            logout(request);
        }

        if (!Assert.isEmpty(currentUser) && Objects.equals(username, currentUser)) {
            return ResponseEntity.ok().build();
        }

        final Authentication authInput = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication authOutput = authenticationManager.authenticate(authInput);
        SecurityContextHolder.getContext().setAuthentication(authOutput);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletRequest request) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, null, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test() {
        return ResponseEntity.ok().build();
    }

    private String getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof String)) {
            return null;
        }

        return (String) principal;
    }

}
