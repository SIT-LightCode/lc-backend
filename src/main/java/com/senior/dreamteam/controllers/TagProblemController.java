package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.entities.TagProblem;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.services.TagProblemService;
import com.senior.dreamteam.entities.Problem;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.entities.Tag;
import com.senior.dreamteam.services.TagService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class TagProblemController {
    @Autowired
    TagProblemService tagProblemService;

    @Autowired
    TagService tagService;

    @Autowired
    ProblemService problemService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @SchemaMapping(typeName = "Query", value = "getTagProblem")
    public List<TagProblem> findAll() {
        return tagProblemService.findAll();
    }

    @SchemaMapping(typeName = "Query", value = "getTagProblemById")
    public TagProblem findAllById(@Min(value = 1, message = "id must be greater than or equal to 1") @Argument int id) {
        return tagProblemService.findAllById(id).get();
    }

    @SchemaMapping(typeName = "Query", value = "getTagProblemByTagId")
    public List<TagProblem> findTagProblemsByTagId(@Min(value = 1, message = "id must be greater than or equal to 1") @Argument int id) {
        return tagProblemService.findTagProblemsByTagId(id);
    }


    @SchemaMapping(typeName = "Mutation", value = "upsertTagProblem")
    public TagProblem upsertTagProblem(@Argument int id, @Min(value = 1, message = "tag id must be greater than or equal to 1") @Argument int tagId, @Min(value = 1, message = "problem id must be greater than or equal to 1") @Argument int problemId, @NotEmpty(message = "Token cannot be empty") @ContextValue String token) throws Exception {
        if (!jwtTokenUtil.isAdminToken(token)) {
            log.info("Unauthorized: Cannot Update this lesson");
            throw new DemoGraphqlException("Unauthorized: Cannot Update this lesson");
        }
        Tag tag = tagService.findAllById(tagId).get();
        Problem problem = problemService.findAllById(problemId).get();
        return tagProblemService.upsertTagProblem(new TagProblem(id, tag, problem));
    }

    @SchemaMapping(typeName = "Mutation", value = "removeTagProblem")
    public String removeTagProblem(@Min(value = 1, message = "tag_problem id must be greater than or equal to 1") @Argument int tagProblemId) {
        return tagProblemService.removeTagProblemById(tagProblemId);
    }

}
