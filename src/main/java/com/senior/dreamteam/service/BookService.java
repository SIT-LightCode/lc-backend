package com.senior.dreamteam.service;

import com.senior.dreamteam.model.ResponseMessage;
import com.senior.dreamteam.model.TestCase;
import org.graalvm.polyglot.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private ResponseMessage responseMessage = new ResponseMessage("");

    public ResponseMessage testAnswer(Integer questionId, String answerCode){
        try (Context context = Context.create()) {
            Value functionValue = context.eval("js", answerCode);
            for (TestCase testCase : getTestCase()) {
                String param = testCase.getParam();
                String expected = testCase.getResult();
                Value resultValue = functionValue.execute(Integer.parseInt(param));
                String result = resultValue.asString();
                if (result.equals(expected)) {
                    System.out.println("Test case PASSED: Param: " + param + ", Result: " + result);
                } else {
                    System.out.println("Test case FAILED: Param: " + param + ", Expected: " + expected + ", Actual: " + result);
                }
            }
            responseMessage.setMessage("Passed");
            return responseMessage;
        } catch (PolyglotException e) {
            e.printStackTrace();
        } catch (Exception e){
            System.out.println(e);
        }
        responseMessage.setMessage("Failed");
        return responseMessage;
    }

    public List<TestCase> getTestCase(){
        List<TestCase> testCases = new ArrayList<>();
        testCases.add(new TestCase("1", "I"));
        testCases.add(new TestCase("7", "VII"));
        testCases.add(new TestCase("21", "XXI"));
        testCases.add(new TestCase("49", "XLIX"));
        testCases.add(new TestCase("99", "XCIX"));
        return testCases;
    }
    public String getSampleCode(){
        return """
                var main = function(s) {
                    let result = ""
                    while (s > 0) {
                        if (s >= 1000) {
                            result += "M"
                            s -= 1000
                        } else if (s >= 900) {
                            result += "CM"
                            s -= 900
                        } else if (s >= 500) {
                            result += "D"
                            s -= 500
                        } else if (s >= 400) {
                            result += "CD"
                            s -= 400
                        } else if (s >= 100) {
                            result += "C"
                            s -= 100
                        } else if (s >= 90) {
                            result += "XC"
                            s -= 90
                        } else if (s >= 50) {
                            result += "L"
                            s -= 50
                        } else if (s >= 40) {
                            result += "XL"
                            s -= 40
                        } else if (s >= 10) {
                            result += "X"
                            s -= 10
                        } else if (s >= 9) {
                            result += "IX"
                            s -= 9
                        } else if (s >= 5) {
                            result += "V"
                            s -= 5
                        } else if (s >= 4) {
                            result += "IV"
                            s -= 4
                        } else {
                            result += "I"
                            s -= 1
                        }
                                
                    };
                    return result
                }
                main
                """.strip();
    }

}
