package com.senior.dreamteam.entities;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.graphql.data.method.annotation.Argument;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TagInput {

    int id;

    @NotEmpty(message = "topic must not be empty")
    @Min(value = 0, message = "topic length must be greater than 0") @Max(value = 255, message = "topic length must be more than 255")
    String topic;
    @NotEmpty(message = "description must not be empty")
    String description;
}
