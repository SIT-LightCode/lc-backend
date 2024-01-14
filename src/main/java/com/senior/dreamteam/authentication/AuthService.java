package com.senior.dreamteam.authentication;

import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.repositories.UserRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

    public User authenticateUser(String email, String password) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new DemoGraphqlException("This user not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            // Password matches, user is authenticated
            return user;
        }

        // Invalid credentials
        throw new DemoGraphqlException("Invalid email or password");
    }
}