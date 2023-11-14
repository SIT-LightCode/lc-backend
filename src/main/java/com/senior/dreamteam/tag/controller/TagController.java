package com.senior.dreamteam.tag.controller;

import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @QueryMapping
//    public Book findById(@Argument Integer id) {
//        return bookRepository.findById(id);
//    }
}
