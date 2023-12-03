package com.senior.dreamteam.problem.service;

import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.problem.repository.ProblemRepository;
import com.senior.dreamteam.testcase.entity.Testcase;
import com.senior.dreamteam.testcase.repository.TestcaseRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import org.graalvm.polyglot.*;
@Service
public class ProblemService {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestcaseRepository testcaseRepository;
    public List<Problem> findAll(){
        return problemRepository.findAll();
    }

    public Optional<Problem> findAllById(int id){
        return problemRepository.findProblemById(id);
    }

    public Problem upsertProblem(Problem problem ){
        List<Testcase> testcases = testcaseRepository.findTestcasesByProblemId(problem.getId());
        try (Context context = Context.create()) {
            Value functionValue = context.eval("js", problem.getSolution());
                if(problem.getTypeParameter() == "number"){

                }
//                String param = testCase.getParam();
//                String expected = testCase.getResult();
//                Value resultValue = functionValue.execute(Integer.parseInt(param));
//                String result = resultValue.asString();
//                if (result.equals(expected)) {
//                    System.out.println("Test case PASSED: Param: " + param + ", Result: " + result);
//                } else {
//                    System.out.println("Test case FAILED: Param: " + param + ", Expected: " + expected + ", Actual: " + result);
//                }

        } catch (PolyglotException e) {
            e.printStackTrace();
        } catch (Exception e){
            System.out.println(e);
        }

        return problemRepository.save(problem);
    }

    public String removeProblmById(int id){
        try {
            Optional<Problem> problemOptional = problemRepository.findById(id);

            if (problemOptional.isPresent()) {
                problemRepository.deleteById(id);
                return "Problem removed successfully";
            } else {
                return "Problem not found with ID: " + id;
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }
}
