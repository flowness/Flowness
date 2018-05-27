package com.flowness.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

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
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            Toast.makeText(SplashActivity.this, String.format("Version is %s(%d)", version, verCode), Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // close splash activity
        finish();
    }
}
