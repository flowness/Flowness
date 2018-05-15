package com.flowness.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flowness.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AlertsConfigActivity extends AppCompatActivity implements View.OnClickListener{

    Calendar calZeroFlowStart = Calendar.getInstance();
    TextView etZeroFlowStart;
    TimePickerDialog.OnTimeSetListener tplStart;
    Calendar calZeroFlowEnd = Calendar.getInstance();
    TextView etZeroFlowEnd;
    TimePickerDialog.OnTimeSetListener tplEnd;

    private class myTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        TextView tv;
        Calendar calendar;
        public myTimeSetListener(TextView tv, Calendar calendar) {
            this.tv = tv;
            this.calendar = calendar;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR, hourOfDay);
            calendar.set(Calendar.MINUTE,minute);
            String hourMinute = String.format("%s%d:%s%d", (hourOfDay >= 10 ? "" : "0"), hourOfDay, (minute >= 10 ? "" : "0"), minute);
            tv.setText(hourMinute);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_alerts_config);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etZeroFlowStart = findViewById(R.id.zero_flow_alert_start);
        etZeroFlowStart.setOnClickListener(this);
        tplStart = new myTimeSetListener(etZeroFlowStart, calZeroFlowStart);
        etZeroFlowEnd = findViewById(R.id.zero_flow_alert_end);
        etZeroFlowEnd.setOnClickListener(this);
        tplEnd = new myTimeSetListener(etZeroFlowEnd, calZeroFlowEnd);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(final View v) {

//                DatePicker datePicker = new DatePicker();
//                datePicker.show(getFragmentManager(), "Select date");
        if (v == etZeroFlowStart || v == etZeroFlowEnd) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(AlertsConfigActivity.this, v == etZeroFlowStart ? tplStart : tplEnd, 0, 0, true);
            timePickerDialog.show();
        }
    }

}
