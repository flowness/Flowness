package com.flowness.utils;

import com.flowness.model.Alert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DynamoDBUtils {
    public static String getDynamoDBString(JSONObject jsonObject, String keyName, String defaultVal) {
        try {
            JSONObject dynamoValue = jsonObject.getJSONObject(keyName);
            return dynamoValue.getString("S");
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public static String getDynamoDBString(JSONObject jsonObject, String keyName) {
        return getDynamoDBString(jsonObject, keyName, null);
    }

    public static int getDynamoDBInt(JSONObject jsonObject, String keyName) {
        try {
            JSONObject dynamoValue = jsonObject.getJSONObject(keyName);
            return Integer.valueOf(dynamoValue.getString("N"));
        } catch (JSONException e) {
            return -1;
        }
    }

    public static double getDynamoDBFloat(JSONObject jsonObject, String keyName) {
        try {
            JSONObject dynamoValue = jsonObject.getJSONObject(keyName);
            return Double.valueOf(dynamoValue.getString("N"));
        } catch (JSONException e) {
            return -1;
        }
    }

    public static boolean getDynamoDBBool(JSONObject jsonObject, String keyName, boolean defaultVal) {
        try {
            JSONObject dynamoValue = jsonObject.getJSONObject(keyName);
            return dynamoValue.getBoolean("BOOL");
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public static Date getDynamoDBDate(JSONObject jsonObject, String keyName) {
        try {
            JSONObject dynamoValue = jsonObject.getJSONObject(keyName);
            String dateStr = dynamoValue.getString("S");
            return DateUtils.getDate(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

}
