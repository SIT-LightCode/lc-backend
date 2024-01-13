package com.senior.dreamteam.services;

import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.entities.Problem;
import com.senior.dreamteam.entities.Tag;
import com.senior.dreamteam.entities.TagProblem;
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
