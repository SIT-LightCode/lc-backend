package com.senior.dreamteam.authentication.controllers;


import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.authentication.payload.JwtRequest;
import com.senior.dreamteam.authentication.payload.JwtResponse;
import com.senior.dreamteam.authentication.payload.LoginRequest;
import com.senior.dreamteam.entities.Token;
import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    final AuthenticationManager authenticationManager;
    final UserService userService;
    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    final JwtTokenUtil jwtTokenUtil;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest login) {
        return createToken(loginWithEmail(login.email(), login.password()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody JwtRequest token) throws Exception {
        try {
            jwtTokenUtil.revokeToken(token.token());
            return ResponseEntity.ok("token revoked");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot revoke this token");
        }
    }

    private ResponseEntity<JwtResponse> createToken(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username or password is incorrect");
        }
        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this account is locked");
        }
        JwtResponse jwtResponse = new JwtResponse(jwtTokenUtil.generateJWT(user, 604800L));
        return ResponseEntity.ok(jwtResponse);
    }

    private User loginWithEmail(String email, String password) {
        User user = userService.findUserByEmail(email);
        return loginProcess(email, password, user);
    }

    private User loginProcess(String email, String password, User user) {
        if (!checkAuth(user, password)) {
            return null;
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            log.info("user {} successfully logged in", email);
        } catch (Exception e) {
            log.error("login process error: {}", e.getMessage());
        }
        return user;
    }

    private boolean checkAuth(User user, String password) {
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

}
