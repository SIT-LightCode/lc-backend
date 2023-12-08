package com.senior.dreamteam.testcase.entity;

import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.user.entity.User;
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
public class Testcase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String input;
    String output;

    @ManyToOne
    Problem problem;

}
