package com.senior.dreamteam.jointable.entities.tagproblem.entity;

import com.senior.dreamteam.lesson.entity.Lesson;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TagProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    Tag tag;

    @ManyToOne
    Problem problem;
}