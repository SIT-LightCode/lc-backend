package com.senior.dreamteam.tag.controller;

import com.senior.dreamteam.lesson.entity.Lesson;
import com.senior.dreamteam.lesson.entity.LessonInput;
import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.entity.TagInput;
import com.senior.dreamteam.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class TagController {
    @Autowired
    TagService tagService;

    @SchemaMapping(typeName = "Query", value = "getTag")
    public List<Tag> findAll() {
        return tagService.findAll();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertTag")
    public Tag upsertLesson(@Argument TagInput tagInput) {
        Tag tag = new Tag();
        tag.setId(tagInput.getId());
        tag.setDescription(tagInput.getDescription());
        tag.setTopic(tagInput.getTopic());

        return tagService.upsertTag(tag);
    }

    @SchemaMapping(typeName = "Mutation", value = "removeTag")
    public String removeLesson(@Argument int tagId) {
        return tagService.removeTagById(tagId);
    }

//    @QueryMapping
//    public Book findById(@Argument Integer id) {
//        return bookRepository.findById(id);
//    }
}
