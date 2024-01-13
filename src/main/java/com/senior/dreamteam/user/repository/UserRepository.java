package com.senior.dreamteam.user.repository;


import com.senior.dreamteam.tagproblem.entity.TagProblem;
import com.senior.dreamteam.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAll();
    Optional<User> findTagProblemById(int id);

    List<User> findTagProblemsByProblemId(int problemId);
    List<User> findTagProblemsByTagId(int tagId);
}
