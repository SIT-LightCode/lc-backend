package com.senior.dreamteam.controllers;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.entities.Tag;
import com.senior.dreamteam.entities.TagInput;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.services.TagService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Controller
@Slf4j
public class TagController {
    @Autowired
    TagService tagService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @SchemaMapping(typeName = "Query", value = "getTag")
    public List<Tag> findAll() {
        return tagService.findAll();
    }

    @SchemaMapping(typeName = "Mutation", value = "upsertTag")
    public Tag upsertTag(@Validated @Argument TagInput tagInput, @NotEmpty(message = "Token cannot be empty") @ContextValue String token) throws Exception {
        if (!jwtTokenUtil.isAdminToken(token)) {
            log.info("Unauthorized: Cannot Update this tag");
            throw new DemoGraphqlException("Unauthorized: Cannot Update this lesson");
        }
        Tag tag = new Tag();
        tag.setId(tagInput.getId());
        tag.setDescription(tagInput.getDescription());
        tag.setTopic(tagInput.getTopic());

        return tagService.upsertTag(tag);
    }

    @SchemaMapping(typeName = "Mutation", value = "removeTag")
    public String removeTag(@Min(value = 1, message = "tag id must be greater than or equal to 1") @Argument int tagId, @NotEmpty(message = "Token cannot be empty") @ContextValue String token) throws Exception {
        if (!jwtTokenUtil.isAdminToken(token)) {
            log.info("Unauthorized: Cannot remove this tag");
            throw new DemoGraphqlException("Unauthorized: Cannot Update this lesson");
        }
        return tagService.removeTagById(tagId);
    }
}
