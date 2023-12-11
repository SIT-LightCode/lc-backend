package com.senior.dreamteam.other.services;

import com.senior.dreamteam.exception.DemoGraphqlException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompilingService {
    private final WebClient webClient;

    @Autowired
    public CompilingService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String postData(JSONObject data, String lang) {
        String url = "/compilers/" + lang;
        // Convert JSONObject to String and perform the POST request
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data.toString()) // Convert JSONObject to String
                .retrieve()
                .bodyToMono(String.class)
                .block(); // This will block until the value is available
    }

    public JSONObject createDataObject(String code, List<Object> params) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("code", code);
        JSONArray paramsArray = new JSONArray(params);
        data.put("params", paramsArray);
        return data;
    }

    public List<String> handleResponse(String jsonResponse) {
        try {
            JSONObject responseObject = new JSONObject(jsonResponse);
            JSONArray messageArray = responseObject.getJSONArray("message");
            List<String> results = new ArrayList<>();

            for (int i = 0; i < messageArray.length(); i++) {
                JSONObject messageObject = messageArray.getJSONObject(i);

                // Extract the values from each "message" object
                boolean isError = messageObject.getBoolean("isError");
                if (isError) {
                    String errorMessage = messageObject.isNull("errorMessage") ? null : messageObject.getString("errorMessage");
                    throw new DemoGraphqlException("Got error from response: " + errorMessage);
                }
                results.add(messageObject.getString("result"));
            }
            return results;
        } catch (JSONException e) {
            throw new DemoGraphqlException("Error parsing JSON response: " + e.getMessage());
        }
    }
}