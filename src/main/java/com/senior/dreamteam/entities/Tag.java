package com.senior.dreamteam.entities;

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
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(length = 255)
    String topic;

    @Column(columnDefinition = "TEXT")
    String description;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    List<TagProblem> tagProblems;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    List<Lesson> lesson;

}
