package com.csi.bnic.Insurance.Motorcycle;

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
import com.csi.bnic.R;
import com.csi.bnic.Utility.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
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

public class ActivityMotorcycleEntry extends AppCompatActivity {
    Toolbar toolbar;
    Dialog dialog;
    Button buttonGetQuote,buttonGoBack,buttonBuyInsurance;
    Spinner spinnerPlanName,spinnerVehicleType,spinnerPassenger,spinnerDriver;
    EditText editTextDiverNumber,editTextEngineCapacity,editTextPolicyStartDate,editTextPolicyEndDate, editTextCarPrice;
    String planName,vehicleType,passengerNo,driverNo,capacity, motorPrice, policyStartDate,policyEndDate,date,totalPassenger,totalDriver,totalConductor = "0",totalHelper = "",message,planId,vehicleCode;
    String driver = "driver", passenger = "passenger",conductor = "conductor", helper = "helper";
    Calendar calendar = Calendar.getInstance();
    Context context = this;
    JSONArray jsonArrayPlanList,jsonArray,jsonArrayPassenger,jsonArrayPassengerNumber,jsonArrayQuote;
    TextView textViewFacilityMotor;
    RecyclerView recyclerViewFacilityMotor;
    JSONArray jsonArrayFacilityListMotor;
    private List<Facility> facilityListMotor = new ArrayList<>();
    private ArrayList<String> facilityIdListMotor = new ArrayList<>();

