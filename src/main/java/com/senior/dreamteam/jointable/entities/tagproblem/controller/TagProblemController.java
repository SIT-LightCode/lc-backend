package com.senior.dreamteam.jointable.entities.tagproblem.controller;

import com.senior.dreamteam.jointable.entities.tagproblem.entity.TagProblem;
import com.senior.dreamteam.jointable.entities.tagproblem.service.TagProblemService;
import com.senior.dreamteam.lesson.entity.Lesson;
import com.senior.dreamteam.lesson.entity.LessonInput;
import com.senior.dreamteam.lesson.service.LessonService;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.problem.service.ProblemService;
import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Collections;
import java.util.List;

@Controller
public class TagProblemController {
    @Autowired
    TagProblemService tagProblemService;

    @Autowired
    TagService tagService;

    @Autowired
    ProblemService problemService;

    @SchemaMapping(typeName = "Query", value = "getTagProblem")
    public List<TagProblem> findAll() {
        return tagProblemService.findAll();
    }

    @SchemaMapping(typeName = "Query", value = "getTagProblemById")
    public TagProblem findAllById(@Argument int id) {
            return tagProblemService.findAllById(id).get();
    }

    @SchemaMapping(typeName = "Query", value = "getTagProblemByTagId")
    public List<TagProblem> findTagProblemsByTagId(@Argument int id) {
        return tagProblemService.findTagProblemsByTagId(id);
    }


    @SchemaMapping(typeName = "Mutation", value = "upsertTagProblem")
    public TagProblem upsertTagProblem(@Argument int id, @Argument int tagId, @Argument int problemId) {
        Tag tag = tagService.findAllById(tagId).get();
        Problem problem = problemService.findAllById(problemId).get();
        return tagProblemService.upsertTagProblem(new TagProblem(id, tag, problem));
    }

    @SchemaMapping(typeName = "Mutation", value = "removeTagProblem")
    public String removeTagProblem(@Argument int tagProblemId) {
        return tagProblemService.removeTagProblemById(tagProblemId);
    }

}
