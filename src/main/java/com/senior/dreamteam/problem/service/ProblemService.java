package com.senior.dreamteam.problem.service;

import com.senior.dreamteam.example.entity.Example;
import com.senior.dreamteam.example.service.ExampleService;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.problem.entity.CheckAnswerResult;
import com.senior.dreamteam.problem.entity.ExampleResult;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.problem.entity.TestcaseResult;
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

    private final int PARAM_GENERATION_COUNT = 1500;
    private int randomNumberCount = 0;


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
        System.out.println("upsert services");
        String lang = "js";
        Boolean isExample = true;
        System.out.println("b4 save");
        Problem problemSaved = problemRepository.save(problem);
        System.out.println("af save");
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
        randomNumberCount = 0;
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
        List<Testcase> testcases = new ArrayList<>();
        List<Example> examples = new ArrayList<>();
        System.out.println("in exe test");
        for (Object params : testParams) {
            try {
                JSONObject jsonBody = compilingService.createDataObject(problem.getSolution(), params.toString());
                System.out.println("b4 post to js node");
                String returnValue = compilingService.postData(jsonBody, lang);
                System.out.println("af post");
                String result = compilingService.handleResponse(returnValue);

                System.out.println("input: " + params.toString());
                System.out.println("result: " + result.toString());
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
                System.out.println("excep");
                System.out.println(e.toString());
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
            return generateRandomNumber();
        } else if (param instanceof String) {
            // Generate a new random string
            return UUID.randomUUID().toString();
        } else {
            // Default to string if type is unknown
            return param.toString();
        }
    }

    private int generateRandomNumber() {
        List<Integer> specialNumbers = Arrays.asList(0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
        Random random = new Random();
        int bound;
        if (randomNumberCount < 200) {
            bound = 10;
        } else if (randomNumberCount < 500) {
            bound = 50;
        } else if (randomNumberCount < 1000) {
            bound = 100;
        } else {
            bound = 150;
        }
        randomNumberCount++;
        if (random.nextBoolean()) {
            return specialNumbers.get(random.nextInt(specialNumbers.size()));
        } else {
            return random.nextInt(bound) - bound / 2;
        }
    }

    public CheckAnswerResult checkAnswer(int problemId, String solution) {
        String lang = "js";
        List<Example> exampleList = exampleService.findExamplesByProblemId(problemId);
        List<Testcase> testcaseList = testcaseService.findTestcasesByProblemId(problemId);

        List<ExampleResult> exampleResult = new ArrayList<>();
        for (Example example : exampleList) {
            try {
                JSONObject jsonBody = compilingService.createDataObject(solution, example.getInput());
                String returnValue = compilingService.postData(jsonBody, lang);
                String result = compilingService.handleResponse(returnValue);
                if (result.equals(example.getOutput())) {
                    exampleResult.add(new ExampleResult(example.getId(), "passed", "with: " + example.getInput() + " and got: " + result));
                } else {
                    exampleResult.add(new ExampleResult(example.getId(), "failed", "with: " + example.getInput() + " but got: " + result));
                }
            } catch (Exception e) {
                throw new DemoGraphqlException("An error occurred: " + e.getMessage(), 400);
            }
        }

        List<TestcaseResult> testcaseResult = new ArrayList<>();
        for (Testcase testcase : testcaseList) {
            try {
                JSONObject jsonBody = compilingService.createDataObject(solution, testcase.getInput());
                String returnValue = compilingService.postData(jsonBody, lang);
                String result = compilingService.handleResponse(returnValue);
                if (result.equals(testcase.getOutput())) {
                    testcaseResult.add(new TestcaseResult(testcase.getId(), "passed", null));
                } else {
                    testcaseResult.add(new TestcaseResult(testcase.getId(), "failed", null));
                }
            } catch (Exception e) {
                throw new DemoGraphqlException("An error occurred: " + e.getMessage(), 400);
            }
        }
        CheckAnswerResult results = new CheckAnswerResult();
        results.setExampleResults(exampleResult);
        results.setTestcaseResults(testcaseResult);
        return results;
    }
}
