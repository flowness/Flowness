package com.flowness.service;

import android.content.SharedPreferences;

import com.flowness.utils.SharedPreferencesKeys;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 *
 */

public class InstanceIdService extends FirebaseInstanceIdService {
    public InstanceIdService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        //sends this token to the server
        sendToServer(FirebaseInstanceId.getInstance().getToken());
    }

    private void sendToServer(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SharedPreferencesKeys.FIB_TOKEN_PREF_KEY, token); // setting String
        editor.apply();
    }
}