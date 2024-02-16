package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.entities.Roles;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.services.TagProblemService;
import com.senior.dreamteam.entities.CheckAnswerResult;
import com.senior.dreamteam.entities.Problem;
import com.senior.dreamteam.services.ProblemService;
import com.senior.dreamteam.services.TagService;
import com.senior.dreamteam.services.UserService;
import com.senior.dreamteam.validation.GenericValidation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
    public Problem findAllById(@Min(value = 1, message = "id must be greater than or equal to 1") @Argument int id) {
        return problemService.findAllById(id).get();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertProblem")
    public Problem upsertProblem(@Min(value = 1, message = "id must be greater than or equal to 1") @Argument Integer id,
                                 @NotEmpty(message = "name must not be empty") @Argument String name,
                                 @NotEmpty(message = "description must not be empty") @Argument String description,
                                 @NotEmpty(message = "tag id must not be empty") @Argument String arrayTagId,
                                 @NotEmpty(message = "solution must not be empty") @Argument String solution,
                                 @Argument String exampleParameter,
                                 @Min(value = 0, message = "level must be greater than or equal to 0") @Max(value = 5, message = "level must be lesser than or equal to 5") @Argument int level,
                                 @Min(value = 0, message = "total score must be greater than or equal to 0") @Max(value = 100, message = "total score must be lesser than or equal to 100") @Argument int totalScore,
                                 @Argument Boolean isOfficial,
                                 @NotEmpty(message = "Token cannot be empty") @ContextValue String token
    ) throws JSONException {
        String emailFromToken = "";
        Boolean isAdmin = false;
        if (!token.isEmpty()) {
            emailFromToken = jwtTokenUtil.getUsernameFromToken(token);
            isAdmin = jwtTokenUtil.getAuthoritiesFromToken(token).contains(Roles.ADMIN.name());
        }
        Problem problem = new Problem();
        if (id != null) {
            problem = problemService.findAllById(id).get();
            if (problem.getUser().getEmail().equals(emailFromToken) || isAdmin) {
                problem.setName(name);
                problem.setDescription(description);
                problem.setTotalScore(totalScore);
                problem.setLevel(level);
                if (isAdmin) {
                    problem.setIsOfficial(isOfficial);
                }
                tagProblemService.upsertMultiTagProblemByProblemAndArrTagId(problem, arrayTagId);
                return problemService.upsertProblem(problem);
            }
        }
        problem.setUser(userService.findUserByEmail(emailFromToken));
        problem.setName(name);
        problem.setDescription(description);
        problem.setSolution(solution);
        problem.setExampleParameter(exampleParameter);
        problem.setTotalScore(totalScore);
        problem.setLevel(level);
        problem.setIsOfficial(false);
        if (isAdmin) {
            problem.setIsOfficial(isOfficial);
        }
        Problem result = problemService.upsertProblem(problem);
        tagProblemService.upsertMultiTagProblemByProblemAndArrTagId(result, arrayTagId);
        return result;
    }

    @SchemaMapping(typeName = "Mutation", value = "removeProblem")
    public String removeProblem(@NotEmpty(message = "Unauthorized: Cannot remove problem") @ContextValue String token, @Min(value = 1, message = "id must be greater than or equal to 1") @Argument int id) {
        return problemService.removeProblemById(token, id);
    }

    @SchemaMapping(typeName = "Mutation", value = "checkAnswer")
    public CheckAnswerResult checkAnswer(@NotEmpty(message = "Token cannot be empty") @ContextValue String token, @Min(value = 1, message = "id must be greater than or equal to 1") @Argument int problemId,
                                         @NotEmpty(message = "solution must not be empty") @Argument String solution) {
        return problemService.checkAnswer(token, problemId, solution);
    }
}
