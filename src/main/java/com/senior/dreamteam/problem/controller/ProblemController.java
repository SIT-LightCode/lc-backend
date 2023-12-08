package com.senior.dreamteam.problem.controller;

import com.senior.dreamteam.jointable.entities.tagproblem.service.TagProblemService;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.problem.service.ProblemService;
import com.senior.dreamteam.tag.service.TagService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProblemController {
    @Autowired
    ProblemService problemService;

    @Autowired
    TagService tagService;

    @Autowired
    TagProblemService tagProblemService;

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
                                 @Argument String solution, @Argument String exampleParameter, @Argument int totalScore
    ) throws JSONException {
        Problem problem = new Problem();
        if (id != null) {
            problem = problemService.findAllById(id).get();
            problem.setName(name);
            problem.setDescription(description);
            problem.setTotalScore(totalScore);
            tagProblemService.upsertMultiTagProblemByProblemAndArrTagId(problem, arrayTagId);
            return problemService.upsertProblem(problem);
        }
        problem.setName(name);
        problem.setDescription(description);
        problem.setSolution(solution);
        problem.setExampleParameter(exampleParameter);
        problem.setTotalScore(totalScore);
        Problem result = problemService.upsertProblem(problem);
        System.out.println("be4 tag_pro");
        tagProblemService.upsertMultiTagProblemByProblemAndArrTagId(result, arrayTagId);
        return result;
    }

    @SchemaMapping(typeName = "Mutation", value = "removeProblem")
    public String removeTagProblem(@Argument int id) {
        return problemService.removeProblemById(id);
    }

}
