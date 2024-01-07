package com.senior.dreamteam.user.controller;

import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.problem.service.ProblemService;
import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.service.TagService;
import com.senior.dreamteam.tagproblem.entity.TagProblem;
import com.senior.dreamteam.tagproblem.service.TagProblemService;
import com.senior.dreamteam.user.service.UserService;
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

    @SchemaMapping(typeName = "Query", value = "getTagProblem")
    public List<TagProblem> findAll() {
        return tagProblemService.findAll();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertUser")
    public TagProblem upsertUser(@Argument int id, @Argument int tagId, @Argument int problemId) {
        Tag tag = tagService.findAllById(tagId).get();
        Problem problem = problemService.findAllById(problemId).get();
        return tagProblemService.upsertTagProblem(new TagProblem(id, tag, problem));
    }

    @SchemaMapping(typeName = "Mutation", value = "removeUser")
    public String removeUser(@Argument int userId) {
        return userService.removeUserById(userId);
    }

}
