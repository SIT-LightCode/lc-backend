package com.senior.dreamteam.problem.entity;

import com.senior.dreamteam.example.entity.Example;
import com.senior.dreamteam.tagproblem.entity.TagProblem;
import com.senior.dreamteam.testcase.entity.Testcase;
import com.senior.dreamteam.user.entity.User;
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
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TagProblem> tagProblem;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String solution;

    String exampleParameter;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Example> example;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Testcase> testcase;

    int level;

    int totalScore;

//    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
//    List<Submission> submission;

    @ManyToOne
    User user;
}
