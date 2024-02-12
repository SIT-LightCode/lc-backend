package com.senior.dreamteam.entities;


import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "tagId must not be empty")
    private int tagId;

    @NotEmpty(message = "name must not be empty")
    private String name;
    private String content;

}
