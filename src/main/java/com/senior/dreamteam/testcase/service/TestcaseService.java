package com.senior.dreamteam.testcase.service;

import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.problem.repository.ProblemRepository;
import com.senior.dreamteam.testcase.entity.Testcase;
import com.senior.dreamteam.testcase.repository.TestcaseRepository;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestcaseService {

    @Autowired
    TestcaseRepository testcaseRepository;

    public List<Testcase> findAll(){
        return testcaseRepository.findAll();
    }

    public Optional<Testcase> findAllById(int id){
        return testcaseRepository.findTestcaseById(id);
    }

    public List<Testcase> findTestcasesByProblemId(int id){
        return testcaseRepository.findTestcasesByProblemId(id);
    }


    public Testcase upsertTestcase(Testcase testcase){
        return testcaseRepository.save(testcase);
    }


}
