package com.senior.dreamteam.controllers;

import com.senior.dreamteam.entities.Problem;
import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.entities.Tag;
import com.senior.dreamteam.services.TagService;
import com.senior.dreamteam.entities.TagProblem;
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

//    @SchemaMapping(typeName = "Mutation", value = "upsertUser")
//    public User upsertUser(@Argument int id, @Argument int tagId, @Argument int problemId) {
//        Tag tag = tagService.findAllById(tagId).get();
//        Problem problem = problemService.findAllById(problemId).get();
//        return tagProblemService.upsertTagProblem(new TagProblem(id, tag, problem));
//    }

    @SchemaMapping(typeName = "Mutation", value = "removeUser")
    public String removeUser(@Argument int userId) {
        return userService.removeUserById(userId);
    }

}
