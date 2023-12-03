package com.senior.dreamteam.jointable.entities.tagproblem.repository;


import com.senior.dreamteam.jointable.entities.tagproblem.entity.TagProblem;
import com.senior.dreamteam.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagProblemRepository extends JpaRepository<TagProblem, Integer> {

    List<TagProblem> findAll();
    Optional<TagProblem> findTagProblemById(int id);

    List<TagProblem> findTagProblemsByProblemId(Long problemId);

    List<TagProblem> findTagProblemsByTagId(Long tagId);}
