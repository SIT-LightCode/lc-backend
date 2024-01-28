package com.senior.dreamteam.services;

import com.senior.dreamteam.entities.Example;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.entities.CheckAnswerResult;
import com.senior.dreamteam.entities.ExampleResult;
import com.senior.dreamteam.entities.Problem;
import com.senior.dreamteam.entities.TestcaseResult;
import com.senior.dreamteam.repositories.ProblemRepository;
import com.senior.dreamteam.entities.Testcase;
import org.json.JSONException;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Problem upsertProblem(Problem problem) throws JSONException {
        if (problem.getId() != 0) {
            return problemRepository.save(problem);
        }
        String lang = "js";
        Boolean isExample = true;
        Problem problemSaved = problemRepository.save(problem);
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
            throw new DemoGraphqlException("An error occurred: " + e.getMessage());
        }
    }

    private List<Object> generateParameters(JSONObject exampleParameters, int count) throws JSONException {
        List<Object> generatedParams = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            JSONObject newParams = generateSingleSetOfParams(exampleParameters);
            generatedParams.add(convertParamsToList(newParams));
        }
        randomNumberCount = 0;
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

    private Object convertParamsToList(JSONObject newParams) throws JSONException {
        List<Object> paramsList = new ArrayList<>();
        Iterator<String> keysIterator = newParams.keys();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            Object value = newParams.get(key);
            paramsList.add(value);

        }
        return paramsList;
    }

//    private Object parseValue(Object value) throws JSONException {
//        if (value instanceof String && ((String) value).startsWith("[")) {
//            JSONArray jsonArray = new JSONArray((String) value);
//            return jsonArrayToList(jsonArray);
//        }
//        return value;
//    }

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

    @Transactional
    public void executeAndSaveTest(Problem problem, List<Object> testParams, String lang, Boolean isExample) {
        List<Testcase> testcases = new ArrayList<Testcase>();
        List<Example> examples = new ArrayList<Example>();
        try {
            int batchSize = 300;
            for (int i = 0; i < testParams.size(); i += batchSize) {
                int endIndex = Math.min(testParams.size(), i + batchSize);
                List<Object> batchParams = testParams.subList(i, endIndex);
                JSONObject jsonBody = compilingService.createDataObject(problem.getSolution(), batchParams);
                String returnValue = compilingService.postData(jsonBody, lang);
                List<String> results = compilingService.handleResponse(returnValue);
                for (int j = 0; j < batchParams.size(); j++) {
                    if (isExample) {
                        Example example = new Example();
                        example.setProblem(problem);
                        example.setInput(testParams.get(i + j).toString());
                        example.setOutput(results.get(i + j));
                        examples.add(example);
                    } else {
                        Testcase testcase = new Testcase();
                        testcase.setProblem(problem);
                        testcase.setInput(batchParams.get(j).toString());
                        testcase.setOutput(results.get(j));
                        testcases.add(testcase);
                    }
                }
            }
        } catch (Exception e) {
            handleTestcaseError(problem, e);
            return;
        }
        // save all test cases and examples at once
        if (!examples.isEmpty()) {
            exampleService.saveAll(examples);
        }
        if (!testcases.isEmpty()) {
            testcaseService.saveAll(testcases);
        }
    }

    @Transactional
    public void handleTestcaseError(Problem problem, Exception e) {
        try {
            problemRepository.deleteById(problem.getId());
        } catch (Exception ex) {
            throw new DemoGraphqlException("An error occurred: " + e.getMessage());
        }
        throw new DemoGraphqlException("An error occurred: " + e.getMessage());
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
            JSONObject newObject = new JSONObject();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                newObject.put(key, generateNewValue(value));
            }
            return newObject.toString();
        } else if (param instanceof Number) {
            return generateRandomNumber();
        } else if (param instanceof String) {
            return UUID.randomUUID().toString();
        } else {
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

    @Transactional
    public CheckAnswerResult checkAnswer(int problemId, String solution) {
        String lang = "js";
        List<Example> exampleList = exampleService.findExamplesByProblemId(problemId);
        List<Testcase> testcaseList = testcaseService.findTestcasesByProblemId(problemId);

        List<ExampleResult> exampleResult = new ArrayList<>();
        List<TestcaseResult> testcaseResult = new ArrayList<>();

        // Define batch size
        int batchSize = 300;

        // Execute in batches
        for (int i = 0; i < exampleList.size(); i += batchSize) {
            int endIndex = Math.min(exampleList.size(), i + batchSize);

            // Create batches for processing
            List<Example> exampleBatch = exampleList.subList(i, endIndex);

            try {
                List<Object> paramList = exampleBatch.stream()
                        .map(example -> example.getInput())
                        .collect(Collectors.toList());
                JSONObject jsonBody = compilingService.createDataObject(solution, paramList);
                String returnValue = compilingService.postData(jsonBody, lang);
                List<String> results = compilingService.handleResponse(returnValue);

                int index = 0;
                for (String result : results) {
                    if (result.equals(exampleBatch.get(index).getOutput())) {
                        exampleResult.add(new ExampleResult(exampleBatch.get(index).getId(), "passed", "with: " + exampleBatch.get(index).getInput() + " and got: " + result));
                    } else {
                        exampleResult.add(new ExampleResult(exampleBatch.get(index).getId(), "failed", "with: " + exampleBatch.get(index).getInput() + " but got: " + result));
                    }
                    index++;
                }
            } catch (Exception e) {
                throw new DemoGraphqlException("An error occurred: " + e.getMessage());
            }
        }
        // Execute Testcase list in batches
        for (int i = 0; i < testcaseList.size(); i += batchSize) {
            int endIndex = Math.min(testcaseList.size(), i + batchSize);

            // Create batches for processing
            List<Testcase> testcaseBatch = testcaseList.subList(i, endIndex);

            try {
                List<Object> paramList = testcaseBatch.stream()
                        .map(testcase -> testcase.getInput()).collect(Collectors.toList());
                JSONObject jsonBody = compilingService.createDataObject(solution, paramList);
                String returnValue = compilingService.postData(jsonBody, lang);
                List<String> results = compilingService.handleResponse(returnValue);

                int index = 0;
                for (String result : results) {
                    if (result.equals(testcaseBatch.get(index).getOutput())) {
                        testcaseResult.add(new TestcaseResult(testcaseBatch.get(index).getId(), "passed", null));
                    } else {
                        testcaseResult.add(new TestcaseResult(testcaseBatch.get(index).getId(), "failed", null));
                    }
                    index++;
                }

            } catch (Exception e) {
                throw new DemoGraphqlException("An error occurred: " + e.getMessage());
            }
        }

        CheckAnswerResult results = new CheckAnswerResult();
        results.setExampleResults(exampleResult);
        results.setTestcaseResults(testcaseResult);
        return results;
    }
}
