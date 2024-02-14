package com.senior.dreamteam.services;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.controllers.payload.UserResponse;
import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Roles;
import com.senior.dreamteam.entities.Submission;
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

    public List<UserResponse> findAll() {
        return mapListUserToListUserResponse(userRepository.findAll());
    }

    public Optional<User> findAllById(int id) {
        return userRepository.findById(id);
    }

    public UserResponse getUserByEmail(String email) {
        return mapUserToUserResponse(userRepository.findUserByEmail(email).orElseThrow(() -> new DemoGraphqlException("This user not found")));
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new DemoGraphqlException("This user not found"));
    }

    public UserResponse addUser(String name, String email, String password) {
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

        return mapUserToUserResponse(userRepository.save(newUser));
    }

    public UserResponse updateUser(String emailFromToken, int id, String authorities, String name, String email) {
        User userFromToken = userRepository.findUserByEmail(emailFromToken).orElseThrow(() -> new DemoGraphqlException("This user not found"));
        User userFromId = userRepository.findUserById(id).orElseThrow(() -> new DemoGraphqlException("This user not found"));

        boolean isAdmin = userFromToken.getAuthorities()
                .stream()
                .anyMatch(authority -> Roles.ADMIN.toString().equalsIgnoreCase(authority.getAuthority()));
        if (!isAdmin) {
            if (!userFromId.getUsername().equals(userFromToken.getUsername()) || authorities.contains(Roles.ADMIN.toString())) {
                log.info("Unauthorized: Cannot Update this User");
                return UserResponse.builder().build();
            }
        }

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
            userFromId.setAuthorities(userAuthorities);
        } else {
            log.error("Incorrect update role");
            return UserResponse.builder().build();
        }
        userFromId.setName(name);
        userFromId.setEmail(email);
        return mapUserToUserResponse(userRepository.save(userFromId));
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

    public UserResponse mapUserToUserResponse(User user) {
        try {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setName(user.getName());
            userResponse.setEmail(user.getEmail());
            userResponse.setAuthorities(user.getSimpleAuthorities());
            userResponse.setScore(user.getSubmission().stream().mapToInt(Submission::getScore).sum());
            if (user.getSubmission() != null) {
                userResponse.setScore(user.getSubmission().stream().mapToInt(Submission::getScore).sum());
            }
            return userResponse;
        } catch (Exception e) {
            log.error("Could not Map User to UserResponse: " + e.getMessage());
            return UserResponse.builder().build();
        }
    }

    public List<UserResponse> mapListUserToListUserResponse(List<User> users) {
        return users.stream().map(this::mapUserToUserResponse).collect(Collectors.toList());
    }
}
