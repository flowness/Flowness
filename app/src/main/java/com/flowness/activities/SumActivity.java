package com.flowness.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Date;

public class SumActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvModuleSN;
    private TextView tvSum;
    private Button btnStartDateSelect;
    private Button btnEndDateSelect;
    private Button btnFetch;
    Date dateStart = null;
    Date dateEnd = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum);
        tvStartDate = findViewById(R.id.startDate);
        tvEndDate = findViewById(R.id.endDate);
        tvModuleSN = findViewById(R.id.moduleSN);
        tvSum = findViewById(R.id.sum);
        btnStartDateSelect = findViewById(R.id.selStart);
        btnEndDateSelect = findViewById(R.id.selEnd);
        btnFetch = findViewById(R.id.fetchBtn);
        assert btnStartDateSelect != null;
        assert btnEndDateSelect != null;
        assert btnFetch != null;
        btnStartDateSelect.setOnClickListener(this);
        btnEndDateSelect.setOnClickListener(this);
        btnFetch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

//                DatePicker datePicker = new DatePicker();
//                datePicker.show(getFragmentManager(), "Select date");
        if (v == btnStartDateSelect || v == btnEndDateSelect) {
            final Calendar c = Calendar.getInstance();

            Integer year = c.get(Calendar.YEAR);
            Integer month = c.get(Calendar.MONTH);
            Integer day = c.get(Calendar.DAY_OF_MONTH);
            final TextView textViewToUse = (v == btnStartDateSelect) ? tvStartDate : tvEndDate;

            DatePickerDialog datePickerDialog = new DatePickerDialog(SumActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String date = String.format("%d/%d/%d", dayOfMonth, (monthOfYear + 1), year);
                    textViewToUse.setText(date);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(SumActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String hourMinute = String.format("%s %d:%s%d", textViewToUse.getText().toString(), hourOfDay, (minute >= 10 ? "" : "0"), minute);
                            textViewToUse.setText(hourMinute);
                        }
                    }, 0, 0, true);
                    timePickerDialog.show();
                }
            }, year, month, day);
            datePickerDialog.show();
        } else if (v == btnFetch) {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(this);
            String moduleSNText = tvModuleSN.getText().toString();
            //
//            String strDateStart = tvStartDate.getText().toString();
//            if (!strDateStart.isEmpty()) {
//                DateFormat df = new SimpleDateFormat();
//                try {
//                    dateStart =  df.parse(strDateStart);
//                } catch (ParseException e) {
//                    Toast.makeText(this, "Invalid Date format for Start Date", Toast.LENGTH_LONG).show();
//                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
//                    return;
//                }
//            }
            //
//            String strDateEnd = tvStartDate.getText().toString();
//            if (!strDateEnd.isEmpty()) {
//                DateFormat df = new SimpleDateFormat();
//                try {
//                    dateEnd =  df.parse(strDateEnd);
//                } catch (ParseException e) {
//                    Toast.makeText(this, "Invalid Date format for Start Date", Toast.LENGTH_LONG).show();
//                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
//                    return;
//                }
//            }
            //
            String url = String.format("https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/measurement?moduleSN=%s&returnSum=1", moduleSNText);
            if (dateStart != null) {
                url += ("&startDate=" + dateStart.toString());
            }
            if (dateEnd != null) {
                url += ("&endDate=" + dateEnd.toString());
            }

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseJson = new JSONObject(response);
                                String sum = (String) responseJson.get("body");
                                tvSum.setText(sum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    tvModuleSN.setText("That didn't work!");
                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    private boolean isValidDateString(String strDate) {
        return false;
    }
//    public static class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
//            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
//        }
//        @Override
//        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
//            tvStartDate.setText("Selected Time: " + String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
//        }
//    }

//    public static class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
//            return new DatePickerDialog(getActivity(), this, 2018, 4, 22);
//        }
//        @Override
//        public void onDateSet(android.widget.DatePicker view, int hourOfDay, int minute, int minute1) {
//            tvStartDate.setText("Selected Time: " + String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
//            TimePicker timePicker = new TimePicker();
//            timePicker.show(getFragmentManager(), "Select time");
//        }
//    }
}
