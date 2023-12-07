package com.senior.dreamteam.example.service;

import com.senior.dreamteam.example.entity.Example;
import com.senior.dreamteam.example.repository.ExampleRepository;
import com.senior.dreamteam.testcase.entity.Testcase;
import com.senior.dreamteam.testcase.repository.TestcaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExampleService {

    @Autowired
    ExampleRepository exampleRepository;

    public List<Example> findAll() {
        return exampleRepository.findAll();
    }

    public Optional<Example> findAllById(int id) {
        return exampleRepository.findExampleById(id);
    }

    public List<Example> findExamplesByProblemId(int problemId) {
        return exampleRepository.findExamplesByProblemId(problemId);
    }

    public void removeExamplesByProblemId(int problemId) {
        exampleRepository.deleteExamplesByProblemId(problemId);
    }

    public Example upsertExample(Example example) {
        return exampleRepository.save(example);
    }

    public List<Example> saveAll(List<Example> exampleList){
        return exampleRepository.saveAll(exampleList);
    }


}
