package com.senior.dreamteam.problem.service;

import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.problem.repository.ProblemRepository;
import com.senior.dreamteam.testcase.entity.Testcase;
import com.senior.dreamteam.testcase.service.CompilingService;
import com.senior.dreamteam.testcase.service.TestcaseService;
import org.json.JSONException;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class ProblemService {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestcaseService testcaseService;

    @Autowired
    CompilingService compilingService;

    public List<Problem> findAll() {
        return problemRepository.findAll();
    }

    public Optional<Problem> findAllById(int id) {
        return problemRepository.findProblemById(id);
    }

    public Problem upsertProblem(Problem problem) {
        try {
            String lang = "js";
            JSONObject typeParameters = new JSONObject(problem.getTypeParameter());
            Problem problemSaved = problemRepository.save(problem);

            // Generate a thousand sets of parameters
            List<Object> generatedParams = new ArrayList<>();
            for (int i = 0; i < 1500; i++) {
                JSONObject newParams = new JSONObject();
                Iterator keys = typeParameters.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object param = new JSONTokener(typeParameters.getString(key)).nextValue();
                    Object newParam = generateNewValue(param);
                    newParams.put(key, newParam);
                }
                // Convert JSONObject to List<Object>
                List<Object> paramsList = new ArrayList<>();
                Iterator<String> keysIterator = newParams.keys();
                while (keysIterator.hasNext()) {
                    String key = keysIterator.next();
                    Object value = newParams.get(key);
                    if (value instanceof String && ((String) value).startsWith("[")) {
                        // Parse the string as a JSONArray
                        JSONArray jsonArray = new JSONArray((String) value);
                        // Manually convert JSONArray to List<Object>
                        List<Object> arrayAsList = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            arrayAsList.add(jsonArray.get(j));
                        }
                        paramsList.add(arrayAsList);
                    } else {
                        // Add other values directly
                        paramsList.add(value);
                    }
                }
                generatedParams.add(paramsList);
            }

            // Execute the JavaScript function with the generated parameters
            for (Object params : generatedParams) {
                System.out.println(params.toString());
                try {
                    JSONObject jsonBody = compilingService.createDataObject(problem.getSolution(), params.toString());
                    String returnValue = compilingService.postData(jsonBody, lang);
                    String result = compilingService.handleResponse(returnValue);
                    // Save the testcase with the generated parameters and result
                    Testcase testcase = new Testcase();
                    testcase.setProblem(problemSaved);
                    testcase.setInput(params.toString());
                    testcase.setOutput(result);
                    testcaseService.upsertTestcase(testcase);
                } catch (Exception e) {
                    System.out.println("catch: " + e.getMessage());
                    testcaseService.findTestcasesByProblemId(problemSaved.getId());
                    problemRepository.deleteById(problemSaved.getId());
                    throw new DemoGraphqlException("An error occured: " + e.getMessage(), 404);
                }
            }
            return problemSaved;
        } catch (Exception e) {
            throw new DemoGraphqlException("An error occured: " + e.getMessage(), 404);
        }
    }


    private Object generateNewValue(Object param) throws JSONException {
        if (param instanceof JSONArray jsonArray) {
            // It's an array
            JSONArray newArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                Object element = jsonArray.get(i);
                newArray.put(generateNewValue(element));
            }
            return newArray.toString();
        } else if (param instanceof JSONObject jsonObject) {
            // It's an object
            JSONObject newObject = new JSONObject();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                newObject.put(key, generateNewValue(value));
            }
            return newObject.toString();
        } else if (param instanceof Number) {
            // Generate a new random number
            int bound = 200;
            return new Random().nextInt(bound) - bound / 4;
        } else if (param instanceof String) {
            // Generate a new random string
            return UUID.randomUUID().toString();
        } else {
            // Default to string if type is unknown
            return param.toString();
        }
    }


    private void saveParameterAndResult(Object param, int result) {
        // Implement your logic to save the parameter and result here
        System.out.println("Parameter: " + param + ", Result: " + result);
    }

    public String removeProblmById(int id) {
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
