package com.senior.dreamteam.entities;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TagInput {

    int id;

    @NotEmpty(message = "topic must not be empty")
    String topic;
    @NotEmpty(message = "description must not be empty")
    String description;
}
