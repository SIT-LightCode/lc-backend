package com.senior.dreamteam.example.repository;

import com.senior.dreamteam.example.entity.Example;
import com.senior.dreamteam.testcase.entity.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ExampleRepository extends JpaRepository<Example, Integer> {

    List<Example> findAll();

    Optional<Example> findExampleById(int id);

    List<Example> findExamplesByProblemId(int problemId);

    @Transactional
    void deleteExamplesByProblemId(int problemId);
}
