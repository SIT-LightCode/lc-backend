package com.senior.dreamteam.problem.service;

import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.problem.repository.ProblemRepository;
import com.senior.dreamteam.testcase.entity.Testcase;
import com.senior.dreamteam.testcase.repository.TestcaseRepository;
import com.senior.dreamteam.testcase.service.TestcaseService;
import lombok.val;
import org.json.JSONException;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.graalvm.polyglot.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptEngine;


@Service
public class ProblemService {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestcaseService testcaseService;
    public List<Problem> findAll(){
        return problemRepository.findAll();
    }

    public Optional<Problem> findAllById(int id){
        return problemRepository.findProblemById(id);
    }

    public Problem upsertProblem(Problem problem) {
        try (Context context = Context.create()) {
            JSONObject typeParameters = new JSONObject(problem.getTypeParameter());
            System.out.println("before json");

            // Prepare the JavaScript function
            context.eval("js", problem.getSolution());

            // Get the JavaScript function
            Value jsFunction = context.getBindings("js").getMember("main");

            // Generate a thousand sets of parameters
            List<Object> generatedParams = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                JSONObject newParams = new JSONObject();
                Iterator keys = typeParameters.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object param = new JSONTokener(typeParameters.getString(key)).nextValue();
                    Object newParam = generateNewValue(param);
                    newParams.put(key, newParam);
                }
                generatedParams.add(newParams);
            }

            // Execute the JavaScript function with the generated parameters
            for (Object params : generatedParams) {
                JSONObject paramsObj = (JSONObject) params;
                Value[] args = new Value[paramsObj.length()];
                int index = 0;
                Iterator keys = paramsObj.keys();
                System.out.println("paramsObj: " + paramsObj);
                System.out.println("keys: " + keys);
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    args[index++] = Value.asValue(paramsObj.get(key));
                }
                System.out.println("args: " + args);
                Value result = jsFunction.execute(args);
                System.out.println("params: " + params.toString());
                System.out.println(result.toString());
                // Save the result to the database
                Problem problemSaved = problemRepository.save(problem);
                try {
                    // Save the testcase with the generated parameters and result
                    // Testcase testcase = new Testcase(null, params.toString(), result.toString(), problemSaved);
                    // testcaseService.upsertTestcase(testcase);
                } catch (Exception e){
                    problemRepository.deleteById(problemSaved.getId());
                }
            }
            return null;
        } catch (PolyglotException e) {
            System.out.println("catch poly");
            e.printStackTrace();
        } catch (Exception e){
            System.out.println(e);
        }
        return problemRepository.save(problem);
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
            return new Random().nextInt(10);
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
