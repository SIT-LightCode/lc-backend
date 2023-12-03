package com.senior.dreamteam.problem.entity;

import com.senior.dreamteam.example.entity.Example;
import com.senior.dreamteam.jointable.entities.tagproblem.entity.TagProblem;
import com.senior.dreamteam.testcase.entity.Testcase;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String name;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    List<TagProblem> tagProblems;

    @Column(length = Integer.MAX_VALUE)
    String description;

    @Column(length = Integer.MAX_VALUE)
    String solution;

    String typeparameter;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    List<Example> example;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    List<Testcase> testcase;

    Long totalScore;

//    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
//    List<Submission> submission;

//    @ManyToOne
//    User user;
}
