package com.senior.dreamteam.tagproblem.repository;


import com.senior.dreamteam.tagproblem.entity.TagProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TagProblemRepository extends JpaRepository<TagProblem, Integer> {

    List<TagProblem> findAll();
    Optional<TagProblem> findTagProblemById(int id);

    List<TagProblem> findTagProblemsByProblemId(int problemId);
    List<TagProblem> findTagProblemsByTagId(int tagId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TagProblem tp WHERE tp.problem.id = :problemId")
    void removeTagProblemsByProblemId(int problemId);
}
