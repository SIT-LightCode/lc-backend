package com.senior.dreamteam.testcase.service;

import com.senior.dreamteam.exception.DemoGraphqlException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

@Service
public class CompilingService {
    private final WebClient webClient;

    @Autowired
    public CompilingService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String postData(JSONObject data, String lang) {
        String url = "/compiling/" + lang;
        // Convert JSONObject to String and perform the POST request
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data.toString()) // Convert JSONObject to String
                .retrieve()
                .bodyToMono(String.class)
                .block(); // This will block until the value is available
    }

    public JSONObject createDataObject(String code, String params) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("code", code);
        data.put("params", new JSONArray(params)); // Assuming params is a JSON array string
        return data;
    }

    public String handleResponse(String jsonResponse) {
        try {
            // Parse the JSON response string
            JSONObject responseObject = new JSONObject(jsonResponse);

            // Get the "message" JSON object
            JSONObject messageObject = responseObject.getJSONObject("message");

            // Extract the values from the "message" object
            boolean isError = messageObject.getBoolean("isError");
            if (isError) {
                String errorMessage = messageObject.isNull("errorMessage") ? null : messageObject.getString("errorMessage");
                throw new DemoGraphqlException("Got error from response: " + errorMessage, 404);
            }
            return messageObject.getString("result");
        } catch (JSONException e) {
            throw new DemoGraphqlException("Error parsing JSON response: " + e.getMessage(), 500);
        }
    }
}