package com.senior.dreamteam.services;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.entities.*;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.repositories.ProblemRepository;
import com.senior.dreamteam.repositories.SubmissionRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProblemService {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestcaseService testcaseService;

    @Autowired
    ExampleService exampleService;

    @Autowired
    CompilingService compilingService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    SubmissionRepository submissionRepository;

    private final int PARAM_GENERATION_COUNT = 1500;
    private int RANDOM_NUMBER_COUNT = 0;


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

        //execute test for exampleParams
        executeAndSaveTest(problemSaved, exampleParametersArray, lang, isExample);

        //execute test for generatedParams
        JSONArray generatedParams = generateParameters(exampleParametersArray.getJSONObject(0), PARAM_GENERATION_COUNT);
        executeAndSaveTest(problemSaved, generatedParams, lang, !isExample);

        return problemRepository.findProblemById(problemSaved.getId()).get();
    }

    public String removeProblemById(String token, int id) {
        try {
            Optional<Problem> problemOptional = problemRepository.findById(id);
            String email = jwtTokenUtil.getUsernameFromToken(token);
            if (problemOptional.isPresent()) {
                if (problemOptional.get().getUser().getEmail() == email || jwtTokenUtil.getAuthoritiesFromToken(token).contains(Roles.ADMIN.name())) {
                    problemRepository.deleteById(id);
                    return "Problem removed successfully";
                }
                throw new DemoGraphqlException("Unable to remove problem");
            }
            return "Problem not found with ID: " + id;
        } catch (Exception e) {
            throw new DemoGraphqlException("An error occurred: " + e.getMessage());
        }
    }

    private JSONArray generateParameters(JSONObject exampleParameters, int count) throws JSONException {
        JSONArray generatedParams = new JSONArray();
        for (int i = 0; i < count; i++) {
            JSONObject newParams = generateSingleSetOfParams(exampleParameters);
            generatedParams.put(newParams);
        }
        RANDOM_NUMBER_COUNT = 0;
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

//    private Object convertParamsToList(JSONObject newParams) throws JSONException {
//        List<Object> paramsList = new ArrayList<>();
//        Iterator<String> keysIterator = newParams.keys();
//        while (keysIterator.hasNext()) {
//            String key = keysIterator.next();
//            Object value = newParams.get(key);
//            paramsList.add(value);
//
//        }
//        return paramsList;
//    }

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
    public void executeAndSaveTest(Problem problem, JSONArray testParams, String lang, Boolean isExample) {
        List<Testcase> testcases = new ArrayList<Testcase>();
        List<Example> examples = new ArrayList<Example>();
        try {
            int batchSize = 200;
            for (int i = 0; i < testParams.length(); i += batchSize) {
                int endIndex = Math.min(testParams.length(), i + batchSize);
                JSONArray batchParams = new JSONArray();
                for (int j = i; j < endIndex; j++) {
                    batchParams.put(testParams.get(j));
                }
                JSONObject jsonBody = compilingService.createDataObject(problem.getSolution(), batchParams);
                String returnValue = compilingService.postData(jsonBody, lang);
                List<String> results = compilingService.handleResponse(returnValue);
                for (int j = 0; j < batchParams.length(); j++) {
                    if (isExample) {
                        Example example = new Example();
                        example.setProblem(problem);
                        example.setInput(batchParams.get(j).toString());
                        example.setOutput(results.get(j));
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
        if (RANDOM_NUMBER_COUNT < 200) {
            bound = 10;
        } else if (RANDOM_NUMBER_COUNT < 500) {
            bound = 50;
        } else if (RANDOM_NUMBER_COUNT < 1000) {
            bound = 100;
        } else {
            bound = 150;
        }
        RANDOM_NUMBER_COUNT++;
        if (random.nextBoolean()) {
            return specialNumbers.get(random.nextInt(specialNumbers.size()));
        } else {
            return random.nextInt(bound) - bound / 2;
        }
    }

    @Transactional
    public CheckAnswerResult checkAnswer(String token, int problemId, String solution) {
        Boolean isCorrect = true;
        String lang = "js";
        List<Example> exampleList = exampleService.findExamplesByProblemId(problemId);
        List<ExampleResult> exampleResult = new ArrayList<>();

        // Define batch size
        int batchSize = 300;

        // Execute in batches
        for (int i = 0; i < exampleList.size(); i += batchSize) {
            int endIndex = Math.min(exampleList.size(), i + batchSize);

            // Create batches for processing
            List<Example> exampleBatch = exampleList.subList(i, endIndex);

            try {
                // Cast List<String> into JSONArray
                JSONArray jsonArray = new JSONArray();
                exampleBatch.stream().map(Example::getInput).forEach(jsonString -> {
                    try {
                        jsonArray.put(new JSONObject(jsonString));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                JSONObject jsonBody = compilingService.createDataObject(solution, jsonArray);
                String returnValue = compilingService.postData(jsonBody, lang);
                List<String> results = compilingService.handleResponse(returnValue);

                int index = 0;
                for (Example example : exampleBatch) {
                    String result = results.get(index);
                    String output = example.getOutput();

                    if (output.equals(result)) {
                        exampleResult.add(new ExampleResult(example.getId(), "passed", "Expected: " + output + ", Got: " + result));
                    } else {
                        exampleResult.add(new ExampleResult(example.getId(), "failed", "Expected: " + output + ", Got: " + result));
                        isCorrect = false;
                    }
                    index++;
                }
            } catch (Exception e) {
                throw new DemoGraphqlException("An error occurred: " + e.getMessage());
            }
        }
// Testcase part
        List<Testcase> testcaseList = testcaseService.findTestcasesByProblemId(problemId);
        List<TestcaseResult> testcaseResult = new ArrayList<>();

        // Execute in batches for testcases
        for (int i = 0; i < testcaseList.size(); i += batchSize) {
            int endIndex = Math.min(testcaseList.size(), i + batchSize);

            // Create batches for processing
            List<Testcase> testcaseBatch = testcaseList.subList(i, endIndex);

            try {
                // Cast List<String> into JSONArray
                JSONArray jsonArray = new JSONArray();
                testcaseBatch.stream().map(Testcase::getInput).forEach(jsonString -> {
                    try {
                        jsonArray.put(new JSONObject(jsonString));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                JSONObject jsonBody = compilingService.createDataObject(solution, jsonArray);

                String returnValue = compilingService.postData(jsonBody, lang);
                List<String> results = compilingService.handleResponse(returnValue);

                int index = 0;
                for (Testcase testcase : testcaseBatch) {
                    String result = results.get(index);
                    String output = testcase.getOutput();

                    // Create TestcaseResult based on returned result
                    if (output.equals(result)) {
                        testcaseResult.add(new TestcaseResult(testcase.getId(), "passed", ""));
                    } else {
                        testcaseResult.add(new TestcaseResult(testcase.getId(), "failed", ""));
                        isCorrect = false;
                    }

                    index++;
                }
            } catch (Exception e) {
                throw new RuntimeException("An error occurred: " + e.getMessage());
            }
        }

        // Create a new instance of CheckAnswerResult
        CheckAnswerResult results = new CheckAnswerResult();
        results.setExampleResults(exampleResult);
        results.setTestcaseResults(testcaseResult);

        if (isCorrect) {
            try {
                User user = jwtTokenUtil.getUserFromToken(token);
                Problem problem = problemRepository.findProblemById(problemId).get();
                submissionRepository.save(Submission.builder().user(user).problem(problem).score(problem.getTotalScore()).code(solution).build());
            } catch (Exception e) {
                log.error("Could not save submission: " + e.getMessage());
                throw new DemoGraphqlException("Could not save submission: " + e.getMessage());
            }
        }

        return results;
    }
}
