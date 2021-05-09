package com.csi.bnic.Insurance.Omp;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.bnic.Insurance.Model.Quote;
import com.csi.bnic.R;
import com.csi.bnic.Utility.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityOmpEntry extends AppCompatActivity {
    Toolbar toolbar;
    EditText editTextBirthDate,editTextAge;
    Spinner spinnerType,spinnerSubType,spinnerStayPeriod;
    Button buttonGetQuote,buttonGoBack,buttonBuyInsurance;
    DatePickerDialog.OnDateSetListener date;
    RecyclerView recyclerViewTotalAmount;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat;
    String birthDate,type,subType,stayPeriod,typeId,categoryId,maxStay,periodId,category,message,min,max,cost,net,vat;
    int spinnerPosition,categorySpinnerPosition,intMaxStay,typedStayPeriod,spinnerPeriodPosition;
    SharedPreferences sharedPreferences;
    JSONArray jsonArrayTypeList,jsonArrayCategoryList,jsonArrayPeriodList,jsonArrayQuote;
    ArrayList<String> typeList = new ArrayList<>();
    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<String> periodList = new ArrayList<>();
    List<Quote> quoteList = new ArrayList<>();
    KProgressHUD kProgressHUD;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omp_entry);

        sharedPreferences = getSharedPreferences(Constants.SharedPrefItemOMP.globalPreferenceOmpInsurance,MODE_PRIVATE);
        kProgressHUD = KProgressHUD.create(ActivityOmpEntry.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(getResources().getColor(R.color.ompBar))
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        initToolBar();
        initUI();
        loadType();
        editTextBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
                new DatePickerDialog(ActivityOmpEntry.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPosition = spinnerType.getSelectedItemPosition();
                typeId = getTypeId(position);
                maxStay = getMaxStay(position);
                if ( spinnerPosition != 0) {
                    //editTextStayPeriod.setHint("Max Stay Period: " + maxStay);
                    categoryList.clear();
                    loadCategory();
                }
                else {
                    categoryList.clear();
                    loadCategory();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSubType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySpinnerPosition = spinnerSubType.getSelectedItemPosition();
                categoryId = getCategoryId(position);
                category = spinnerSubType.getSelectedItem().toString();
                if ( categorySpinnerPosition != 0){
                    loadStayPeriod();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerStayPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPeriodPosition = spinnerStayPeriod.getSelectedItemPosition();
                periodId = getPeriodId(position);
                min = getMin(position);
                max = getMax(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buttonGetQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    birthDate = editTextBirthDate.getText().toString();
                    stayPeriod = spinnerStayPeriod.getSelectedItem().toString();
                    type = spinnerType.getSelectedItem().toString();
                    subType = spinnerSubType.getSelectedItem().toString();
                    intMaxStay = Integer.parseInt(maxStay);
                    typedStayPeriod = Integer.parseInt(stayPeriod);
                }catch (Exception e){}

                if ( spinnerPosition == 0){
                    Toast.makeText(ActivityOmpEntry.this,"Select Type",Toast.LENGTH_SHORT).show();
                }else if ( categorySpinnerPosition == 0){
                    Toast.makeText(ActivityOmpEntry.this,"Select Category",Toast.LENGTH_SHORT).show();
                } else if ( birthDate.isEmpty()){
                    editTextBirthDate.setError("Enter Birth Date");
                    editTextBirthDate.requestFocus();
                }
                else if ( spinnerPeriodPosition == 0){
                    Toast.makeText(ActivityOmpEntry.this,"Select Stay period",Toast.LENGTH_SHORT).show();
                }else {
                    sendData();
                }
            }
        });
    }

    private void sendData() {
        kProgressHUD.show();
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityOmpEntry.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.OMP_QUOTE,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("response", response);
                            String status = jsonObject.getString("status");
                            if (status.matches("1")) {
                                 cost = jsonObject.getString("total_cost");
                                 net = jsonObject.getString("net");
                                 vat = jsonObject.getString("vat");
                                 Log.d("cost",cost);
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
                param.put("type_id", "4");
                param.put("sub_type_id", typeId);
                param.put("category_id", categoryId);
                param.put("dob", birthDate);
                param.put("min_stay", min);
                param.put("max_stay", max);
                Log.d("All Data",param.toString());
                return param;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void promotTotalAmount() {
        dialog = new Dialog(ActivityOmpEntry.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_omp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        /*Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);*/
        buttonGoBack = (Button) dialog.findViewById(R.id.buttonGoBack);
        buttonBuyInsurance = (Button) dialog.findViewById(R.id.buttonBuyInsurance);
        TextView textViewNetAmount = (TextView) dialog.findViewById(R.id.textViewNetAmount);
        TextView textViewVatAmount = (TextView) dialog.findViewById(R.id.textViewVatAmount);
        TextView textViewTotalAmount = (TextView) dialog.findViewById(R.id.textViewTotalAmount);
        textViewNetAmount.setText(net);
        textViewVatAmount.setText(vat);
        textViewTotalAmount.setText(cost);
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonBuyInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityOmpEntry.this,ActivityOmpInfoEntry.class));
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

    private void dialogMessage() {
        final Dialog dialog = new Dialog(ActivityOmpEntry.this);
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
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.ompBar));
        buttonOk.setBackgroundResource(R.drawable.button_style_omp);
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

    private void loadStayPeriod() {
        periodList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.PERIOD_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayPeriodList = jsonObject.getJSONArray("list");
                            getCategoryName(jsonArrayPeriodList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getCategoryName(JSONArray jsonArrayPeriodList) {
                        periodList.add("Select Stay Period");
                        for(int i = 0; i<jsonArrayPeriodList.length(); i++) {
                            try {
                                JSONObject json = jsonArrayPeriodList.getJSONObject(i);
                                periodList.add(json.getString("stay"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerStayPeriod.setAdapter(new ArrayAdapter<String>(ActivityOmpEntry.this, R.layout.spinner_item, periodList));
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
                map.put("type_id","4");
                map.put("sub_type_id",typeId);
                map.put("category_id",categoryId);
                return map;
            }};
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getPeriodId(int position){
        String id = "";
        try {
            JSONObject json = jsonArrayPeriodList.getJSONObject(position-1);
            id = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
    public String getMin(int position){
        String id = "";
        try {
            JSONObject json = jsonArrayPeriodList.getJSONObject(position-1);
            id = json.getString("min_stay");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
    public String getMax(int position){
        String id = "";
        try {
            JSONObject json = jsonArrayPeriodList.getJSONObject(position-1);
            id = json.getString("max_stay");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void loadCategory() {
        categoryList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.OMP_CATEGORY_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayCategoryList = jsonObject.getJSONArray("list");
                            getCategoryName(jsonArrayCategoryList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getCategoryName(JSONArray jsonArrayCategoryList) {
                        categoryList.add("Select Category");
                        for(int i = 0; i<jsonArrayCategoryList.length(); i++) {
                            try {
                                JSONObject json = jsonArrayCategoryList.getJSONObject(i);
                                categoryList.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerSubType.setAdapter(new ArrayAdapter<String>(ActivityOmpEntry.this, R.layout.spinner_item, categoryList));
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
                map.put("sub_type_id",typeId);
                return map;
            }};
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getCategoryId(int position){
        String id = "";
        try {
            JSONObject json = jsonArrayCategoryList.getJSONObject(position-1);
            id = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void loadType() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.TYPE_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArrayTypeList = jsonObject.getJSONArray("list");
                            getTypeName(jsonArrayTypeList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getTypeName(JSONArray jsonArrayTypeList) {
                        typeList.add("Select Type");
                        for(int i = 0; i<jsonArrayTypeList.length(); i++) {
                            try {
                                JSONObject json = jsonArrayTypeList.getJSONObject(i);
                                typeList.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerType.setAdapter(new ArrayAdapter<String>(ActivityOmpEntry.this, R.layout.spinner_item, typeList));
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
                map.put("type_id","4");
                return map;
            }};
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public String getTypeId(int position){
        String id = "";
        try {
            JSONObject json = jsonArrayTypeList.getJSONObject(position-1);
            id = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
    public String getMaxStay(int position){
        String id = "";
        try {
            JSONObject json = jsonArrayTypeList.getJSONObject(position-1);
            id = json.getString("policy");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void saveToPreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SharedPrefItemOMP.BIRTH_DATE,birthDate);
        editor.putString(Constants.SharedPrefItemOMP.STAY_PERIOD,stayPeriod);
        editor.putString(Constants.SharedPrefItemOMP.TYPE,type);
        editor.putString(Constants.SharedPrefItemOMP.TYPE_ID,typeId);
        editor.putString(Constants.SharedPrefItemOMP.CATEGORY,category);
        editor.putString(Constants.SharedPrefItemOMP.CATEGORY_ID,categoryId);
        editor.putString(Constants.SharedPrefItemOMP.STAY_PERIOD_ID,stayPeriod);
        editor.putString(Constants.SharedPrefItemOMP.STAY_MIN,min);
        editor.putString(Constants.SharedPrefItemOMP.STAY_MAX,max);
        editor.commit();
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
        simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editTextBirthDate.setText(simpleDateFormat.format(calendar.getTime()));
        String birth = simpleDateFormat.format(calendar.getTime());
        getAge(birth);
    }
    //Calculate age
    private int getAge(String birth) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
        try {
            date = sdf.parse(birth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month+1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        editTextAge.setText(age+" "+"Years");
        return age;

    }

    private void initUI() {
        editTextBirthDate = (EditText) findViewById(R.id.editTextBirthDate);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        spinnerType = (Spinner) findViewById(R.id.spinnerOmpType);
        spinnerSubType = (Spinner) findViewById(R.id.spinnerSubType);
        spinnerStayPeriod = (Spinner) findViewById(R.id.spinnerStayPeriod);
        buttonGetQuote = (Button) findViewById(R.id.buttonGetQuote);
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

    private class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>{
        private Context context;
        private List<Quote> quoteList;

        public QuoteAdapter(Context context, List<Quote> quoteList) {
            this.context = context;
            this.quoteList = quoteList;
        }
        @Override
        public QuoteAdapter.QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.total_amount_card, null);
            return new QuoteViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final QuoteAdapter.QuoteViewHolder holder,final int position) {
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
}
