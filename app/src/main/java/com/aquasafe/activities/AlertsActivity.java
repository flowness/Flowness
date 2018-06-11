package com.aquasafe.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.aquasafe.R;
import com.aquasafe.adapters.AlertsListAdapter;
import com.aquasafe.model.Alert;
import com.aquasafe.utils.DynamoDBUtils;
import com.aquasafe.utils.SharedPreferencesKeys;
import com.aquasafe.volley.BasicRequest;
import com.aquasafe.volley.GetAlertsRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {

    private String savedModuleSN;
    List<Alert> alertsList = new ArrayList<>();
    private AlertsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        savedModuleSN = pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, null); // getting String

        getAlertsArray();

        adapter = new AlertsListAdapter(alertsList, this);

        ListView listView = findViewById(R.id.alerts_list);
        listView.setAdapter(adapter);
    }

    @NonNull
    private void getAlertsArray() {
        fetchAlerts();
//        return new String[]{"Android","IPhone","WindowsMobile","Blackberry",
//                "WebOS","Ubuntu","Windows7","Max OS X"};

    }

    private void fetchAlerts() {
        // Request a string response from the provided URL.
        new GetAlertsRequest(savedModuleSN,
                new BasicRequest.ResponseListener() {
                    @Override
                    public void onResponse(BasicRequest.Response response) {
                        if (response.isSuccess()) {
                            try {
                                JSONObject responseJson = new JSONObject(response.data);
//                            JSONObject responseBody = responseJson.getJSONObject("body");
                                alertsList = new ArrayList<>();
                                JSONArray alertsArray = new JSONArray(responseJson.getString("body"));
                                for (int i = 0; i < alertsArray.length(); i++) {
                                    JSONObject alertJson = alertsArray.getJSONObject(i);
                                    Date alertDate = DynamoDBUtils.getDynamoDBDate(alertJson, "notificationDate");
                                    int alertType = DynamoDBUtils.getDynamoDBInt(alertJson, "notificationType");
                                    boolean alertApproved = DynamoDBUtils.getDynamoDBBool(alertJson, "approved", false);
                                    String alertId = DynamoDBUtils.getDynamoDBString(alertJson, "notificationId");
                                    alertsList.add(new Alert(alertDate, alertType, alertApproved, alertId));
                                }
                                adapter.clear();
                                adapter.addAll(alertsList);
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }).execute(AlertsActivity.this);
    }


}
