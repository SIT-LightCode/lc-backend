package com.senior.dreamteam.validation;

import com.senior.dreamteam.exception.DemoGraphqlException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class GenericValidation {
    // Validate a string with a minimum and maximum length
    public void validateIsEmptyToken(String token) {
        if (token.isEmpty()) {
            log.error("Unauthorized token");
            throw new DemoGraphqlException("Unauthorized");
        }

    }

    // Validate an integer with a minimum and maximum value
    public void validateParameter(int value, int min, int max) {
        if (value < min || value > max) {
            throw new DemoGraphqlException("Integer value out of range: " + value);
        }
    }

    // Validate a string with a minimum and maximum length
    public void validateParameter(String value, int minLength, int maxLength) {
        if (value == null || value.length() < minLength || value.length() > maxLength) {
            throw new DemoGraphqlException("String length out of range: " + value);
        }

    }
}