package com.senior.dreamteam.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckAnswerResult {
    private List<ExampleResult> exampleResults;
    private List<TestcaseResult> testcaseResults;

}
