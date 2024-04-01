package com.senior.dreamteam.repositories;


import com.senior.dreamteam.entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {

//    List<Problem> findAll();
//    Optional<Problem> findProblemById(int id);

    List<Problem> findByEnableTrue();
    Optional<Problem> findProblemByIdAndEnableTrue(int id);

    @Transactional
    @Modifying
    @Query("UPDATE Problem p SET p.enables = false WHERE p.id = :id")
    void disableProblemById(int id);
}
