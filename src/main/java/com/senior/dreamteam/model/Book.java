package com.senior.dreamteam.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@Setter
public class Book {
    private Integer id;
    private String title;
    private Integer pages;

    public Book(Integer id, String title, Integer pages) {
        this.id = id;
        this.title = title;
        this.pages = pages;
    }
}