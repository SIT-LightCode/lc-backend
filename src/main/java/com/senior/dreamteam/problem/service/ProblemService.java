package com.senior.dreamteam.problem.service;

import com.senior.dreamteam.example.entity.Example;
import com.senior.dreamteam.example.service.ExampleService;
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
    ExampleService exampleService;

    @Autowired
    CompilingService compilingService;

    int PARAM_GENERATION_COUNT = 1500;

    public List<Problem> findAll() {
        return problemRepository.findAll();
    }

    public Optional<Problem> findAllById(int id) {
        return problemRepository.findProblemById(id);
    }

    public Problem upsertProblem(Problem problem) throws JSONException {
        if (problem.getId() != 0) {
            return problemRepository.save(problem);
        }
        String lang = "js";
        Boolean isExample = true;
        Problem problemSaved = problemRepository.save(problem);
        System.out.println("here");
        JSONArray exampleParametersArray = new JSONArray(problem.getExampleParameter());
        List<Object> exampleParameters = new ArrayList<>();
        for (int i = 0; i < exampleParametersArray.length(); i++) {
            JSONObject exampleParametersObject = exampleParametersArray.getJSONObject(i);
            exampleParameters.add(convertParamsToList(exampleParametersObject));
        }
        executeAndSaveTest(problemSaved, exampleParameters, lang, isExample);

        List<Object> generatedParams = generateParameters(exampleParametersArray.getJSONObject(0), PARAM_GENERATION_COUNT);
        executeAndSaveTest(problemSaved, generatedParams, lang, !isExample);

        return problemRepository.findProblemById(problemSaved.getId()).get();
    }

    public String removeProblemById(int id) {
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

    private List<Object> generateParameters(JSONObject exampleParameters, int count) throws JSONException {
        List<Object> generatedParams = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            JSONObject newParams = generateSingleSetOfParams(exampleParameters);
            generatedParams.add(convertParamsToList(newParams));
        }
        return generatedParams;
    }

    private JSONObject generateSingleSetOfParams(JSONObject exampleParameters) throws JSONException {
        JSONObject newParams = new JSONObject();
        Iterator<String> keys = exampleParameters.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object param = new JSONTokener(exampleParameters.getString(key)).nextValue();
            newParams.put(key, generateNewValue(param));
        }
        return newParams;
    }

    private List<Object> convertParamsToList(JSONObject newParams) throws JSONException {
        List<Object> paramsList = new ArrayList<>();
        Iterator<String> keysIterator = newParams.keys();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            Object value = newParams.get(key);
            paramsList.add(parseValue(value));
        }
        return paramsList;
    }

    private Object parseValue(Object value) throws JSONException {
        if (value instanceof String && ((String) value).startsWith("[")) {
            JSONArray jsonArray = new JSONArray((String) value);
            return jsonArrayToList(jsonArray);
        }
        return value;
    }

    private List<Object> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object item = jsonArray.get(i);
            if (item instanceof JSONArray) {
                list.add(jsonArrayToList((JSONArray) item));
            } else if (item instanceof JSONObject) {
                list.add(jsonObjectToMap((JSONObject) item));
            } else {
                list.add(item);
            }
        }
        return list;
    }

    private Map<String, Object> jsonObjectToMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONArray) {
                map.put(key, jsonArrayToList((JSONArray) value));
            } else if (value instanceof JSONObject) {
                map.put(key, jsonObjectToMap((JSONObject) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    private void executeAndSaveTest(Problem problem, List<Object> testParams, String lang, Boolean isExample) {
        System.out.println(problem.getId());
        List<Testcase> testcases = new ArrayList<>();
        List<Example> examples = new ArrayList<>();

        for (Object params : testParams) {
            try {
                JSONObject jsonBody = compilingService.createDataObject(problem.getSolution(), params.toString());
                String returnValue = compilingService.postData(jsonBody, lang);
                String result = compilingService.handleResponse(returnValue);

                if (isExample) {
                    Example example = new Example();
                    example.setProblem(problem);
                    example.setInput(params.toString());
                    example.setOutput(result);
                    examples.add(example);
                } else {
                    Testcase testcase = new Testcase();
                    testcase.setProblem(problem);
                    testcase.setInput(params.toString());
                    testcase.setOutput(result);
                    testcases.add(testcase);
                }
            } catch (Exception e) {
                handleTestcaseError(problem, e);
                return; // Exit the method if an error occurs
            }
        }

        // Save all test cases and examples at once
        if (!examples.isEmpty()) {
            exampleService.saveAll(examples);
        }
        if (!testcases.isEmpty()) {
            testcaseService.saveAll(testcases);
        }
    }

    private void handleTestcaseError(Problem problem, Exception e) {
        testcaseService.removeTestcasesByProblemId(problem.getId());
        problemRepository.deleteById(problem.getId());
        throw new DemoGraphqlException("An error occurred: " + e.getMessage(), 404);
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
}
