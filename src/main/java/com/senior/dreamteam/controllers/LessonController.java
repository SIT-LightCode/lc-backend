package com.senior.dreamteam.controllers;

import com.senior.dreamteam.entities.Lesson;
import com.senior.dreamteam.entities.LessonInput;
import com.senior.dreamteam.services.LessonService;
import com.senior.dreamteam.entities.Tag;
import com.senior.dreamteam.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Collections;
import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class LessonController {
    @Autowired
    LessonService lessonService;

    @Autowired
    TagService tagService;

    @SchemaMapping(typeName = "Query", value = "getLesson")
    public List<Lesson> findAll() {
        return lessonService.findAll();
    }

    @SchemaMapping(typeName = "Query", value = "getLessonByTagId")
    public List<Lesson> findAllByTag_Id(String id) {
        try {
            int tagId = Integer.parseInt(id);
            return lessonService.findAllByTagId(tagId);
        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertLesson")
    public Lesson upsertLesson(@Argument LessonInput lessonInput) {
        Lesson lesson = new Lesson();
        lesson.setId(lessonInput.getId());
        lesson.setName(lessonInput.getName());
        lesson.setContent(lessonInput.getContent());
        Tag tag = tagService.findAllById(lessonInput.getTagId()).orElse(null);
        lesson.setTag(tag);

        return lessonService.upsertLesson(lesson);
    }

    @SchemaMapping(typeName = "Mutation", value = "removeLesson")
    public String removeLesson(@Argument int lessonId) {
        return lessonService.removeLessonById(lessonId);
    }

}
