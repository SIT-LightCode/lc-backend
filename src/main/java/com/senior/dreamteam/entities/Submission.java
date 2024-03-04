package com.senior.dreamteam.entities;

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
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    int id;

    @ManyToOne
    User user;

    @ManyToOne
    Problem problem;

    @Column(columnDefinition = "TEXT")
    String code;

    int score;
    int scoreUnOfficial;
}
