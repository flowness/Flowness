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
import android.widget.Toast;

import com.flowness.R;
import com.flowness.utils.SharedPreferencesKeys;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_alerts_config);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvZeroFlowStart = findViewById(R.id.zero_flow_alert_start);
        tvZeroFlowStart.setOnClickListener(this);
        tplStart = new myTimeSetListener(tvZeroFlowStart, calZeroFlowStart);
        tvZeroFlowEnd = findViewById(R.id.zero_flow_alert_end);
        tvZeroFlowEnd.setOnClickListener(this);
        tplEnd = new myTimeSetListener(tvZeroFlowEnd, calZeroFlowEnd);
        tvMonthlyCost = findViewById(R.id.monthly_cost_alert_amount);
        tvMonthlyCost.setOnClickListener(this);
        swFreezeAlert = findViewById(R.id.freeze_alert_switch);
        swFreezeAlert.setOnCheckedChangeListener(this);
        swIrregularityAlert = findViewById(R.id.irregularity_alert_switch);
        swIrregularityAlert.setOnCheckedChangeListener(this);
        swLeakageAlert = findViewById(R.id.leakage_alert_switch);
        swLeakageAlert.setOnCheckedChangeListener(this);
        swZeroFlowAlert = findViewById(R.id.zero_flow_alert_switch);
        swZeroFlowAlert.setOnCheckedChangeListener(this);
        swMonthlyCostAlert = findViewById(R.id.monthly_cost_alert_switch);
        swMonthlyCostAlert.setOnCheckedChangeListener(this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        tvMonthlyCostUnits = findViewById(R.id.monthly_cost_alert_unit);
        tvMonthlyCostUnits.setText(pref.getInt(SharedPreferencesKeys.METER_UNITS_PREF_KEY, 0) == 0 ? "Liters" : "Gallons");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setValuesFromDB();
    }

    private void setValuesFromDB() {
        
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
        switch (buttonView.getTag().toString()) {
            case "swfreeze": {
                if (isChecked) {
                    Toast.makeText(AlertsConfigActivity.this,
                            "Switch On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlertsConfigActivity.this,
                            "Switch Off", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "swirregularity": {
                if (isChecked) {
                    Toast.makeText(AlertsConfigActivity.this,
                            "Switch On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlertsConfigActivity.this,
                            "Switch Off", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "swleakage": {
                if (isChecked) {
                    Toast.makeText(AlertsConfigActivity.this,
                            "Switch On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlertsConfigActivity.this,
                            "Switch Off", Toast.LENGTH_SHORT).show();
                }
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
        }
    }

}
