package com.senior.dreamteam.services;

import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.model.Result;
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

    public JSONObject createDataObject(String code, JSONArray params) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("code", code);

//        for(Object obj : params) {
//            String strObj = String.valueOf(obj);
//            if(strObj.startsWith("{") && strObj.endsWith("}")) {
//                paramsArray.put(new JSONObject(strObj));
//            }
//            else if(strObj.startsWith("[") && strObj.endsWith("]")) {
//                paramsArray.put(new JSONArray(strObj));
//            }
//            else {
//                paramsArray.put(obj);
//            }
//        }
        System.out.println("params");
        System.out.println(params); //[{"1":"asd","2":"21","3":"asd2"},{"1":"asd","2":"2","3":"aa"}]

        data.put("params", params);
        return data;
    }

    public List<Result> handleResponse(String jsonResponse) {
        List<Result> results = new ArrayList<>();
        try {
            JSONObject responseObject = new JSONObject(jsonResponse);
            JSONArray messageArray = responseObject.getJSONArray("message");

            for (int i = 0; i < messageArray.length(); i++) {
                JSONObject messageObject = messageArray.getJSONObject(i);
                boolean isError = messageObject.getBoolean("isError");
                if (isError) {
                    String errorMessage = messageObject.optString("errorMessage", null);
                    results.add(new Result("", true));
                } else {
                    String result = messageObject.optString("result", null);
                    results.add(new Result(result, false));
                }
            }
        } catch (JSONException e) {
            // If the JSON parsing itself fails, we add a single error result.
            results.add(new Result(null, true));
        }
        return results;
    }
}