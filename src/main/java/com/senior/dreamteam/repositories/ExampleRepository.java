package com.senior.dreamteam.repositories;

import com.senior.dreamteam.entities.Example;
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
