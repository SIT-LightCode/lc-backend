package com.senior.dreamteam.controllers;

import com.senior.dreamteam.entities.Tag;
import com.senior.dreamteam.entities.TagInput;
import com.senior.dreamteam.services.TagService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Controller
public class TagController {
    @Autowired
    TagService tagService;

    @SchemaMapping(typeName = "Query", value = "getTag")
    public List<Tag> findAll() {
        return tagService.findAll();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertTag")
    public Tag upsertLesson(@Validated @Argument TagInput tagInput) {
        Tag tag = new Tag();
        tag.setId(tagInput.getId());
        tag.setDescription(tagInput.getDescription());
        tag.setTopic(tagInput.getTopic());

        return tagService.upsertTag(tag);
    }

    @SchemaMapping(typeName = "Mutation", value = "removeTag")
    public String removeLesson(@Min(value = 1, message = "tag id must be greater than or equal to 1") @Argument int tagId) {
        return tagService.removeTagById(tagId);
    }
}
