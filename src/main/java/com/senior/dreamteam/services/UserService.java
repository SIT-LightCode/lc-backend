package com.senior.dreamteam.services;

import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Roles;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.repositories.AuthoritiesRepository;

import com.senior.dreamteam.repositories.TagProblemRepository;
import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    TagProblemRepository tagProblemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthoritiesRepository authoritiesRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findAllById(int id) {
        return userRepository.findById(id);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new DemoGraphqlException("This user not found"));
    }

    public User addUser(String role, String name, String email, String password) {
        if (!userRepository.findUserByEmail(email).isEmpty()) {
            throw new DemoGraphqlException("This email have already registered");
        }
        Authorities authorities = getAuthorityByName(Roles.USER);
        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .authorities(List.of(authorities))
                .build();

        return userRepository.save(newUser);
    }

    public User updateUser(int id, String role, String name, String email, String password) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new DemoGraphqlException("This user not found"));

        if ("admin".equalsIgnoreCase(role)) {
            user.setAuthorities(List.of(getAuthorityByName(Roles.ADMIN)));
        } else {
            user.setAuthorities(List.of(getAuthorityByName(Roles.USER)));
        }
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public Authorities getAuthorityByName(Roles name) {
        return authoritiesRepository.findByName(name);
    }



    public String removeUserById(int id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                userRepository.deleteById(id);
                return "User removed successfully";
            } else {
                return "User not found with ID: " + id;
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }

}
