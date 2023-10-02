package com.senior.dreamteam.tag.entity;

import com.senior.dreamteam.lesson.entity.Lesson;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String topic;
    String description;

    @ManyToOne
    Problem problem;

    @ManyToOne
    Lesson lesson;

}
