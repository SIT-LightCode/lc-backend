package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.services.JwtTokenUtil;
import com.senior.dreamteam.controllers.payload.UserLeaderboardResponse;
import com.senior.dreamteam.controllers.payload.UserResponse;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.services.TagService;
import com.senior.dreamteam.services.TagProblemService;
import com.senior.dreamteam.services.UserService;
import com.senior.dreamteam.validation.GenericValidation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
        userService.getLeaderboard();
        return userService.findAll();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertUser")
    public UserResponse upsertUser(@Argument Integer id, @Argument String authorities, @NotEmpty(message = "name must not be empty") @Argument String name, @Email(message = "email must be correctly format") @Argument String email, @Argument String password, @ContextValue String token) {
        if (id == null) {
            // add user
            // no need la
            return userService.addUser(name, email, password);
        }
        // update user
        genericValidation.validateIsEmptyToken(token);
        String emailFromToken = jwtTokenUtil.getUsernameFromToken(token);
        return userService.updateUser(emailFromToken, id, authorities, name, email);
    }

    @SchemaMapping(typeName = "Mutation", value = "removeUser")
    public String removeUser(@Min(value = 1, message = "id must be greater than or equal to 1") @Argument int id, @NotEmpty(message = "Unauthorized: Cannot remove user") @ContextValue String token) {
        return userService.removeUserById(id, token);
    }

    @SchemaMapping(typeName = "Query", value = "getUserByEmail")
    public UserResponse getUserByEmail(@Email(message = "email must be correctly format") @Argument String email) {
        return userService.getUserByEmail(email);
    }

    @SchemaMapping(typeName = "Query", value = "getLeaderboard")
    public List<UserLeaderboardResponse> getLeaderboard() {
        return userService.getLeaderboard();
    }

}
