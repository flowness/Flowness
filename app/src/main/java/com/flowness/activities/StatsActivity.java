package com.flowness.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.flowness.R;
import com.flowness.model.ChartOptions;
import com.flowness.utils.DateUtils;
import com.flowness.utils.JsonConst;
import com.flowness.utils.SharedPreferencesKeys;
import com.flowness.volley.BasicRequest;
import com.flowness.volley.GetGraphRangesRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
//import com.jjoe64.graphview.series.BarGraphSeries;
//import com.jjoe64.graphview.series.DataPoint;

public class StatsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int MINUTE_IN_MILLISECONDS = 60 * 1000;
    private static final long HOUR_IN_MILLISECONDS = 60l * MINUTE_IN_MILLISECONDS;
    private static final long DAY_IN_MILLISECONDS = 24l * HOUR_IN_MILLISECONDS;

    //    private GraphView graphView;
    private XYPlot plot;
    private Spinner spinner;
    private int currentSpinnerSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.charts_option_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ChartOptions.values()));
        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
        currentSpinnerSelection = spinner.getSelectedItemPosition();
        plot = findViewById(R.id.plot);
//        graphView = findViewById(R.id.graph);
        findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
        rePaintGraph();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        if (pos != currentSpinnerSelection) {
            rePaintGraph();
        }
        currentSpinnerSelection = pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void rePaintGraph() {
        findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
        // Request a string response from the provided URL.
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        final String savedMeterSN = pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, null); // getting String

        String requestBody = getGraphRequestBody(savedMeterSN);
        new GetGraphRangesRequest(requestBody,
                new BasicRequest.ResponseListener() {
                    @Override
                    public void onResponse(BasicRequest.Response response) {
                        if (response.isSuccess()) {
                            try {
                                JsonParser parser = new JsonParser();
                                JsonObject responseJson = (JsonObject) parser.parse(response.data);
                                JsonArray body = (JsonArray) parser.parse(responseJson.get(JsonConst.BODY).getAsString());
                                Log.d("graphView", body.toString());

                                List<Date> xList = new ArrayList<>(body.size());
                                List<Float> yList = new ArrayList<>(body.size());
                                for (int i = 0; i < body.size(); i++) {
                                    JsonObject range = (JsonObject) body.get(i);
                                    xList.add(DateUtils.getDate(range.get(JsonConst.RANGE_END).getAsString()));
                                    yList.add(Float.valueOf(range.get(JsonConst.RANGE_SUM).getAsString()));
                                }
                                // create a couple arrays of y-values to plot:
                                final Date[] domainLabels = xList.toArray(new Date[body.size()]);
                                Number[] series1Numbers = yList.toArray(new Number[body.size()]);

                                // turn the above arrays into XYSeries':
                                // (Y_VALS_ONLY means use the element index as the x value)
                                XYSeries series1 = new SimpleXYSeries(
                                        Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

                                // create formatters to use for drawing a series using LineAndPointRenderer
                                // and configure them from xml:
                                BarFormatter series1Format = new BarFormatter(Color.BLUE, Color.BLUE);
//                                LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);


                                // just for fun, add some smoothing to the lines:
                                // see: http://androidplot.com/smooth-curves-and-androidplot/
                                series1Format.setInterpolationParams(
                                        new CatmullRomInterpolator.Params(30, CatmullRomInterpolator.Type.Centripetal));

                                // add a new series' to the xyplot:
                                plot.clear();
                                plot.addSeries(series1, series1Format);
                                plot.getLegend().setVisible(false);
                                plot.getRenderer(BarRenderer.class).setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, 10);

                                plot.setDomainStep(StepMode.INCREMENT_BY_VAL, Math.ceil(xList.size()/6));

                                plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                                    @Override
                                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                                        DateFormat df = new SimpleDateFormat(((ChartOptions) spinner.getSelectedItem()).getxDomainLabelPattern());
                                        int i = Math.round(((Number) obj).floatValue());
                                        return toAppendTo.append(df.format(domainLabels[i]));
                                    }

                                    @Override
                                    public Object parseObject(String source, ParsePosition pos) {
                                        return null;
                                    }
                                });

                                plot.redraw();

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                            }
                        } else {
                            findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                            Toast.makeText(StatsActivity.this, "Error with data fetching", Toast.LENGTH_LONG).show();
                        }
                    }
                }).execute(StatsActivity.this);


    }

    @NonNull
    private String getGraphRequestBody(String savedMeterSN) {
        int position = spinner.getSelectedItemPosition();
        JsonArray ranges;
        switch (position) {
            case 0:
                ranges = getHourRanges();
                break;
            case 1:
                ranges = getDayRanges();
                break;
            case 2:
                ranges = getWeekRanges();
                break;
            case 3:
                ranges = getMonthRanges();
                break;
            case 4:
                ranges = getYearRanges();
                break;
            default:
                ranges = getHourRanges();
        }
        JsonObject requestBody = new JsonObject();
        requestBody.add(JsonConst.RANGES, ranges);
        requestBody.addProperty(JsonConst.MODULE_SN, savedMeterSN);
        return requestBody.toString();
    }

    private JsonArray getHourRanges() {
        Date zeroedDate = resetedDate(Calendar.MILLISECOND, Calendar.SECOND);
        Date anchor = resetDateField(zeroedDate, Calendar.MINUTE, 10);
        Date start = new Date(anchor.getTime() - HOUR_IN_MILLISECONDS);
        return getRangeElements(start, 6, 10 * MINUTE_IN_MILLISECONDS);
    }

    private JsonArray getDayRanges() {
        Date zeroedDate = resetedDate(Calendar.MINUTE, Calendar.MILLISECOND, Calendar.SECOND);
        Date start = new Date(zeroedDate.getTime() - DAY_IN_MILLISECONDS);
        return getRangeElements(start, 24, HOUR_IN_MILLISECONDS);
    }

    private JsonArray getWeekRanges() {
        Date zeroedDate = resetedDate(Calendar.HOUR, Calendar.MINUTE, Calendar.MILLISECOND, Calendar.SECOND);
        Date start = new Date(zeroedDate.getTime() - (7l * DAY_IN_MILLISECONDS));
        JsonArray ranges = getRangeElements(start, 7, DAY_IN_MILLISECONDS);
        return ranges;
    }

    private JsonArray getMonthRanges() {
        Date zeroedDate = resetedDate(Calendar.HOUR, Calendar.MINUTE, Calendar.MILLISECOND, Calendar.SECOND);
        Date start = new Date(zeroedDate.getTime() - 30l * DAY_IN_MILLISECONDS);
        return getRangeElements(start, 30, DAY_IN_MILLISECONDS);
    }

    private JsonArray getYearRanges() {
        Date zeroedDate = resetedDate(Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.MILLISECOND, Calendar.SECOND);
        Date start = new Date(zeroedDate.getTime() - (365l * DAY_IN_MILLISECONDS));
        return getRangeElements(start, 12, 30 * DAY_IN_MILLISECONDS);
    }

    @NonNull
    private JsonArray getRangeElements(Date startDate, int numRanges, long rangeInterval) {
        JsonArray ranges = new JsonArray();
        for (int i = 0; i < numRanges; i++) {
            JsonObject range = new JsonObject();
            range.addProperty(JsonConst.START_DATE, getISOString(startDate));
            startDate = new Date(startDate.getTime() + (rangeInterval));
            range.addProperty(JsonConst.END_DATE, getISOString(startDate));
            ranges.add(range);
        }
        return ranges;
    }

    private String getISOString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        return dateFormat.format(date);
    }

    private Date resetDateField(Date date, int fieldsToReset, int resetTo) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int current = cal.get(fieldsToReset);
        int diff = (current % resetTo);
        cal.set(fieldsToReset, current - diff);
        return cal.getTime();
    }

    private Date resetedDate(int... fieldsToReset) {
        Date date = new Date();                      // timestamp now
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);                           // set cal to date
        for (int field : fieldsToReset) {
            cal.set(field, 0);            // set millis in second
        }
        return cal.getTime();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
