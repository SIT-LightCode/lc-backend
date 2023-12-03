package com.senior.dreamteam.submisstion.entity;

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
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    User user;

//    @ManyToOne
//    Problem problem;

    String code;

    Long score;
}
