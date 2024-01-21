package com.senior.dreamteam.controllers;

import com.senior.dreamteam.entities.*;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.services.TagService;
import com.senior.dreamteam.services.TagProblemService;
import com.senior.dreamteam.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class UserController {
    @Autowired
    TagProblemService tagProblemService;

    @Autowired
    TagService tagService;

    @Autowired
    ProblemService problemService;

    @Autowired
    UserService userService;

    @SchemaMapping(typeName = "Query", value = "getUser")
    public List<User> findAll() {
        return userService.findAll();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertUser")
    public User upsertUser(@Argument int id, @Argument String role, @Argument String name, @Argument String email, @Argument String password) {
        if (id == 0) {
            // add user
            return userService.addUser(role, name, email, password);
        }
        // update user
        return userService.updateUser(id, role, name, email, password);
    }

    @SchemaMapping(typeName = "Mutation", value = "removeUser")
    public String removeUser(@Argument int id) {
        return userService.removeUserById(id);
    }

}
