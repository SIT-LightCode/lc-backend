package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.controllers.payload.UserResponse;
import com.senior.dreamteam.entities.*;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.services.TagService;
import com.senior.dreamteam.services.TagProblemService;
import com.senior.dreamteam.services.UserService;
import com.senior.dreamteam.validation.GenericValidation;
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
    GenericValidation genericValidation = new GenericValidation();

    @SchemaMapping(typeName = "Query", value = "getUser")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertUser")
    public UserResponse upsertUser(@Argument Integer id, @Argument String authorities, @Argument String name, @Argument String email, @Argument String password, @ContextValue String token) {
        if (id == null) {
            // add user
            return userService.addUser(name, email, password);
        }
        // update user
        genericValidation.validateIsEmptyToken(token);
        String emailFromToken = jwtTokenUtil.getUsernameFromToken(token);
        return userService.updateUser(emailFromToken, id, authorities, name, email);
    }

    @SchemaMapping(typeName = "Mutation", value = "removeUser")
    public String removeUser(@Argument int id) {
        return userService.removeUserById(id);
    }

    @SchemaMapping(typeName = "Query", value = "getUserByEmail")
    public UserResponse getUserByEmail(@Argument String email) {
        return userService.getUserByEmail(email);
    }

}
