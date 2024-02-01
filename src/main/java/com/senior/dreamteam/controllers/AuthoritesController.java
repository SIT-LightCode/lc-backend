package com.senior.dreamteam.controllers;

import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Problem;
import com.senior.dreamteam.services.AuthoritiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthoritesController {
    final AuthoritiesService authoritiesService;

    @SchemaMapping(typeName = "Query", value = "getAuthorities")
    public List<Authorities> findAll() {
        return authoritiesService.findAll();
    }

    @SchemaMapping(typeName = "Query", value = "getAuthoritiesByUserId")
    public List<Authorities> findAll(@Argument int userId) {
        return authoritiesService.findByUserId(userId);
    }
}