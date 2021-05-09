package com.csi.bnic.Insurance.Car;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.bnic.Insurance.Model.Facility;
import com.csi.bnic.Insurance.Model.Quote;
import com.csi.bnic.Insurance.Model.Vehicle;
import com.csi.bnic.R;
import com.csi.bnic.Utility.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

public class ActivityCarEntry extends AppCompatActivity {
    Toolbar toolbar;
    Button buttonGetQuote,buttonGoBack,buttonBuyInsurance;
    TextView textViewFacility;
    Dialog dialog;
    Spinner spinnerPlanName,spinnerVehicleType,spinnerPassenger,spinnerDriver,spinnerConductor,spinnerHelper,spinnerSubType;
    EditText editTextDiverNumber,editTextEngineCapacity,editTextPolicyStartDate,editTextPolicyEndDate,editTextCarPrice;
    LinearLayout linearLayoutCarPrice,linearLayoutFacility,linearLayoutConductor,linearLayoutHelper,linearLayoutPassenger;
    Space space;
    RecyclerView recyclerViewFacility;
    String planName,vehicleType,passenger,driverNo,capacity,policyStartDate,policyEndDate,date,planId,vehicleCode,conductorString ="0",helperString = "0",totalPassenger,totalDriver,totalConductor = "0",totalHelper = "0",message;
    String passengerString = "passenger", driverString = "driver",conductor = "conductor",helper = "helper",carPrice,subType;
    Calendar calendar = Calendar.getInstance();
    Context context = this;
    SharedPreferences sharedPreferences;
    JSONArray jsonArrayPlanList,jsonArray,jsonArrayQuote,jsonArrayPassenger,jsonArrayPassengerNumber,jsonArrayFacilityList,jsonArrayFacilityId;
    private ArrayList<String> planNameList = new ArrayList<>();
    private ArrayList<String> vehicleNameList = new ArrayList<>();
    private List<Vehicle> vehicleTypeList = new ArrayList<>();
    private List<Facility> facilitylist = new ArrayList<>();
    private ArrayList<String> driverList = new ArrayList<>();
    private ArrayList<String> capcityList = new ArrayList<>();
    private ArrayList<String> facilityIdList = new ArrayList<>();
    private ArrayList<String> driverListDropdown = new ArrayList<>();
    private ArrayList<String> passengerListDropdown = new ArrayList<>();
    private ArrayList<String> conductorListDropdown = new ArrayList<>();
    private ArrayList<String> helperListDropdown = new ArrayList<>();
    List<Quote> quoteList = new ArrayList<>();
    KProgressHUD kProgressHUD;
    RecyclerView recyclerViewTotalAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_entry);
        kProgressHUD = KProgressHUD.create(ActivityCarEntry.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(getResources().getColor(R.color.md_yellow_900))
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        sharedPreferences = getSharedPreferences(Constants.SharedPrefItem.globalPreferenceInsurance,MODE_PRIVATE);
        initToolBar();
        initUI();
        loadPlan();
        getCurrentDate();
        planDropDown();
        recyclerViewFacility.setHasFixedSize(true);
        recyclerViewFacility.setLayoutManager(new LinearLayoutManager(ActivityCarEntry.this));
        recyclerViewFacility.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ActivityCarEntry.this).color(Color.TRANSPARENT).sizeResId(R.dimen.divider2).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());

        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.SUB_TYPE, R.layout.spinner_item);
        spinnerSubType.setAdapter(arrayAdapter);
        editTextPolicyStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        spinnerPlanName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                planId = getPlanId(position);
                if ( planId.matches("1")){
                    linearLayoutCarPrice.setVisibility(View.GONE);
                    linearLayoutFacility.setVisibility(View.GONE);
                }
                else {
                    linearLayoutCarPrice.setVisibility(View.VISIBLE);
                    linearLayoutFacility.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDriver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                totalDriver = spinnerDriver.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerPassenger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                totalPassenger = spinnerPassenger.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerConductor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spinnerPosition = spinnerConductor.getSelectedItemPosition();
                int size = conductorListDropdown.size();
                if ( spinnerPosition == 0){
                    totalConductor = "0";
                }else {
                    totalConductor = spinnerConductor.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerHelper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spinnerPosition = spinnerHelper.getSelectedItemPosition();
                int size = helperListDropdown.size();
                Log.d("hi", String.valueOf(size));
                if ( spinnerPosition == 0){
                    totalHelper = "0";
                }else {
                    totalHelper = spinnerHelper.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSubType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int i = spinnerSubType.getSelectedItemPosition();
                if ( i == 0){
                    subType = "2";
                }else {
                    subType = "9";
                }
                loadVehicleType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                passengerListDropdown.clear();
                driverListDropdown.clear();
                conductorListDropdown.clear();
                helperListDropdown.clear();
                driverList.clear();
                capcityList.clear();
                conductorListDropdown.add("select Cond.");
                helperListDropdown.add("select Helper");
                spinnerHelper.setAdapter(new ArrayAdapter<String>(ActivityCarEntry.this, R.layout.spinner_item, helperListDropdown));
                spinnerConductor.setAdapter(new ArrayAdapter<String>(ActivityCarEntry.this, R.layout.spinner_item, conductorListDropdown));
                Vehicle vehicle = vehicleTypeList.get(position);
                vehicleCode = getCode(position);
                vehicleType = spinnerVehicleType.getSelectedItem().toString();
                JSONArray jsonArraySeat = vehicle.getJsonArraySeat();
                    for (int i = 0; i <= jsonArraySeat.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArraySeat.getJSONObject(i);
                            driverList.add(jsonObject.getString("name").toLowerCase());
                            capcityList.add(jsonObject.getString("max_capacity"));
                        }catch (Exception e){}
                    }


                try {
                    int index = driverList.indexOf(passengerString);
                    String passenger1 = capcityList.get(index);
                    for (int i = 1; i <= Integer.parseInt(passenger1); i++) {
                        passengerListDropdown.add(String.valueOf(i));
                    }
                    spinnerPassenger.setAdapter(new ArrayAdapter<String>(ActivityCarEntry.this, R.layout.spinner_item, passengerListDropdown));
                }catch (Exception e){}
                int indexDriver = driverList.indexOf(driverString);
                String driver = capcityList.get(indexDriver);
                try {
                    for (int i = 1; i <= Integer.parseInt(driver); i++) {
                        driverListDropdown.add(String.valueOf(i));
                    }
                    spinnerDriver.setAdapter(new ArrayAdapter<String>(ActivityCarEntry.this, R.layout.spinner_item, driverListDropdown));
                }catch (Exception e){}
                try {
                    int indexConductor = driverList.indexOf(conductor);
                    String conductor = capcityList.get(indexConductor);
                    for (int i = 1; i <= Integer.parseInt(conductor); i++) {
                        conductorListDropdown.add(String.valueOf(i));
                    }
                    spinnerConductor.setAdapter(new ArrayAdapter<String>(ActivityCarEntry.this, R.layout.spinner_item, conductorListDropdown));
                }catch (Exception e){}
                try {
                    int indexHelper = driverList.indexOf(helper);
                    String helper = capcityList.get(indexHelper);
                        for (int i = 1; i <= Integer.parseInt(helper); i++) {
                            helperListDropdown.add(String.valueOf(i));
                    }
                    spinnerHelper.setAdapter(new ArrayAdapter<String>(ActivityCarEntry.this, R.layout.spinner_item, helperListDropdown));
                }catch (Exception e){}
                int helperSize = helperListDropdown.size();
                int conductorSize = conductorListDropdown.size();
                int passengerSize = passengerListDropdown.size();
                Log.d("size1", String.valueOf(passengerSize));
                if (helperSize == 1 ){
                    linearLayoutHelper.setVisibility(View.GONE);
                    space.setVisibility(View.GONE);
                }else {
                    linearLayoutHelper.setVisibility(View.VISIBLE);
                    space.setVisibility(View.VISIBLE);
                }
                if (conductorSize == 1 ){
                    linearLayoutConductor.setVisibility(View.GONE);
                    space.setVisibility(View.GONE);
                }else {
                    linearLayoutConductor.setVisibility(View.VISIBLE);
                    space.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textViewFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capacity = editTextEngineCapacity.getText().toString();
                if (capacity.isEmpty()){
                    editTextEngineCapacity.setError("Enter Capacity");
                    editTextEngineCapacity.requestFocus();
                } else {
                    facilitylist.clear();
                    facilityIdList.clear();
                    loadFacility();
                }
            }
        });
        buttonGetQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planName = spinnerPlanName.getSelectedItem().toString();
                driverNo = spinnerDriver.getSelectedItem().toString();
                capacity = editTextEngineCapacity.getText().toString();
                policyStartDate = editTextPolicyStartDate.getText().toString();
                policyEndDate = editTextPolicyEndDate.getText().toString();
                carPrice = editTextCarPrice.getText().toString();
                jsonArrayPassenger = new JSONArray();
                jsonArrayPassengerNumber = new JSONArray();
                jsonArrayFacilityId = new JSONArray();
                for (int i =1; i<5; i++){
                    jsonArrayPassenger.put(i);
                }
                int intDriver,intHelper = 0,intConductor,intPassenger;
                intPassenger = Integer.parseInt(totalPassenger);
                intDriver = Integer.parseInt(totalDriver);
                intHelper = Integer.parseInt(totalHelper);
                intConductor = Integer.parseInt(totalConductor);
                jsonArrayPassengerNumber.put(intDriver);
                jsonArrayPassengerNumber.put(intHelper);
                jsonArrayPassengerNumber.put(intConductor);
                jsonArrayPassengerNumber.put(intPassenger);
                Log.d("json_array_is: ",jsonArrayPassengerNumber.toString());
                try{
                    conductorString = spinnerConductor.getSelectedItem().toString();
                    helperString = spinnerHelper.getSelectedItem().toString();
                    passenger = spinnerPassenger.getSelectedItem().toString();
                    if (conductorString.toLowerCase().matches("select cond."))
                    {
                        conductorString = "0";
                    }
                    if (helperString.toLowerCase().matches("select helper")){
                        helperString = "0";
                    }
                }catch (Exception e){}
                if (capacity.isEmpty()){
                    editTextEngineCapacity.setError("Enter Capacity");
                    editTextEngineCapacity.requestFocus();
                }
                else if (policyStartDate.isEmpty()){
                    editTextPolicyStartDate.setError("Select Date");
                    editTextPolicyStartDate.requestFocus();
                }
                else {
                    //saveToPreference();
                    sendData();
                    //promotTotalAmount();
                }
            }
        });
    }

    private void loadFacility() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.FACILITY_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayFacilityList = jsonObject.getJSONArray("list");
                            Log.d("data",jsonArrayFacilityList.toString());
                            loadList(jsonArrayFacilityList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error",error.toString());
                    }
                })
        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("plan_id",planId);
                map.put("type_id","1");
                map.put("sub_type_id","2");
                map.put("vehicle_type_id",vehicleCode);
                map.put("cc",capacity);
                Log.d("facility_post_data_car",map.toString());
                return map;
            }};
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadList(JSONArray jsonArrayFacilityList) {
        try {
            for (int i = 0; i < jsonArrayFacilityList.length(); i++) {
                JSONObject jsonObject1 = jsonArrayFacilityList.getJSONObject(i);
                facilityIdList.add(jsonObject1.getString("id"));
                facilitylist.add(new Facility(
                        jsonObject1.getString("id"),
                        jsonObject1.getString("name"),
                        jsonObject1.getString("cost")
                ));
            }
            FacilityAdapter adapter = new FacilityAdapter(ActivityCarEntry.this,facilitylist);
            recyclerViewFacility.setAdapter(adapter);
            //recyclerViewBuyOrder.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity().getApplicationContext()).color(Color.TRANSPARENT).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendData() {
        kProgressHUD.show();
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityCarEntry.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.GET_QUOTE,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("cost",response);
                            String status = jsonObject.getString("status");
                            if (status.matches("1")) {
                                jsonArrayQuote = jsonObject.getJSONArray("terrif");
                                String cost = jsonObject.getString("total_cost");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.SharedPrefItem.TOTAL_COST,cost);
                                editor.commit();
                                promotTotalAmount();
                                saveToPreference();
                                kProgressHUD.dismiss();
                            } else {
                                message = jsonObject.getString("message");
                                /*Toast.makeText(ActivityCarEntry.this,
                                        message, Toast.LENGTH_SHORT).show();*/
                                dialogMessage();
                                kProgressHUD.dismiss();
                            }
                        }catch (Exception e){
                            Log.d("Error", e.toString());
                            Log.d("Error2", response.toString());
                            kProgressHUD.dismiss();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("plan_id", planId);
                param.put("type_id", "1");
                param.put("sub_type_id", "2");
                param.put("vtype", vehicleCode);
                param.put("passenger_id", jsonArrayPassenger.toString());
                param.put("passenger_no", jsonArrayPassengerNumber.toString());
                param.put("cc", capacity);
                param.put("asset_value", carPrice);
                param.put("facility", String.valueOf(facilityIdList));
                Log.d("All_Data",param.toString());
                return param;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void dialogMessage() {
        final Dialog dialog = new Dialog(ActivityCarEntry.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_message);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.a);
        text.setText(message);
        imageView.startAnimation(animation);
        //imageView.setBackground(getResources().getDrawable(R.drawable.cancel));
        imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cancel));
        RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.relative);
        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.md_yellow_900));
        buttonOk.setBackgroundResource(R.drawable.button_style_motor);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 10000);
        dialog.show();
    }

    private void loadVehicleType() {
        vehicleTypeList.clear();
        vehicleNameList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.VEHICLE_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                             jsonArray = jsonObject.getJSONArray("list");
                            //getType(jsonArray);
                            getName(jsonArray);
                            for (int i =0; i< jsonArray.length(); i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                vehicleTypeList.add(new Vehicle(
                                        jsonObject1.getString("name"),
                                        jsonObject1.getString("id"),
                                        jsonObject1.getJSONArray("seat")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("type_id","1");
                map.put("sub_type_id",subType);
                return map;
            }};

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getName(JSONArray jsonArray) {
        for(int i = 0; i<jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                vehicleNameList.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinnerVehicleType.setAdapter(new ArrayAdapter<String>(ActivityCarEntry.this, R.layout.spinner_item, vehicleNameList));
    }

    public String getCode(int position){
        String id = "";
        try {
            JSONObject json = jsonArray.getJSONObject(position);
            id = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void loadPlan() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.API.PLAN_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayPlanList = jsonObject.getJSONArray("list");
                            getPlanName(jsonArrayPlanList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getPlanName(JSONArray jsonArrayPlanList) {
                        for(int i = 0; i<jsonArrayPlanList.length(); i++) {
                            try {
                                JSONObject json = jsonArrayPlanList.getJSONObject(i);
                                planNameList.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerPlanName.setAdapter(new ArrayAdapter<String>(ActivityCarEntry.this, R.layout.spinner_item, planNameList));
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

    public String getPlanId(int position){
        String id = "";
        try {
            JSONObject json = jsonArrayPlanList.getJSONObject(position);
            id = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void saveToPreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SharedPrefItem.PLAN_NAME,planName);
        editor.putString(Constants.SharedPrefItem.DRIVER,driverNo);
        editor.putString(Constants.SharedPrefItem.ENGINE_CAPACITY,capacity);
        editor.putString(Constants.SharedPrefItem.POLICY_START_DATE,policyStartDate);
        editor.putString(Constants.SharedPrefItem.POLICY_END_DATE,policyEndDate);
        editor.putString(Constants.SharedPrefItem.PLAN_ID,planId);
        editor.putString(Constants.SharedPrefItem.VEHICLE_ID,vehicleCode);
        editor.putString(Constants.SharedPrefItem.VEHICLE_TYPE,vehicleType);
        editor.putString(Constants.SharedPrefItem.PASSENGER,passenger);
        editor.putString(Constants.SharedPrefItem.CONDUCTOR,conductorString);
        editor.putString(Constants.SharedPrefItem.HELPER,helperString);
        editor.putString(Constants.SharedPrefItem.SUBTYPE_ID,subType);
        editor.putString(Constants.SharedPrefItem.PASSENGER_ID,jsonArrayPassenger.toString());
        editor.putString(Constants.SharedPrefItem.PASSENGER_NO,jsonArrayPassengerNumber.toString());
        editor.commit();
    }

    private void getCurrentDate() {
         date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    private void selectDate() {
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String _year = String.valueOf(year);
                String _month = (month+1) < 10 ? "0" + (month+1) : String.valueOf(month+1);
                String _date = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String pickedDate = _date + "-" + _month + "-" + year;
                int endYear = year+1;
                String nextDate = _date + "-" + _month + "-" + endYear;
                Log.e("PickedDate: ", "Date: " + pickedDate);
                if (date.matches(pickedDate)){
                    Toast.makeText(ActivityCarEntry.this,"Invalid Date",Toast.LENGTH_SHORT).show();
                }
                else {
                    editTextPolicyStartDate.setText(pickedDate);
                    editTextPolicyEndDate.setText(nextDate);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void planDropDown() {
        /*ArrayAdapter planName = ArrayAdapter.createFromResource(this, R.array.PLAN, R.layout.spinner_item);
        planName.setDropDownViewResource(R.layout.spinner_list);
        spinnerPlanName.setAdapter(planName);*/
    }

    private void promotTotalAmount() {
        dialog = new Dialog(ActivityCarEntry.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_motorcycle);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        buttonGoBack = (Button) dialog.findViewById(R.id.buttonGoBack);
        buttonBuyInsurance = (Button) dialog.findViewById(R.id.buttonBuyInsurance);
        recyclerViewTotalAmount = (RecyclerView) dialog.findViewById(R.id.recyclerViewTotalAmount);
        recyclerViewTotalAmount.setHasFixedSize(true);
        recyclerViewTotalAmount.setLayoutManager(new LinearLayoutManager(ActivityCarEntry.this));
        recyclerViewTotalAmount.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ActivityCarEntry.this).color(Color.TRANSPARENT).sizeResId(R.dimen.dividerLess).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
        quoteList.clear();
        try {
            for ( int i=0; i<jsonArrayQuote.length();i++){
                JSONObject jsonObject = jsonArrayQuote.getJSONObject(i);
                quoteList.add(new Quote(
                        jsonObject.getString("title"),
                        jsonObject.getString("total_cost")
                ));
            }
            Log.d("list",jsonArrayQuote.toString());
            QuoteAdapter quoteAdapter = new QuoteAdapter(ActivityCarEntry.this, quoteList);
            recyclerViewTotalAmount.setAdapter(quoteAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonBuyInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityCarEntry.this,ActivityCarInfoEntry.class));
            }
        });
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handler.removeCallbacks(runnable);
            }
        });

        dialog.show();
    }

    private void initUI() {
        buttonGetQuote = (Button) findViewById(R.id.buttonGetQuote);
        spinnerPlanName = (Spinner) findViewById(R.id.spinnerPlanName);
        spinnerSubType = (Spinner) findViewById(R.id.spinnerSubType);
        spinnerVehicleType = (Spinner) findViewById(R.id.spinnerVehicleType);
        spinnerPassenger = (Spinner) findViewById(R.id.spinnerPassenger);
        spinnerDriver = (Spinner) findViewById(R.id.spinnerDriver);
        spinnerConductor = (Spinner) findViewById(R.id.spinnerConductor);
        spinnerHelper = (Spinner) findViewById(R.id.spinnerHelper);
        editTextDiverNumber = (EditText) findViewById(R.id.editTextDriver);
        editTextEngineCapacity = (EditText) findViewById(R.id.editTextCapacity);
        editTextPolicyStartDate = (EditText) findViewById(R.id.editTextPolicyStartDate);
        editTextPolicyEndDate = (EditText) findViewById(R.id.editTextPolicyEndDate);
        editTextCarPrice = (EditText) findViewById(R.id.editTextCarPrice);
        linearLayoutCarPrice = (LinearLayout) findViewById(R.id.linearCarPrice);
        linearLayoutFacility = (LinearLayout) findViewById(R.id.linearFacility);
        linearLayoutConductor = (LinearLayout) findViewById(R.id.linearLayoutConductor);
        linearLayoutHelper = (LinearLayout) findViewById(R.id.linearLayoutHelper);
        linearLayoutPassenger = (LinearLayout) findViewById(R.id.linearLayoutPassenger);
        textViewFacility = (TextView) findViewById(R.id.textViewFacility);
        recyclerViewFacility = (RecyclerView) findViewById(R.id.recyclerViewFacility);
        space = (Space) findViewById(R.id.space);
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

    private class QuoteAdapter extends RecyclerView.Adapter<ActivityCarEntry.QuoteAdapter.QuoteViewHolder>{
        private Context context;
        private List<Quote> quoteList;

        public QuoteAdapter(Context context, List<Quote> quoteList) {
            this.context = context;
            this.quoteList = quoteList;
        }
        @Override
        public ActivityCarEntry.QuoteAdapter.QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.total_amount_card, null);
            return new QuoteViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ActivityCarEntry.QuoteAdapter.QuoteViewHolder holder,final int position) {
            final Quote quote = quoteList.get(position);
            holder.textViewTitle.setText(quote.getTitle());
            holder.textViewCalculatedCost.setText(quote.getTotalCost());
        }
        @Override
        public int getItemCount() {
            return quoteList.size();
        }

        public class QuoteViewHolder extends RecyclerView.ViewHolder {
            TextView textViewTitle,textViewCalculatedCost;
            public QuoteViewHolder(View itemView) {
                super(itemView);
                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewCalculatedCost = (TextView) itemView.findViewById(R.id.textViewCalculatedView);
            }
        }
    }

    //Listed Data
    public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.OrderViewHolder> {
        private Context context;
        private List<Facility> facilityList;

        public FacilityAdapter(Context context, List<Facility> facilityList) {
            this.context = context;
            this.facilityList = facilityList;
        }

        @Override
        public FacilityAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.facility_card, null);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FacilityAdapter.OrderViewHolder holder, int position) {
            final Facility facility = facilityList.get(position);
            holder.textViewName.setText(facility.getName());
            holder.textViewPercentage.setText(facility.getCost());
            holder.checkBoxMultiSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if ( isChecked){
                        facilityIdList.add(facility.getId());
                    }
                    else {
                        String deleteItem = facility.getId();
                        int facilityRemovedItemIndex = facilityIdList.indexOf( deleteItem );
                        facilityIdList.remove(facilityRemovedItemIndex);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return facilityList.size();
        }

        public class OrderViewHolder extends RecyclerView.ViewHolder{
            TextView textViewName,textViewPercentage;
            CheckBox checkBoxMultiSelect;
            public OrderViewHolder(View itemView) {
                super(itemView);
                textViewName = (TextView) itemView.findViewById(R.id.textViewName);
                textViewPercentage = (TextView) itemView.findViewById(R.id.textViewPercentage);
                checkBoxMultiSelect = (CheckBox) itemView.findViewById(R.id.checkboxMultiSelect);
            }
        }
    }

}
