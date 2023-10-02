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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    List<Tag> topics;

    String name;

    String content;

}
