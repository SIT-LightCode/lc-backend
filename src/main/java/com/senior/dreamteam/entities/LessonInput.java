package com.senior.dreamteam.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LessonInput {
    private int id;
    private int tagId;
    private String name;
    private String content;

}
