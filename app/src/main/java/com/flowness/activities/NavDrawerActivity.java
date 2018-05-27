package com.flowness.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flowness.R;
import com.flowness.utils.DateUtils;
import com.flowness.utils.SharedPreferencesKeys;
import com.flowness.volley.BasicRequest;
import com.flowness.volley.GetCountTotalPerModuleRequest;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvCounter;
    private TextView tvLastMeasure;
    private ScheduledThreadPoolExecutor sch;
    //
    private TextView tvMonthAmount;
    private TextView tvMonthCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvMonthAmount = findViewById(R.id.monthly_amount);
        tvMonthCost = findViewById(R.id.monthly_cost);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tvMeterName = navigationView.getHeaderView(0).findViewById(R.id.meter_name_txt);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        String savedMeterSN = pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, null); // getting String
        if (!(savedMeterSN == null || savedMeterSN.isEmpty())) {
            tvMeterName.setText(String.format("Current Meter: %s", savedMeterSN));
        }

        TextView tvLastLogin = navigationView.getHeaderView(0).findViewById(R.id.last_login_txt);
        long lastLogin = pref.getLong(SharedPreferencesKeys.LAST_LOGIN_PREF_KEY, 0); // getting long
        if (lastLogin > 0) {
            tvLastLogin.setText(String.format("Last login: %s", convertDateToString(new Date(lastLogin))));
        }
        //
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(SharedPreferencesKeys.LAST_LOGIN_PREF_KEY, System.currentTimeMillis()); // setting String
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCounter();
    }

    private void setMonthlyCost() {
        View navDrawer = findViewById(R.id.app_bar_nav_drawer);
        tvCounter = navDrawer.findViewById(R.id.counter);
        tvLastMeasure = navDrawer.findViewById(R.id.last_measure);

        // Request a string response from the provided URL.
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        final String savedMeterSN = pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, null); // getting String
        final int unitCost = pref.getInt(SharedPreferencesKeys.UNIT_PRICE_PREF_KEY, 1); // getting int

        //TODO: change to range count
        new GetCountTotalPerModuleRequest(savedMeterSN,
                new BasicRequest.ResponseListener() {
                    @Override
                    public void onResponse(BasicRequest.Response response) {
                        if (response.isSuccess()) {
                            try {
                                JSONObject responseJson = new JSONObject(response.data);
//                            JSONObject responseBody = responseJson.getJSONObject("body");
                                JSONObject body = new JSONObject(responseJson.getString("body"));
                                String count = body.getJSONObject("totalCount").getString("N");
                                tvMonthAmount.setText(count);
                                tvMonthCost.setText(String.valueOf(Float.valueOf(count) * unitCost));
                                Log.d("Counter", "total response");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).execute(NavDrawerActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMonthlyCost();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sch != null) {
            sch.shutdownNow();
            sch = null;
        }
    }

    private void setCounter() {
        View navDrawer = findViewById(R.id.app_bar_nav_drawer);
        tvCounter = navDrawer.findViewById(R.id.counter);
        tvLastMeasure = navDrawer.findViewById(R.id.last_measure);

        // Request a string response from the provided URL.
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        final String savedMeterSN = pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, null); // getting String

        if (sch == null) {
            sch = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

            Runnable delayTask = new Runnable() {
                @Override
                public void run() {
                    try {
                        new GetCountTotalPerModuleRequest(savedMeterSN,
                                new BasicRequest.ResponseListener() {
                                    @Override
                                    public void onResponse(BasicRequest.Response response) {
                                        if (response.isSuccess()) {
                                            try {
                                                JSONObject responseJson = new JSONObject(response.data);
//                            JSONObject responseBody = responseJson.getJSONObject("body");
                                                JSONObject body = new JSONObject(responseJson.getString("body"));
                                                String count = body.getJSONObject("totalCount").getString("N");
                                                String date = body.getJSONObject("measurementDate").getString("S");
                                                tvCounter.setText(count);
                                                tvLastMeasure.setText(String.format("Last Update: %s", getDateFromString(date)));
                                                Log.d("Counter", "total response");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).execute(NavDrawerActivity.this);
                    } catch (Exception e) {

                    }
                }
            };


            ScheduledFuture<?> delayFuture = sch.scheduleWithFixedDelay(delayTask, 0, 5, TimeUnit.SECONDS);
        }
//        String url = String.format("https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/total?moduleSN=%s", savedMeterSN);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject responseJson = new JSONObject(response);
////                            JSONObject responseBody = responseJson.getJSONObject("body");
//                            JSONObject body = new JSONObject(responseJson.getString("body"));
//                            String count = body.getJSONObject("totalCount").getString("N");
//                            String date = body.getJSONObject("measurementDate").getString("S");
//                            tvCounter.setText(count);
//                            tvLastMeasure.setText(String.format("Last Update: %s", getDateFromString(date)));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                tvCounter.setText("That didn't work!");
//                tvLastMeasure.setText("");
//                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
//            }
//        });
//
//        // Add the request to the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(stringRequest);
    }

    private String getDateFromString(String dateStr) {
        try {
            Date date = DateUtils.getDate(dateStr);
            return convertDateToString(date);
        } catch (ParseException e) {
            return "N/A";
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meter_settings) {
            startActivity(new Intent(NavDrawerActivity.this, MeterSettingsActivity.class));
        } else if (id == R.id.nav_sum_reading) {
            startActivity(new Intent(NavDrawerActivity.this, SumActivity.class));
        } else if (id == R.id.nav_reading_graph) {
            startActivity(new Intent(NavDrawerActivity.this, StatsActivity.class));
        } else if (id == R.id.nav_alerts_config) {
            startActivity(new Intent(NavDrawerActivity.this, AlertsConfigActivity.class));
        } else if (id == R.id.nav_alerts) {
            startActivity(new Intent(NavDrawerActivity.this, AlertsActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"my.flow.ness@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hi Your Flowness!");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String convertDateToString(Date indate) {
        String dateString = null;
        DateFormat sdfr = new SimpleDateFormat("d-MMM H:mm");
        try {
            dateString = sdfr.format(indate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dateString;
    }
}
