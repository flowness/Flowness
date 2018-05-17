package com.flowness.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.flowness.R;
import com.flowness.utils.JsonConst;
import com.flowness.utils.SharedPreferencesKeys;
import com.flowness.volley.BasicRequest;
import com.flowness.volley.GetAlertsConfigRequest;
import com.flowness.volley.UpdateAlertsConfigRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AlertsConfigActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Switch swFreezeAlert;
    Switch swIrregularityAlert;
    Switch swLeakageAlert;
    Switch swZeroFlowAlert;
    Switch swMonthlyCostAlert;
    Calendar calZeroFlowStart = Calendar.getInstance();
    TextView tvZeroFlowStart;
    TimePickerDialog.OnTimeSetListener tplStart;
    Calendar calZeroFlowEnd = Calendar.getInstance();
    TextView tvZeroFlowEnd;
    TimePickerDialog.OnTimeSetListener tplEnd;
    TextView tvMonthlyCost;
    TextView tvMonthlyCostUnits;
    private String savedMeterSN;
    private int unitsScheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_alerts_config);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvZeroFlowStart = findViewById(R.id.zero_flow_alert_start);
        tplStart = new myTimeSetListener(tvZeroFlowStart, calZeroFlowStart);
        tvZeroFlowEnd = findViewById(R.id.zero_flow_alert_end);
        tplEnd = new myTimeSetListener(tvZeroFlowEnd, calZeroFlowEnd);
        tvMonthlyCost = findViewById(R.id.monthly_cost_alert_amount);
        swFreezeAlert = findViewById(R.id.freeze_alert_switch);
        swIrregularityAlert = findViewById(R.id.irregularity_alert_switch);
        swLeakageAlert = findViewById(R.id.leakage_alert_switch);
        swZeroFlowAlert = findViewById(R.id.zero_flow_alert_switch);
        swMonthlyCostAlert = findViewById(R.id.monthly_cost_alert_switch);
        tvMonthlyCostUnits = findViewById(R.id.monthly_cost_alert_unit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        savedMeterSN = pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, null); // getting String
        unitsScheme = pref.getInt(SharedPreferencesKeys.METER_UNITS_PREF_KEY, 0);
        setValuesFromDB();
        tvMonthlyCostUnits.setText(unitsScheme == 0 ? "Liters" : "Gallons");
        //
        swFreezeAlert.setOnCheckedChangeListener(this);
        swIrregularityAlert.setOnCheckedChangeListener(this);
        swLeakageAlert.setOnCheckedChangeListener(this);
        swZeroFlowAlert.setOnCheckedChangeListener(this);
        swMonthlyCostAlert.setOnCheckedChangeListener(this);
        //
        tvZeroFlowStart.setOnClickListener(this);
        tvZeroFlowEnd.setOnClickListener(this);
        tvMonthlyCost.setOnClickListener(this);


    }

    private void setValuesFromDB() {
        // Request a string response from the provided URL.
        new GetAlertsConfigRequest(savedMeterSN,
                new BasicRequest.ResponseListener() {
                    @Override
                    public void onResponse(BasicRequest.Response response) {
                        if (response.isSuccess()) {
                            try {
                                JSONObject responseJson = new JSONObject(response.data);
//                            JSONObject responseBody = responseJson.getJSONObject("body");
                                JSONObject body = new JSONObject(responseJson.getString("body"));
                                swFreezeAlert.setChecked(getDynamoDBBool(body, "freezeAlert", false));
                                swLeakageAlert.setChecked(getDynamoDBBool(body, "leakageAlert", false));
                                swMonthlyCostAlert.setChecked(getDynamoDBBool(body, "monthlyCostAlert", false));
                                tvMonthlyCost.setText(getDynamoDBString(body, "monthlyCostAlertAmount", "100"));
                                setEnabledLinearLayout(swMonthlyCostAlert.isChecked(), R.id.monthly_cost_alert_config_amount_layout);
                                swIrregularityAlert.setChecked(getDynamoDBBool(body, "irregularityAlert", false));
                                swZeroFlowAlert.setChecked(getDynamoDBBool(body, "zeroFlowHoursAlert", false));
                                String zeroFlowHoursStart = getDynamoDBString(body, "zeroFlowHoursStart", "0000");
                                tvZeroFlowStart.setText(getHourFormatFromString(zeroFlowHoursStart));
                                String zeroFlowHoursEnd = getDynamoDBString(body, "zeroFlowHoursEnd", "0000");
                                tvZeroFlowEnd.setText(getHourFormatFromString(zeroFlowHoursEnd));
                                setEnabledLinearLayout(swZeroFlowAlert.isChecked(), R.id.zero_flow_alert_hours_layout);

                                Log.d("Alerts Config", String.format("Alerts Config: %s", body.toString(2)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    private String getHourFormatFromString(String dbStr) {
                        return String.format("%c%c:%c%c", dbStr.charAt(0), dbStr.charAt(1), dbStr.charAt(2), dbStr.charAt(3));
                    }

                    private boolean getDynamoDBBool(JSONObject jsonObject, String keyName, boolean defaultVal) {
                        try {
                            JSONObject dynamoValue = jsonObject.getJSONObject(keyName);
                            return dynamoValue.getBoolean("BOOL");
                        } catch (JSONException e) {
                            return defaultVal;
                        }
                    }

                    private String getDynamoDBString(JSONObject jsonObject, String keyName, String defaultVal) {
                        try {
                            JSONObject dynamoValue = jsonObject.getJSONObject(keyName);
                            return dynamoValue.getString("S");
                        } catch (JSONException e) {
                            return defaultVal;
                        }
                    }
                }).execute(AlertsConfigActivity.this);
    }

    @Override
    public void onClick(final View v) {

//                DatePicker datePicker = new DatePicker();
//                datePicker.show(getFragmentManager(), "Select date");
        if (v == tvZeroFlowStart || v == tvZeroFlowEnd) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(AlertsConfigActivity.this, v == tvZeroFlowStart ? tplStart : tplEnd, 0, 0, true);
            timePickerDialog.show();
        } else if (v == tvMonthlyCost) {
            RelativeLayout linearLayout = new RelativeLayout(AlertsConfigActivity.this);
            final NumberPicker numberPicker = new NumberPicker(AlertsConfigActivity.this);
            numberPicker.setMaxValue(100000);
            numberPicker.setMinValue(100);
            numberPicker.setValue(Integer.valueOf((String) tvMonthlyCost.getText()));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
            RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            linearLayout.setLayoutParams(params);
            linearLayout.addView(numberPicker, numPickerParams);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlertsConfigActivity.this);
            alertDialogBuilder.setTitle("Select the number");
            alertDialogBuilder.setView(linearLayout);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.e("AlertsConfig", String.format("New Quantity Value : %d", numberPicker.getValue()));
                    tvMonthlyCost.setText(String.format("%d", numberPicker.getValue()));
                    saveValuesToDB();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        saveValuesToDB();
        switch (buttonView.getTag().toString()) {
            case "swfreeze": {
                break;
            }
            case "swirregularity": {
                break;
            }
            case "swleakage": {
                break;
            }
            case "swzeroflow": {
                setEnabledLinearLayout(isChecked, R.id.zero_flow_alert_hours_layout);
                break;
            }
            case "swmonthly": {
                setEnabledLinearLayout(isChecked, R.id.monthly_cost_alert_config_amount_layout);
                break;
            }
        }
    }

    private void saveValuesToDB() {
        String postBody = getPostBody();
        new UpdateAlertsConfigRequest(postBody,
                new BasicRequest.ResponseListener() {
                    @Override
                    public void onResponse(BasicRequest.Response response) {
                        if (response.isSuccess()) {
                            try {
                                JSONObject responseJson = new JSONObject(response.data);
//                            JSONObject responseBody = responseJson.getJSONObject("body");
                                JSONObject body = new JSONObject(responseJson.getString("body"));
                                Log.d("Alerts Config", String.format("Alerts Config saved"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).execute(AlertsConfigActivity.this);
    }

    private String getPostBody() {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("moduleSN", savedMeterSN);
        requestBody.addProperty("freezeAlert", swFreezeAlert.isChecked());
        requestBody.addProperty("irregularityAlert", swIrregularityAlert.isChecked());
        requestBody.addProperty("leakageAlert", swLeakageAlert.isChecked());
        requestBody.addProperty("zeroFlowHoursAlert", swZeroFlowAlert.isChecked());
        requestBody.addProperty("zeroFlowHoursStart", tvZeroFlowStart.getText().toString().replace(":", ""));
        requestBody.addProperty("zeroFlowHoursEnd", tvZeroFlowEnd.getText().toString().replace(":", ""));
        requestBody.addProperty("monthlyCostAlert", swMonthlyCostAlert.isChecked());
        requestBody.addProperty("monthlyCostAlertAmount", tvMonthlyCost.getText().toString());
        return requestBody.toString();
    }

    private void setEnabledLinearLayout(boolean isChecked, int id) {
        LinearLayout layout = findViewById(id);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(isChecked);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class myTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        TextView tv;
        Calendar calendar;

        myTimeSetListener(TextView tv, Calendar calendar) {
            this.tv = tv;
            this.calendar = calendar;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            String hourMinute = String.format("%s%d:%s%d", (hourOfDay >= 10 ? "" : "0"), hourOfDay, (minute >= 10 ? "" : "0"), minute);
            tv.setText(hourMinute);
            saveValuesToDB();
        }
    }

}
