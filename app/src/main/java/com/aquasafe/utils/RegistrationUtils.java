package com.aquasafe.utils;

import android.content.Context;
import android.util.Log;

import com.aquasafe.activities.SplashActivity;
import com.aquasafe.volley.BasicRequest;
import com.aquasafe.volley.RegisterRequest;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class RegistrationUtils {
    public static void registerDevice(String instanceId, String token, String moduleSN, Context context) {
        String postBody = getRegisterPostBody(instanceId, token, moduleSN);
        new RegisterRequest(postBody,
                new BasicRequest.ResponseListener() {
                    @Override
                    public void onResponse(BasicRequest.Response response) {
                        if (response.isSuccess()) {
                            try {
                                JSONObject responseJson = new JSONObject(response.data);
//                            JSONObject responseBody = responseJson.getJSONObject("body");
                                JSONObject body = new JSONObject(responseJson.getString("body"));
                                Log.d("Registration", String.format("Succeeded"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).execute(context);
    }

    private static String getRegisterPostBody(String instanceId, String token, String moduleSN) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("os", "Android");
        requestBody.addProperty("osVersion", System.getProperty("os.version"));
        requestBody.addProperty("model", android.os.Build.MODEL);
        requestBody.addProperty("instanceId", instanceId);
        requestBody.addProperty("token", token);
        requestBody.addProperty("moduleSN", moduleSN);
        return requestBody.toString();
    }
}
