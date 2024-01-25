package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.entities.*;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.services.TagService;
import com.senior.dreamteam.services.TagProblemService;
import com.senior.dreamteam.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    final TagProblemService tagProblemService;
    final TagService tagService;
    final ProblemService problemService;
    final UserService userService;

    final JwtTokenUtil jwtTokenUtil;
    @SchemaMapping(typeName = "Query", value = "getUser")
    public List<User> findAll() {
        return userService.findAll();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertUser")
    public User upsertUser(@Argument int id, @Argument String authorities, @Argument String name, @Argument String email, @Argument String password) {
        if (id == 0) {
            // add user
            return userService.addUser(name, email, password);
        }
        // update user
//        String emailFromToken = jwtTokenUtil.getUsernameFromToken(token);
        return User.builder().build();
//        return userService.updateUser(emailFromToken, id, authorities, name, email);
    }

    @SchemaMapping(typeName = "Mutation", value = "removeUser")
    public String removeUser(@Argument int id) {
        return userService.removeUserById(id);
    }

}
