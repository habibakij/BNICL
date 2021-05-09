package com.csi.bnic.Insurance.Omp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.bnic.R;
import com.csi.bnic.Utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityOmpInfoEntry extends AppCompatActivity {
    Toolbar toolbar;
    EditText editTextPermanentAddress,editTextMobileNo,editTextFullName,editTextEmail,editTextPassportNo,editTextVisitedCountry;
    Spinner spinnerPermanentCity,spinnerAirport,spinnerCategory;
    Button buttonNext;
    CheckBox checkBoxSameAsPermanent;
    SharedPreferences sharedPreferences,sharedPreferencesInfo;
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<String> airportList = new ArrayList<>();
    JSONArray jsonArrayCityList,jsonArrayAirportList;
    String permanentAddress,mobile,email,fullName,passportNo,permanentCity,airport,category,visitedCountry;
    int cityPosition,airportPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omp_info_entry);
        sharedPreferences = getSharedPreferences(Constants.SharedPrefItemOMP.globalPreferenceOmpInsurance,MODE_PRIVATE);
        sharedPreferencesInfo = getSharedPreferences(Constants.SharedPrefItemOMP.globalPreferenceOmpInfo,MODE_PRIVATE);
        initToolBar();
        initUI();
        loadCity();
        loadAirport();
        editTextMobileNo.setText("+880");
        editTextMobileNo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(14)});
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.CATEGORY, R.layout.spinner_item);
        spinnerCategory.setAdapter(arrayAdapter);

        editTextMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+880")){
                    editTextMobileNo.setText("+880");
                    Selection.setSelection(editTextMobileNo.getText(), editTextMobileNo.getText().length());

                }

            }
        });
        Selection.setSelection(editTextMobileNo.getText(), editTextMobileNo.getText().length());
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = editTextFullName.getText().toString();
                permanentAddress = editTextPermanentAddress.getText().toString();
                permanentCity = spinnerPermanentCity.getSelectedItem().toString();
                mobile = editTextMobileNo.getText().toString();
                email = editTextEmail.getText().toString();
                passportNo = editTextPassportNo.getText().toString();
                visitedCountry = editTextVisitedCountry.getText().toString();
                if ( fullName.isEmpty()){
                    editTextFullName.setError("Enter Full Name");
                    editTextFullName.requestFocus();
                } else if ( permanentAddress.isEmpty()){
                    editTextPermanentAddress.setError("Enter Address");
                    editTextPermanentAddress.requestFocus();
                }else if ( cityPosition == 0){
                    Toast.makeText(ActivityOmpInfoEntry.this,"Select City",Toast.LENGTH_SHORT).show();
                }else if ( mobile.isEmpty()){
                    editTextMobileNo.setError("Enter Mobile No");
                    editTextMobileNo.requestFocus();
                }else if ( mobile.length()<14){
                    editTextMobileNo.setError("Enter Valid Mobile No");
                    editTextMobileNo.requestFocus();
                }else if ( email.isEmpty()){
                    editTextEmail.setError("Enter Email");
                    editTextEmail.requestFocus();
                }else if ( passportNo.isEmpty()){
                    editTextPassportNo.setError("Enter Passport No");
                    editTextPassportNo.requestFocus();
                }
                else {
                    saveToPreference();
                    startActivity(new Intent(ActivityOmpInfoEntry.this,ActivityOmpPreview.class));
                }
            }
        });
        spinnerPermanentCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityPosition = spinnerPermanentCity.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int i = spinnerCategory.getSelectedItemPosition();
                if ( i == 0){
                    category = "medical";
                }else {
                    category = "non-medical";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerAirport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                airport = spinnerAirport.getSelectedItem().toString();
                airportPosition = spinnerAirport.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadAirport() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.API.AIRPORT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayAirportList = jsonObject.getJSONArray("list");
                            getAirportName(jsonArrayAirportList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getAirportName(JSONArray jsonArrayAirportList) {
                        airportList.add("Select Airport");
                        for(int i = 0; i<jsonArrayAirportList.length(); i++) {
                            try {
                                JSONObject json = jsonArrayAirportList.getJSONObject(i);
                                airportList.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerAirport.setAdapter(new ArrayAdapter<String>(ActivityOmpInfoEntry.this, R.layout.spinner_item, airportList));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error",error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void saveToPreference() {
        SharedPreferences.Editor editor = sharedPreferencesInfo.edit();
        editor.putString(Constants.SharedPrefItemOMP.FULL_NAME,fullName);
        editor.putString(Constants.SharedPrefItemOMP.PERMANENT_ADDRESS,permanentAddress);
        editor.putString(Constants.SharedPrefItemOMP.PERMANENT_CITY,permanentCity);
        editor.putString(Constants.SharedPrefItemOMP.MOBILE,mobile);
        editor.putString(Constants.SharedPrefItemOMP.EMAIL,email);
        editor.putString(Constants.SharedPrefItemOMP.PASSPORT_NO,passportNo);
        editor.putString(Constants.SharedPrefItemOMP.AIRPORT,airport);
        editor.putString(Constants.SharedPrefItemOMP.CATEGORY,category);
        editor.putString(Constants.SharedPrefItemOMP.VISITED_COUNTRY,visitedCountry);
        editor.commit();
    }

    private void loadCity() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.API.CITY_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayCityList = jsonObject.getJSONArray("list");
                            getPlanName(jsonArrayCityList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getPlanName(JSONArray jsonArrayCityList) {
                        cityList.add("Select City");
                        for(int i = 0; i<jsonArrayCityList.length(); i++) {
                            try {
                                JSONObject json = jsonArrayCityList.getJSONObject(i);
                                cityList.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerPermanentCity.setAdapter(new ArrayAdapter<String>(ActivityOmpInfoEntry.this, R.layout.spinner_item, cityList));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error",error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void initUI() {
        editTextPermanentAddress = (EditText) findViewById(R.id.editTextPermanentAddress);
        editTextMobileNo = (EditText) findViewById(R.id.editTextMobile);
        editTextFullName = (EditText) findViewById(R.id.editTextFullName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextVisitedCountry = (EditText) findViewById(R.id.editTextVisitedCountry);
        editTextPassportNo = (EditText) findViewById(R.id.editTextPassportNo);
        spinnerPermanentCity = (Spinner) findViewById(R.id.spinnerPermanentCity);
        spinnerAirport = (Spinner) findViewById(R.id.spinnerAirport);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        checkBoxSameAsPermanent = (CheckBox) findViewById(R.id.checkboxSameAsPermanent);
        buttonNext = (Button) findViewById(R.id.buttonNext);
    }

    private void initToolBar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.omp);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.md_yellow_800));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
