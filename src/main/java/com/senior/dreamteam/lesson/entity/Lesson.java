package com.senior.dreamteam.lesson.entity;

import com.senior.dreamteam.example.entity.Example;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.tag.entity.Tag;
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
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    Tag tag;

    @Column(length = 255)
    String name;

    @Column(length = 20000)
    String content;

}
