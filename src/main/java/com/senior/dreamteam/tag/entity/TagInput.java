package com.senior.dreamteam.tag.entity;

import com.senior.dreamteam.lesson.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TagInput {

    int id;
    String topic;
    String description;
}
