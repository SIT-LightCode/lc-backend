package com.senior.dreamteam.user.entity;

import com.senior.dreamteam.example.entity.Example;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.skill.entity.Skill;
import com.senior.dreamteam.submisstion.entity.Submission;
import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.testcase.entity.Testcase;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    Authorities authorities;
    String name;
    String username;
    String password;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Submission> submission;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Problem> problem;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    List<Problem> likedProblem;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Skill> skill;
}

