package com.senior.dreamteam.lesson.controller;

import com.senior.dreamteam.lesson.entity.Lesson;
import com.senior.dreamteam.lesson.service.LessonService;
import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
public class LessonController {
    @Autowired
    LessonService lessonService;

    @SchemaMapping(typeName = "Query", value = "getLesson")
    public List<Lesson> findAll() {
        return lessonService.findAll();
    }

    @SchemaMapping(typeName = "Query", value = "getLessonByTagId")
    public List<Lesson> findAllByTag_Id(String id) {
        try {
            Long tagId = Long.parseLong(id);
            return lessonService.findAllByTagId(tagId);
        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
    }

//    @QueryMapping
//    public Book findById(@Argument Integer id) {
//        return bookRepository.findById(id);
//    }
}
