package com.senior.dreamteam.entities;


import jakarta.validation.constraints.Min;
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

    @Min(value = 1, message = "tagId must be greater than or equal to 1")
    private int tagId;

    @NotEmpty(message = "name must not be empty")
    private String name;
    private String content;

}
