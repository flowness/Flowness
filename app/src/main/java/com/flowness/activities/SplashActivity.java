package com.flowness.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.flowness.utils.SharedPreferencesKeys;
import com.flowness.volley.BasicRequest;
import com.flowness.volley.RegisterRequest;
import com.flowness.volley.UpdateAlertsConfigRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = FirebaseInstanceId.getInstance().getToken();
        String instanceId = FirebaseInstanceId.getInstance().getId();
        // 0 - for private mode
        pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SharedPreferencesKeys.FIB_TOKEN_PREF_KEY, token); // setting String
        editor.apply();
        // Start home activity
        startActivity(new Intent(SplashActivity.this, NavDrawerActivity.class));
        toastVersion();
        registerDevice(instanceId, token);
        // close splash activity
        finish();
    }

    private void registerDevice(String instanceId, String token) {
        String postBody = getRegisterPostBody(instanceId, token);
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
                }).execute(SplashActivity.this);
    }

    private String getRegisterPostBody(String instanceId, String token) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("os", "Android");
        requestBody.addProperty("osVersion", System.getProperty("os.version"));
        requestBody.addProperty("model", android.os.Build.MODEL);
        requestBody.addProperty("instanceId", instanceId);
        requestBody.addProperty("token", token);
        requestBody.addProperty("moduleSN", pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, ""));
        return requestBody.toString();
    }

    private void toastVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            Toast.makeText(SplashActivity.this, String.format("Version is %s(%d)", version, verCode), Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
