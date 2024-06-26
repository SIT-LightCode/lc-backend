package com.senior.dreamteam.repositories;


import com.senior.dreamteam.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    List<Submission> findByUser(User user);
    List<Submission> findByUserId(Integer userId);

//    Optional<Submission> findByUserAndProblem(User user, Problem problem);
    Optional<Submission> findFirstByUserAndProblem(User user, Problem problem);

}
