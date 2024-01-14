package com.senior.dreamteam.services;

import com.senior.dreamteam.configs.GraphqlExceptionConfig;
import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.exception.DemoGraphqlException;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import com.senior.dreamteam.repositories.TagProblemRepository;
import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.repositories.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    TagProblemRepository tagProblemRepository;

    @Autowired
    UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findAllById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public String argon2Hashing(String stringToHash) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2i, 8, 16);
        return argon2.hash(22, 65536, 1, stringToHash); //97 length of string
    }

    public User addUser(String role, String name, String email, String password) {
        if (!userRepository.findUserByEmail(email).isEmpty()) {
            throw new DemoGraphqlException("This email have already registered");
        }

        User user = new User();
        if (Authorities.ADMIN.name().equalsIgnoreCase(role)) {
            user.setAuthorities(Authorities.ADMIN);
        } else {
            user.setAuthorities(Authorities.USER);
        }
        user.setName(name);
        user.setEmail(email);
        user.setPassword(argon2Hashing(password));
        userRepository.save(user);
        return user;
    }

    public User updateUser(int id, String role, String name, String email, String password) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new DemoGraphqlException("This user not found"));

        if (Authorities.ADMIN.name().equalsIgnoreCase(role)) {
            user.setAuthorities(Authorities.ADMIN);
        } else {
            user.setAuthorities(Authorities.USER);
        }
        user.setName(name);
        user.setEmail(email);
        user.setPassword(argon2Hashing(password));
        userRepository.save(user);
        return user;
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
