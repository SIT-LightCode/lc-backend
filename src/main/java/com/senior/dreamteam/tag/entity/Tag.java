package com.senior.dreamteam.tag.entity;

import com.senior.dreamteam.lesson.entity.Lesson;
import com.senior.dreamteam.problem.entity.Problem;
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
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(length = 255)
    String topic;

    @Column(length = 1000)
    String description;

    @ManyToOne
    Problem problem;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    List<Lesson> lesson;

}
