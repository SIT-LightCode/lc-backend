package com.senior.dreamteam.services;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.entities.*;
import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.model.Result;
import com.senior.dreamteam.repositories.ProblemRepository;
import com.senior.dreamteam.repositories.SubmissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    private final int PARAM_GENERATION_COUNT = 700;
    private int RANDOM_NUMBER_COUNT = 0;


    public List<Problem> findAll() {
        return problemRepository.findByEnableTrue();
    }

    public Optional<Problem> findAllById(int id) {
        return problemRepository.findProblemByIdAndEnableTrue(id);
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
        return problemRepository.findProblemByIdAndEnableTrue(problemSaved.getId()).get();
    }

    @Transactional
    public String removeProblemById(String token, int id) {
        try {
            Optional<Problem> problemOptional = problemRepository.findById(id);
            String email = jwtTokenUtil.getUsernameFromToken(token);
            if (problemOptional.isPresent()) {
                if (jwtTokenUtil.getAuthoritiesFromToken(token).contains(Roles.ADMIN.name()) || problemOptional.get().getUser().getEmail().equals(email)) {
                    problemRepository.disableProblemById(id);
//                    problemRepository.deleteById(id);
                    return "Problem removed successfully";
                }
                log.info("This {} try to remove problem id from this {}", email, problemOptional.get().getUser().getEmail());
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
            int numOfError = 0;
            for (int i = 0; i < testParams.length(); i += batchSize) {
                int endIndex = Math.min(testParams.length(), i + batchSize);
                JSONArray batchParams = new JSONArray();
                for (int j = i; j < endIndex; j++) {
                    batchParams.put(testParams.get(j));
                }
                JSONObject jsonBody = compilingService.createDataObject(problem.getSolution(), batchParams);
                String returnValue = compilingService.postData(jsonBody, lang);
                List<Result> results = compilingService.handleResponse(returnValue);
                for (int j = 0; j < batchParams.length(); j++) {
                    if (results.get(j).isError()) {
                        numOfError++;
                        continue;
                    }
                    if (isExample) {
                        Example example = new Example();
                        example.setProblem(problem);
                        example.setInput(batchParams.get(j).toString());
                        example.setOutput(results.get(j).getOutput());
                        examples.add(example);
                    } else {
                        Testcase testcase = new Testcase();
                        testcase.setProblem(problem);
                        testcase.setInput(batchParams.get(j).toString());
                        testcase.setOutput(results.get(j).getOutput());
                        testcases.add(testcase);
                    }
                }
                if (Math.ceil(testParams.length() * 90 / 100) < numOfError) {
                    throw new DemoGraphqlException("Cannot create wrong solution for this problem");
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
            return newArray;
        } else if (param instanceof JSONObject jsonObject) {
            JSONObject newObject = new JSONObject();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                newObject.put(key, generateNewValue(value));
            }
            return newObject;
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
        boolean isCorrect = true;
        String lang = "js";
        List<Example> exampleList = exampleService.findExamplesByProblemId(problemId);
        List<ExampleResult> exampleResult = new ArrayList<>();
        List<TestcaseResult> testcaseResult = new ArrayList<>();

        // Define batch size
        int batchSize = 300;

        // Execute in batches for examples
        for (int i = 0; i < exampleList.size(); i += batchSize) {
            int endIndex = Math.min(exampleList.size(), i + batchSize);
            List<Example> exampleBatch = exampleList.subList(i, endIndex);

            try {
                JSONArray jsonArray = new JSONArray();
                for (Example example : exampleBatch) {
                    System.out.println(example.getInput());

                    jsonArray.put(new JSONObject(example.getInput()));
                }
                JSONObject jsonBody = compilingService.createDataObject(solution, jsonArray);
                System.out.println(jsonBody);
                String returnValue = compilingService.postData(jsonBody, lang);
                List<Result> results = compilingService.handleResponse(returnValue);

                for (int j = 0; j < exampleBatch.size(); j++) {
                    Example example = exampleBatch.get(j);
                    Result result = results.get(j);
                    if (result.isError()) {
                        throw new DemoGraphqlException("There was some error with your code");
                    }
                    String output = example.getOutput().trim();
                    if (output.equals(result.getOutput().trim())) {
                        exampleResult.add(new ExampleResult(example.getId(), "passed", ""));
                    } else {
                        exampleResult.add(new ExampleResult(example.getId(), "failed", "Expected: " + output + ", Got: " + result.getOutput()));
                        isCorrect = false;
                    }
                }
            } catch (Exception e) {
                throw new DemoGraphqlException("An error occurred during example processing: " + e.getMessage());
            }
        }

        // Same approach for testcases
        List<Testcase> testcaseList = testcaseService.findTestcasesByProblemId(problemId);

        // Execute in batches for testcases
        for (int i = 0; i < testcaseList.size(); i += batchSize) {
            int endIndex = Math.min(testcaseList.size(), i + batchSize);
            List<Testcase> testcaseBatch = testcaseList.subList(i, endIndex);

            try {
                JSONArray jsonArray = new JSONArray();
                for (Testcase testcase : testcaseBatch) {
                    jsonArray.put(new JSONObject(testcase.getInput()));
                }
                JSONObject jsonBody = compilingService.createDataObject(solution, jsonArray);
                String returnValue = compilingService.postData(jsonBody, lang);
                List<Result> results = compilingService.handleResponse(returnValue);

                for (int j = 0; j < testcaseBatch.size(); j++) {
                    Testcase testcase = testcaseBatch.get(j);
                    Result result = results.get(j);


                    String output = testcase.getOutput().trim();
                    if (output.equals(result.getOutput().trim())) {
                        testcaseResult.add(new TestcaseResult(testcase.getId(), "passed", ""));
                    } else {
                        testcaseResult.add(new TestcaseResult(testcase.getId(), "failed", "Expected: " + output + ", Got: " + result.getOutput()));
                        isCorrect = false;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("An error occurred during testcase processing: " + e.getMessage());
            }
        }

        // Handling submissions
        if (isCorrect) {
            try {
                Submission submission = new Submission();
                User user = jwtTokenUtil.getUserFromToken(token);
                Problem problem = problemRepository.findProblemByIdAndEnableTrue(problemId).get();
                submission.setProblem(problem);
                submission.setUser(user);
                submission.setCode(solution);
                Boolean isNewSubmission = submissionRepository.findFirstByUserAndProblem(user, problem).isEmpty();
                if (problem.getIsOfficial()) {
                    //add skill if it news
                    List<Skill> userSkills = user.getSkill();
                    List<TagProblem> tagProblems = problem.getTagProblem();
                    int levelProblem = problem.getLevel();
                    for (TagProblem tagProblem : tagProblems) {
                        Tag tag = tagProblem.getTag();
                        boolean foundSkillToUpdate = false;
                        for (Skill userSkill : userSkills) {
                            if (tag.getId() == userSkill.getTag().getId()) {
                                foundSkillToUpdate = true;
                                if (levelProblem > userSkill.getLevel()) {
                                    userSkill.setLevel(levelProblem);
                                    userSkills.set(userSkills.indexOf(userSkill), userSkill);
                                }
                                break;
                            }
                        }
                        // If the skill for the tag was not found in the user's skill list, add it
                        if (!foundSkillToUpdate) {
                            Skill newSkill = Skill.builder()
                                    .tag(tag)
                                    .level(levelProblem)
                                    .user(user)
                                    .build();
                            userSkills.add(newSkill);
                        }
                        user.setSkill(userSkills);
                    }

                    if (isNewSubmission) {
                        //save submission
                        submission.setScore(problem.getTotalScore());
                        submission.setScoreUnOfficial(0);
                        submissionRepository.save(submission);
//                        submissionRepository.save(Submission.builder().problem(problem).user(user).code(solution).score(problem.getTotalScore()).scoreUnOfficial(0).build());
                    } else {
                        // If not new, create a submission with a score of 0?
                        submission.setScore(0);
                        submission.setScoreUnOfficial(0);
                        submissionRepository.save(submission);
//                        submissionRepository.save(Submission.builder().problem(problem).user(user).code(solution).score(0).scoreUnOfficial(0).build());
                    }
                }
                if (isNewSubmission) {
                    //save submission
                    submission.setScore(0);
                    submission.setScoreUnOfficial(problem.getTotalScore());
                    submissionRepository.save(submission);
//                    submissionRepository.save(Submission.builder().problem(problem).user(user).code(solution).score(0).scoreUnOfficial(problem.getTotalScore()).build());
                } else {
                    // If not new, create a submission with a score of 0?
                    submission.setScore(0);
                    submission.setScoreUnOfficial(0);
                    submissionRepository.save(submission);
//                    submissionRepository.save(Submission.builder().problem(problem).user(user).code(solution).score(0).scoreUnOfficial(0).build());
                }
            } catch (Exception e) {
                log.error("Could not handling submission: " + e.getMessage());
                throw new DemoGraphqlException("Could not handling submission: " + e.getMessage());
            }
        }

        CheckAnswerResult answerResults = new CheckAnswerResult(exampleResult, testcaseResult);
        return answerResults;
    }
}
