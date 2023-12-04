package com.senior.dreamteam.example.entity;

import com.senior.dreamteam.problem.entity.Problem;
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
public class Example {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String input;
    String output;

    @ManyToOne
    Problem problem;

}
