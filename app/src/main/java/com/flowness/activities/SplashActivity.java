package com.flowness.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.flowness.utils.SharedPreferencesKeys;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SharedPreferencesKeys.FIB_TOKEN_PREF_KEY, token); // setting String
        editor.apply();
        // Start home activity
        startActivity(new Intent(SplashActivity.this, NavDrawerActivity.class));
        // close splash activity
        finish();
    }
}
