package com.flowness.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.flowness.R;
import com.flowness.utils.SharedPreferencesKeys;

public class MeterSettingsActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener {

    private EditText meterSNEditText;
    private Spinner meterUnitsSpinner;
    private EditText unitPriceEditText;
    private Spinner firstDowSpinner;
    private Button saveBtn;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
        //
        meterSNEditText = findViewById(R.id.sn_edit);
        meterSNEditText.setText(pref.getString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, null));
        meterSNEditText.addTextChangedListener(this);
        //
        meterUnitsSpinner = findViewById(R.id.units_edit);
        meterUnitsSpinner.setSelection(pref.getInt(SharedPreferencesKeys.METER_UNITS_PREF_KEY, 0));
        meterUnitsSpinner.setOnItemSelectedListener(this);
        //
        unitPriceEditText = findViewById(R.id.unit_price_edit);
        unitPriceEditText.setText(String.valueOf(pref.getInt(SharedPreferencesKeys.UNIT_PRICE_PREF_KEY, 0)));
        unitPriceEditText.addTextChangedListener(this);
        //
        firstDowSpinner = findViewById(R.id.first_week_day_edit);
        firstDowSpinner.setSelection(pref.getInt(SharedPreferencesKeys.FIRST_DOW_PREF_KEY, 0));
        meterUnitsSpinner.setOnItemSelectedListener(this);
        //
        saveBtn = findViewById(R.id.save_btn);
        enableSaveButton(false);
        saveBtn.setOnClickListener(this);
    }

    private void enableSaveButton(boolean enable) {
        saveBtn.setEnabled(enable);
        saveBtn.setTextColor(enable ? Color.BLACK : Color.DKGRAY);
    }

    @Override
    public void onClick(View view) {
        if (view == saveBtn) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPreferencesKeys.SP_ROOT_NAME, MODE_PRIVATE); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(SharedPreferencesKeys.SAVED_METER_SN_PREF_KEY, meterSNEditText.getText().toString()); // setting String
            editor.putInt(SharedPreferencesKeys.METER_UNITS_PREF_KEY, meterUnitsSpinner.getSelectedItemPosition()); // setting int
            editor.putInt(SharedPreferencesKeys.UNIT_PRICE_PREF_KEY, Integer.valueOf(unitPriceEditText.getText().toString())); // setting String
            editor.putInt(SharedPreferencesKeys.FIRST_DOW_PREF_KEY, firstDowSpinner.getSelectedItemPosition()); // setting int
            editor.apply();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        enableSaveButton(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        if (view != null && view.getParent() instanceof Spinner) {
//
//            switch (((Spinner) view.getParent()).getId()) {
//
//                case R.id.units_edit: {
//
//                }
//                case R.id.first_week_day_edit: {
//
//                }
//            }
//        }
        enableSaveButton(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
