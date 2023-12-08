package com.senior.dreamteam.problem.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TestcaseResult {
    private int id;
    private String status;
    private String message;
}
