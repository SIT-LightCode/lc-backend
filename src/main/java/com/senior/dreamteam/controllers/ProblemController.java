package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.entities.Roles;
import com.senior.dreamteam.services.TagProblemService;
import com.senior.dreamteam.entities.CheckAnswerResult;
import com.senior.dreamteam.entities.Problem;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.services.TagService;
import com.senior.dreamteam.services.UserService;
import com.senior.dreamteam.validation.GenericValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProblemController {
    final ProblemService problemService;
    final TagService tagService;
    final TagProblemService tagProblemService;
    final JwtTokenUtil jwtTokenUtil;
    final UserService userService;

    GenericValidation genericValidation = new GenericValidation();

    @SchemaMapping(typeName = "Query", value = "getProblem")
    public List<Problem> findAll() {
        return problemService.findAll();
    }

    @SchemaMapping(typeName = "Query", value = "getProblemById")
    public Problem findAllById(@Argument int id) {
        return problemService.findAllById(id).get();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertProblem")
    public Problem upsertProblem(@Argument Integer id, @Argument String name, @Argument String description,
                                 @Argument String arrayTagId,
                                 @Argument String solution, @Argument String exampleParameter, @Argument int level, @Argument int totalScore, @Argument Boolean isOfficial, @ContextValue String token
    ) throws JSONException {
        genericValidation.validateParameter(level, 1, 5);
        genericValidation.validateParameter(totalScore, 1, 100);
        String emailFromToken = jwtTokenUtil.getUsernameFromToken(token);
        Boolean isAdmin = jwtTokenUtil.getAuthoritiesFromToken(token).contains(Roles.ADMIN.name());
        Problem problem = new Problem();
        if (id != null) {
            problem = problemService.findAllById(id).get();
            if (problem.getUser().getEmail() == emailFromToken || isAdmin) {
                problem.setName(name);
                problem.setDescription(description);
                problem.setTotalScore(totalScore);
                problem.setLevel(level);
                if(isAdmin){
                    problem.setIsOfficial(isOfficial);
                }
                tagProblemService.upsertMultiTagProblemByProblemAndArrTagId(problem, arrayTagId);
                return problemService.upsertProblem(problem);
            }
        }
        problem.setName(name);
        problem.setDescription(description);
        problem.setSolution(solution);
        problem.setExampleParameter(exampleParameter);
        problem.setTotalScore(totalScore);
        problem.setLevel(level);
        problem.setIsOfficial(false);
        if(isAdmin){
            problem.setIsOfficial(isOfficial);
        }
        Problem result = problemService.upsertProblem(problem);
        tagProblemService.upsertMultiTagProblemByProblemAndArrTagId(result, arrayTagId);
        return result;
    }

    @SchemaMapping(typeName = "Mutation", value = "removeProblem")
    public String removeTagProblem(@ContextValue String token, @Argument int id) {
        return problemService.removeProblemById(token, id);
    }

    @SchemaMapping(typeName = "Mutation", value = "checkAnswer")
    public CheckAnswerResult checkAnswer(@ContextValue String token, @Argument int problemId, @Argument String solution) {
        return problemService.checkAnswer(token, problemId, solution);
    }
}
