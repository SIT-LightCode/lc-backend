package com.senior.dreamteam.repositories;


import com.senior.dreamteam.entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    List<Problem> findAll();
    Optional<Problem> findProblemById(int id);


}
