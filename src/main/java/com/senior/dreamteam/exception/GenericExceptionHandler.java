package com.senior.dreamteam.exception;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Slf4j
@ControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        String errorMessage = ex.getReason();
        // If there's no custom message, use a default one
        if (errorMessage == null) {
            errorMessage = "There was an error processing your request.";
        }
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", errorMessage);
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(RuntimeException ex) {
        String errorMessage = ex.getMessage();
        // If there's no custom message, use a default one
        if (errorMessage == null) {
            errorMessage = "There was an error processing your request.";
        }
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    //for other Exception that not specified handling
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        String errorMessage = ex.getMessage();
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", errorMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