    private ArrayList<String> planNameList = new ArrayList<>();
    private ArrayList<String> vehicleTypeList = new ArrayList<>();
    private ArrayList<String> driverList = new ArrayList<>();
    private ArrayList<String> passengerList = new ArrayList<>();
    private ArrayList<String> driverListDropdown = new ArrayList<>();
    private ArrayList<String> passengerListDropdown = new ArrayList<>();
    List<Quote> quoteList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    KProgressHUD kProgressHUD;
    RecyclerView recyclerViewTotalAmount;
    LinearLayout linearLayoutFacilityMotor, linearLayoutMotorPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcycle_entry);
        kProgressHUD = KProgressHUD.create(ActivityMotorcycleEntry.this)
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
        loadVehicleType();

        recyclerViewFacilityMotor.setHasFixedSize(true);
        recyclerViewFacilityMotor.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFacilityMotor.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.TRANSPARENT).sizeResId(R.dimen.divider2).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());


        spinnerPlanName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                planId = getPlanId(position);
                if ( planId.matches("1")){
                    linearLayoutFacilityMotor.setVisibility(View.GONE);
                    linearLayoutMotorPrice.setVisibility(View.GONE);
                }
                else {
                    linearLayoutFacilityMotor.setVisibility(View.VISIBLE);
                    linearLayoutMotorPrice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleType = spinnerVehicleType.getSelectedItem().toString();
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

        textViewFacilityMotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capacity = editTextEngineCapacity.getText().toString();
                if (capacity.isEmpty()){
                    editTextEngineCapacity.setError("Enter Capacity");
                    editTextEngineCapacity.requestFocus();
                } else {
                    facilityListMotor.clear();
                    facilityIdListMotor.clear();
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
                motorPrice = editTextCarPrice.getText().toString();
                policyStartDate = editTextPolicyStartDate.getText().toString();
                policyEndDate = editTextPolicyEndDate.getText().toString();
                jsonArrayPassenger = new JSONArray();
                jsonArrayPassengerNumber = new JSONArray();
                for (int i =1; i<5; i++){
                    jsonArrayPassenger.put(i);
                }
                int intDriver,intHelper = 0,intConductor = 0,intPassenger;
                intPassenger = Integer.parseInt(totalPassenger);
                intDriver = Integer.parseInt(totalDriver);
                jsonArrayPassengerNumber.put(intDriver);
                jsonArrayPassengerNumber.put(intHelper);
                jsonArrayPassengerNumber.put(intConductor);
                jsonArrayPassengerNumber.put(intPassenger);
                Log.d("array",jsonArrayPassengerNumber.toString());

                if (capacity.isEmpty()){
                    editTextEngineCapacity.setError("Enter Capacity");
                    editTextEngineCapacity.requestFocus();
                }
                else if (policyStartDate.isEmpty()){
                    editTextPolicyStartDate.setError("Select Date");
                    editTextPolicyStartDate.requestFocus();
                } else if(motorPrice.isEmpty()){
                    editTextCarPrice.setError("Select Date");
                    editTextCarPrice.requestFocus();
                }
                else {
                    //saveToPreference();
                    sendData();
                    //promotTotalAmount();
                }
            }
        });
        //vehicleDropDown();
        getCurrentDate();
        editTextPolicyStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });
    }

    private void loadFacility() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.FACILITY_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ActivityMotorcycleEntry.this, "response success: ", Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayFacilityListMotor = jsonObject.getJSONArray("list");
                            Log.d("response_data",jsonArrayFacilityListMotor.toString());
                            loadList(jsonArrayFacilityListMotor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivityMotorcycleEntry.this, "response error", Toast.LENGTH_LONG).show();
                        Log.d("response_error",error.toString());
                    }
                })
        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("plan_id",planId);
                map.put("type_id","1");
                map.put("sub_type_id","1");
                map.put("vehicle_type_id",vehicleCode);
                map.put("cc",capacity);
                Log.d("facility_post_motor",map.toString());
                return map;
            }};
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadList(JSONArray jsonArrayFacilityList) {
        try {
            for (int i = 0; i < jsonArrayFacilityList.length(); i++) {
                JSONObject jsonObject1 = jsonArrayFacilityList.getJSONObject(i);
                facilityIdListMotor.add(jsonObject1.getString("id"));
                facilityListMotor.add(new Facility(
                        jsonObject1.getString("id"),
                        jsonObject1.getString("name"),
                        jsonObject1.getString("cost")
                ));
            }
            FacilityAdapter adapter = new FacilityAdapter(ActivityMotorcycleEntry.this,facilityListMotor);
            recyclerViewFacilityMotor.setAdapter(adapter);
            //recyclerViewBuyOrder.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity().getApplicationContext()).color(Color.TRANSPARENT).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendData() {
        kProgressHUD.show();
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityMotorcycleEntry.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.GET_QUOTE,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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
                param.put("sub_type_id", "1");
                param.put("vtype", vehicleCode);
                param.put("passenger_id", jsonArrayPassenger.toString());
                param.put("passenger_no", jsonArrayPassengerNumber.toString());
                param.put("cc", capacity);
                param.put("asset_value", editTextCarPrice.getText().toString());
                param.put("facility", String.valueOf(facilityIdListMotor));
                //param.put("source", "android");
                Log.d("All_Data_motorcycle",param.toString());
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
        final Dialog dialog = new Dialog(ActivityMotorcycleEntry.this);
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
        editor.putString(Constants.SharedPrefItem.PASSENGER,totalPassenger);
        editor.putString(Constants.SharedPrefItem.SUBTYPE_ID,"1");
        editor.putString(Constants.SharedPrefItem.PASSENGER_ID,jsonArrayPassenger.toString());
        editor.putString(Constants.SharedPrefItem.PASSENGER_NO,jsonArrayPassengerNumber.toString());
        editor.commit();
    }

    private void loadVehicleType() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.VEHICLE_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            getType(jsonArray);
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
                map.put("sub_type_id","1");
                return map;
            }};

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getType(JSONArray jsonArray) {
        for(int i = 0; i<jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                vehicleTypeList.add(json.getString("name"));
                vehicleCode = json.getString("id");
                Log.d("vehicles_code:", vehicleCode);
                JSONArray jsonArraySeat = json.getJSONArray("seat");
                getSeat(jsonArraySeat);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinnerVehicleType.setAdapter(new ArrayAdapter<String>(ActivityMotorcycleEntry.this, R.layout.spinner_item, vehicleTypeList));
    }

    private void getSeat(JSONArray jsonArraySeat) {
        for(int i = 0; i<jsonArraySeat.length(); i++) {
            try {
                JSONObject json = jsonArraySeat.getJSONObject(i);
                driverList.add(json.getString("name").toLowerCase());
                passengerList.add(json.getString("max_capacity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int index = driverList.indexOf(passenger);
        String passenger = passengerList.get(index);
        for ( int i=1; i<=Integer.parseInt(passenger);  i++){
            passengerListDropdown.add(String.valueOf(i));
            spinnerPassenger.setAdapter(new ArrayAdapter<String>(ActivityMotorcycleEntry.this, R.layout.spinner_item, passengerListDropdown));
        }
        int indexDriver = driverList.indexOf(driver);
        String driver = passengerList.get(indexDriver);
        for ( int i=1; i<=Integer.parseInt(driver);  i++){
            driverListDropdown.add(String.valueOf(i));
            spinnerDriver.setAdapter(new ArrayAdapter<String>(ActivityMotorcycleEntry.this, R.layout.spinner_item, driverListDropdown));
        }
        Log.d("index", passenger);
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
                        spinnerPlanName.setAdapter(new ArrayAdapter<String>(ActivityMotorcycleEntry.this, R.layout.spinner_item, planNameList));
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
                    Toast.makeText(ActivityMotorcycleEntry.this,"Invalid Date",Toast.LENGTH_SHORT).show();
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

    private void vehicleDropDown() {
        ArrayAdapter vehicleType = ArrayAdapter.createFromResource(this, R.array.VEHICLE_TYPE, R.layout.spinner_item);
        vehicleType.setDropDownViewResource(R.layout.spinner_list);
        spinnerVehicleType.setAdapter(vehicleType);
        editTextDiverNumber.setText("1");
    }

    private void promotTotalAmount() {
        dialog = new Dialog(ActivityMotorcycleEntry.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_motorcycle);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        buttonGoBack = (Button) dialog.findViewById(R.id.buttonGoBack);
        buttonBuyInsurance = (Button) dialog.findViewById(R.id.buttonBuyInsurance);
        recyclerViewTotalAmount = (RecyclerView) dialog.findViewById(R.id.recyclerViewTotalAmount);
        recyclerViewTotalAmount.setHasFixedSize(true);
        recyclerViewTotalAmount.setLayoutManager(new LinearLayoutManager(ActivityMotorcycleEntry.this));
        recyclerViewTotalAmount.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ActivityMotorcycleEntry.this).color(Color.TRANSPARENT).sizeResId(R.dimen.dividerLess).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
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
            QuoteAdapter quoteAdapter = new QuoteAdapter(ActivityMotorcycleEntry.this, quoteList);
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
                startActivity(new Intent(ActivityMotorcycleEntry.this,ActivityMotorcycleInfoEntry.class));
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
        spinnerVehicleType = (Spinner) findViewById(R.id.spinnerVehicleType);
        spinnerPassenger = (Spinner) findViewById(R.id.spinnerPassenger);
        spinnerDriver = (Spinner) findViewById(R.id.spinnerDriver);
        editTextCarPrice = findViewById(R.id.editText_vehicles_price);
        editTextDiverNumber = (EditText) findViewById(R.id.editTextDriver);
        editTextEngineCapacity = (EditText) findViewById(R.id.editTextCapacity);
        editTextPolicyStartDate = (EditText) findViewById(R.id.editTextPolicyStartDate);
        editTextPolicyEndDate = (EditText) findViewById(R.id.editTextPolicyEndDate);


        textViewFacilityMotor = (TextView) findViewById(R.id.textViewFacility_motor);
        recyclerViewFacilityMotor= (RecyclerView) findViewById(R.id.recyclerViewFacility_motor);

        linearLayoutFacilityMotor = (LinearLayout) findViewById(R.id.linearLayoutFacility_motor);
        linearLayoutMotorPrice= findViewById(R.id.linear_layout_motor_price);
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

    private class QuoteAdapter extends RecyclerView.Adapter<ActivityMotorcycleEntry.QuoteAdapter.QuoteViewHolder>{
        private Context context;
        private List<Quote> quoteList;

        public QuoteAdapter(Context context, List<Quote> quoteList) {
            this.context = context;
            this.quoteList = quoteList;
        }
        @Override
        public ActivityMotorcycleEntry.QuoteAdapter.QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.total_amount_card, null);
            return new QuoteViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ActivityMotorcycleEntry.QuoteAdapter.QuoteViewHolder holder,final int position) {
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
            return new FacilityAdapter.OrderViewHolder(view);
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
                        facilityIdListMotor.add(facility.getId());
                    }
                    else {
                        String deleteItem = facility.getId();
                        int facilityRemovedItemIndex = facilityIdListMotor.indexOf(deleteItem);
                        facilityIdListMotor.remove(facilityRemovedItemIndex);
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
