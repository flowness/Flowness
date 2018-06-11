package com.aquasafe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.aquasafe.utils.RegistrationUtils;
import com.aquasafe.utils.SharedPreferencesKeys;
import com.google.firebase.iid.FirebaseInstanceId;

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
        RegistrationUtils.registerDevice(instanceId, token, pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, ""), this);
        // close splash activity
        finish();
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
