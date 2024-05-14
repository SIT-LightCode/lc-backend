package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.services.JwtTokenUtil;
import com.senior.dreamteam.entities.Submission;
import com.senior.dreamteam.repositories.SubmissionRepository;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.services.TagProblemService;
import com.senior.dreamteam.services.TagService;
import com.senior.dreamteam.services.UserService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SubmissionController {
    final SubmissionRepository submissionRepository;
    final TagProblemService tagProblemService;
    final TagService tagService;
    final ProblemService problemService;
    final UserService userService;
    final JwtTokenUtil jwtTokenUtil;

    @SchemaMapping(typeName = "Query", value = "getSubmissionByUserId")
    public List<Submission> findByUserId(@Min(value = 1, message = "id must be greater than or equal to 1") @Argument int userId) {
        return submissionRepository.findByUserId(userId);
    }

}
