package com.senior.dreamteam.testcase.service;

import com.senior.dreamteam.testcase.entity.Testcase;
import com.senior.dreamteam.testcase.repository.TestcaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestcaseService {

    @Autowired
    TestcaseRepository testcaseRepository;

    public List<Testcase> findAll() {
        return testcaseRepository.findAll();
    }

    public Optional<Testcase> findAllById(int id) {
        return testcaseRepository.findTestcaseById(id);
    }

    public List<Testcase> findTestcasesByProblemId(int problemId) {
        return testcaseRepository.findTestcasesByProblemId(problemId);
    }

    public void removeTestcasesByProblemId(int problemId) {
        testcaseRepository.deleteTestcasesByProblemId(problemId);
    }

    public Testcase upsertTestcase(Testcase testcase) {
        return testcaseRepository.save(testcase);
    }

    public List<Testcase> saveAll(List<Testcase> testcaseList) {
        return testcaseRepository.saveAll(testcaseList);
    }

}
