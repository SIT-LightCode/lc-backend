package com.senior.dreamteam.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCase {
    private String param;
    private String result;

    public TestCase(String param, String result) {
        this.param = param;
        this.result = result;
    }
}