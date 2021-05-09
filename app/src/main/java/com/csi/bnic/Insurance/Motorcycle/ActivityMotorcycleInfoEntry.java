package com.csi.bnic.Insurance.Motorcycle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.csi.bnic.Insurance.ActivityPreview;
import com.csi.bnic.R;
import com.csi.bnic.Utility.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityMotorcycleInfoEntry extends AppCompatActivity {
    Toolbar toolbar;
    CheckBox checkBoxSameAddress,checkBoxConfirm;
    Button buttonSubmit,buttonBuyNow,buttonGoBack;
    Spinner spinnerCity,spinnerMailingCity,spinnerYearOfManufacture;
    EditText editTextFullName,editTextAddress,editTextMailingAddress,editTextMobile,editTextEmail,editTextVehicleBrand,editTextRegistrationNumber,editTextRegistrationDate,editTextEngineNumber,editTextChassisNo;
    EditText editTextPlanName,editTextVehicleType,editTextPassenger,editTextDiverNumber,editTextEngineCapacity,editTextPolicyStartDate,editTextPolicyEndDate,editTextCity,editTextMailingCity,editTextManufactureYear;
    String city,cityId, mailingCity,mailingCityId, manufactureYear, fullName, address, mailingAddress, mobile, email, brand, registrationNumber, registrationDate, engineNumber, chassisNumber;
    int cityPosition,mailingCityPosition,yearPosition;
    Calendar calendar = Calendar.getInstance();
    Context context = this;
    DatePickerDialog.OnDateSetListener date;
    JSONArray jsonArrayCityList,jsonArrayYearList;
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<String> yearList = new ArrayList<>();
    Dialog dialog;
    SharedPreferences sharedPreferences,sharedPreferencesInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcycle_info_entry);
        sharedPreferences = getSharedPreferences(Constants.SharedPrefItem.globalPreferenceInsurance,MODE_PRIVATE);
        sharedPreferencesInfo = getSharedPreferences(Constants.SharedPrefItem.globalPreferenceInfo,MODE_PRIVATE);
        initToolBar();
        initUI();
        loadCity();
        loadManufactureYear();
        editTextRegistrationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
                new DatePickerDialog(ActivityMotorcycleInfoEntry.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityPosition = spinnerCity.getSelectedItemPosition();
                cityId = getCityId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMailingCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mailingCityPosition = spinnerMailingCity.getSelectedItemPosition();
                mailingCityId = getCityId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerYearOfManufacture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearPosition = spinnerYearOfManufacture.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        checkBoxSameAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    address = editTextAddress.getText().toString();
                    editTextMailingAddress.setText(address);
                    editTextMailingAddress.setEnabled(false);
                    int position = spinnerCity.getSelectedItemPosition();
                    spinnerMailingCity.setSelection(position);
                    spinnerMailingCity.setEnabled(false);
                }
                else {
                    editTextMailingAddress.setText("");
                    spinnerMailingCity.setSelection(0);
                    spinnerMailingCity.setEnabled(true);
                    editTextMailingAddress.setEnabled(true);
                }
            }
        });
        editTextMobile.setText("+880");
        editTextMobile.setFilters(new InputFilter[] {new InputFilter.LengthFilter(14)});
        editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+880")){
                    editTextMobile.setText("+880");
                    Selection.setSelection(editTextMobile.getText(), editTextMobile.getText().length());

                }

            }
        });
        Selection.setSelection(editTextMobile.getText(), editTextMobile.getText().length());
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = editTextFullName.getText().toString();
                address = editTextAddress.getText().toString();
                mailingAddress = editTextMailingAddress.getText().toString();
                mobile = editTextMobile.getText().toString();
                email = editTextEmail.getText().toString();
                brand = editTextVehicleBrand.getText().toString();
                registrationNumber = editTextRegistrationNumber.getText().toString();
                registrationDate = editTextRegistrationDate.getText().toString();
                engineNumber = editTextEngineNumber.getText().toString();
                chassisNumber = editTextChassisNo.getText().toString();
                city = spinnerCity.getSelectedItem().toString();
                mailingCity = spinnerMailingCity.getSelectedItem().toString();
                manufactureYear = spinnerYearOfManufacture.getSelectedItem().toString();
                if ( fullName.isEmpty()){
                    editTextFullName.setError("Enter Full Name");
                    editTextFullName.requestFocus();
                }else if ( address.isEmpty()){
                    editTextAddress.setError("Enter Address");
                    editTextAddress.requestFocus();
                }else if ( cityPosition == 0){
                    Toast.makeText(ActivityMotorcycleInfoEntry.this,"Select City",Toast.LENGTH_SHORT).show();
                } else if ( mailingAddress.isEmpty()){
                    editTextMailingAddress.setError("Enter Mailing address");
                    editTextMailingAddress.requestFocus();
                }else if ( mailingCityPosition == 0){
                    Toast.makeText(ActivityMotorcycleInfoEntry.this,"Select City",Toast.LENGTH_SHORT).show();
                }else if ( mobile.isEmpty()){
                    editTextMobile.setError("Enter Mobile");
                    editTextMobile.requestFocus();
                }else if ( email.isEmpty()){
                    editTextEmail.setError("Enter Email");
                    editTextEmail.requestFocus();
                }else if ( brand.isEmpty()){
                    editTextVehicleBrand.setError("Enter Brand");
                    editTextVehicleBrand.requestFocus();
                }else if ( yearPosition == 0){
                    Toast.makeText(ActivityMotorcycleInfoEntry.this,"Select Year",Toast.LENGTH_SHORT).show();
                }else if ( registrationNumber.isEmpty()){
                    editTextRegistrationNumber.setError("Enter Registration Number");
                    editTextRegistrationNumber.requestFocus();
                }else if ( registrationDate.isEmpty()){
                    editTextRegistrationDate.setError("Select Date");
                    editTextRegistrationDate.requestFocus();
                }else if ( engineNumber.isEmpty()){
                    editTextEngineNumber.setError("Enter Engine Number");
                    editTextEngineNumber.requestFocus();
                }else if ( chassisNumber.isEmpty()){
                    editTextChassisNo.setError("Enter Chassis Number");
                    editTextChassisNo.requestFocus();
                } else {
                    saveToPreference();
                    startActivity(new Intent(ActivityMotorcycleInfoEntry.this,ActivityPreview.class));
                }
            }
        });
    }

    private void saveToPreference() {
        SharedPreferences.Editor editor = sharedPreferencesInfo.edit();
        editor.putString(Constants.SharedPrefItem.FULL_NAME,fullName);
        editor.putString(Constants.SharedPrefItem.ADDRESS,address);
        editor.putString(Constants.SharedPrefItem.CITY,city);
        editor.putString(Constants.SharedPrefItem.CITY_ID,cityId);
        editor.putString(Constants.SharedPrefItem.MAILING_CITY,mailingCity);
        editor.putString(Constants.SharedPrefItem.MAILING_CITY_ID,mailingCityId);
        editor.putString(Constants.SharedPrefItem.MAILING_ADDRESS,mailingAddress);
        editor.putString(Constants.SharedPrefItem.MAILING_CITY,mailingCity);
        editor.putString(Constants.SharedPrefItem.MOBILE,mobile);
        editor.putString(Constants.SharedPrefItem.EMAIL,email);
        editor.putString(Constants.SharedPrefItem.BRAND,brand);
        editor.putString(Constants.SharedPrefItem.MANUFACTURE_YEAR,manufactureYear);
        editor.putString(Constants.SharedPrefItem.REGISTRATION_NUMBER,registrationNumber);
        editor.putString(Constants.SharedPrefItem.REGISTRATION_DATE,registrationDate);
        editor.putString(Constants.SharedPrefItem.ENGINE_NUMBER,engineNumber);
        editor.putString(Constants.SharedPrefItem.CHASSIS_NUMBER,chassisNumber);
        editor.commit();
    }

    private void loadManufactureYear() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.API.YEAR_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayYearList = jsonObject.getJSONArray("list");
                            getPlanName(jsonArrayYearList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getPlanName(JSONArray jsonArrayYearList) {
                        yearList.add("Select Year");
                        for(int i = 0; i<jsonArrayYearList.length(); i++) {
                            try {
                                JSONObject json = jsonArrayYearList.getJSONObject(i);
                                yearList.add(json.getString("year"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerYearOfManufacture.setAdapter(new ArrayAdapter<String>(ActivityMotorcycleInfoEntry.this, R.layout.spinner_item, yearList));
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
                        spinnerCity.setAdapter(new ArrayAdapter<String>(ActivityMotorcycleInfoEntry.this, R.layout.spinner_item, cityList));
                        spinnerMailingCity.setAdapter(new ArrayAdapter<String>(ActivityMotorcycleInfoEntry.this, R.layout.spinner_item, cityList));
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
    public String getCityId(int position){
        String id = "";
        try {
            JSONObject json = jsonArrayCityList.getJSONObject(position-1);
            id = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void selectDate() {
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
    }

    private void updateLabel() {
        String myFormat = "dd-MM-YYYY";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextRegistrationDate.setText(sdf.format(calendar.getTime()));
    }

    private void initUI() {
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        spinnerMailingCity = (Spinner) findViewById(R.id.spinnerMailingCity);
        spinnerYearOfManufacture = (Spinner) findViewById(R.id.spinnerManufactureYear);
        editTextFullName = (EditText) findViewById(R.id.editTextInsuredFullName);
        editTextAddress = (EditText) findViewById(R.id.editTextInsuredAddress);
        editTextMailingAddress = (EditText) findViewById(R.id.editTextMailingAddress);
        editTextMobile = (EditText) findViewById(R.id.editTextMobile);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextVehicleBrand = (EditText) findViewById(R.id.editTextBrand);
        editTextRegistrationNumber = (EditText) findViewById(R.id.editTextRegistrationNo);
        editTextRegistrationDate = (EditText) findViewById(R.id.editTextRegistrationDate);
        editTextEngineNumber = (EditText) findViewById(R.id.editTextEngineNo);
        editTextChassisNo = (EditText) findViewById(R.id.editTextChassisNo);
        checkBoxSameAddress = (CheckBox) findViewById(R.id.checkboxSameAsInsured);
        buttonSubmit = (Button) findViewById(R.id.buttonNext);
    }

    private void initToolBar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.motorInsurance);
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
