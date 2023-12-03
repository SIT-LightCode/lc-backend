package com.senior.dreamteam.testcase.repository;

import com.senior.dreamteam.testcase.entity.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestcaseRepository extends JpaRepository<Testcase, Integer> {

    List<Testcase> findAll();

    Optional<Testcase> findTestcaseById(int id);

    List<Testcase> findTestcasesByProblemId(Long problemId);

}
