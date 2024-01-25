package com.senior.dreamteam.services;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Roles;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.repositories.AuthoritiesRepository;

import com.senior.dreamteam.repositories.TagProblemRepository;
import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    final TagProblemRepository tagProblemRepository;
    final UserRepository userRepository;
    final AuthoritiesRepository authoritiesRepository;

    final JwtTokenUtil jwtTokenUtil;
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

    public User addUser(String name, String email, String password) {
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

    public User updateUser(String emailFromToken, int id, String authorities, String name, String email) {
        User userFromToken = userRepository.findUserByEmail(emailFromToken).orElseThrow(() -> new DemoGraphqlException("This user not found"));

        boolean isAdmin = userFromToken.getAuthorities()
                .stream()
                .anyMatch(authority -> Roles.ADMIN.toString().equalsIgnoreCase(authority.getAuthority()));
        if(!isAdmin){
            if(!email.equals(userFromToken.getUsername()) || authorities.contains(Roles.ADMIN.toString())){
                log.info("Unauthorized: Cannot Update this User");
                return User.builder().build();
            }
        }
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new DemoGraphqlException("This user not found"));

        List<Authorities> authoritiesList = authoritiesRepository.findAll();
        List<String> roleList = Arrays.asList(authorities.split(", "));

        boolean allRolesCorrect = roleList.stream()
                .allMatch(role -> authoritiesList.stream()
                        .anyMatch(authority -> authority.getName().toString().equals(role)));

        if (allRolesCorrect) {
            List<Authorities> userAuthorities = roleList.stream()
                    .map(role -> authoritiesList.stream()
                            .filter(authority -> authority.getName().toString().equals(role))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            user.setAuthorities(userAuthorities);
        } else {
            log.error("Incorrect update role");
            return User.builder().build();
        }
        user.setName(name);
        user.setEmail(email);
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
