package com.csi.bnic.Insurance.Marine;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.bnic.R;
import com.csi.bnic.Utility.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityMarineInsurance extends AppCompatActivity {
    Toolbar toolbar;
    Button buttonSubmit;
    CheckBox checkBoxTermsAndConditions;
    EditText editTextFullName,editTextAddress,editTextNameOfInsured,editTextInsuredAddress,editTextNatureOfCommodities,editTextModeOfPacking,editTextFromDate,editTextToDate,editTextVia,editTextShippedFor,editTextRiskCovered,editTextAmount,editTextMobile,editTextEmail;
    String name,address,nameofInsured,addressOfInsured,natureOfCommodities,modelOfPacking,fromDate,toDate,via,shippedFor,riskCovered,amount,mobile,email,message;
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat simpleDateFormat;
    KProgressHUD dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marine_insurance);
        dialog = KProgressHUD.create(ActivityMarineInsurance.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(getResources().getColor(R.color.md_blue_900))
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        initToolBar();
        initUI();
        editTextFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
                new DatePickerDialog(ActivityMarineInsurance.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        editTextToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate1();
                new DatePickerDialog(ActivityMarineInsurance.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextFullName.getText().toString();
                address = editTextAddress.getText().toString();
                nameofInsured = editTextNameOfInsured.getText().toString();
                addressOfInsured = editTextInsuredAddress.getText().toString();
                natureOfCommodities = editTextNatureOfCommodities.getText().toString();
                modelOfPacking = editTextModeOfPacking.getText().toString();
                fromDate = editTextFromDate.getText().toString();
                toDate = editTextToDate.getText().toString();
                via = editTextVia.getText().toString();
                shippedFor = editTextShippedFor.getText().toString();
                riskCovered = editTextRiskCovered.getText().toString();
                amount = editTextAmount.getText().toString();
                mobile = editTextMobile.getText().toString();
                email = editTextEmail.getText().toString();
                if ( name.isEmpty()){
                    editTextFullName.setError("Enter Name");
                    editTextFullName.requestFocus();
                }else if ( mobile.isEmpty()){
                    editTextMobile.setError("Enter Mobile Number");
                    editTextMobile.requestFocus();
                }else if ( email.isEmpty()){
                    editTextEmail.setError("Enter Email");
                    editTextEmail.requestFocus();
                }else if ( address.isEmpty()){
                    editTextAddress.setError("Enter Address");
                    editTextAddress.requestFocus();
                }else if ( natureOfCommodities.isEmpty()){
                    editTextNatureOfCommodities.setError("Enter Commodities");
                    editTextNatureOfCommodities.requestFocus();
                }else if ( modelOfPacking.isEmpty()){
                    editTextModeOfPacking.setError("Enter Packing");
                    editTextModeOfPacking.requestFocus();
                }else if ( fromDate.isEmpty()){
                    editTextFromDate.setError("Select Date");
                    editTextFromDate.requestFocus();
                }else if ( toDate.isEmpty()){
                    editTextToDate.setError("Select Date");
                    editTextToDate.requestFocus();
                }else if ( via.isEmpty()){
                    editTextVia.setError("Enter Via");
                    editTextVia.requestFocus();
                }else if ( shippedFor.isEmpty()){
                    editTextShippedFor.setError("Enter Shipped per");
                    editTextShippedFor.requestFocus();
                }else if ( riskCovered.isEmpty()){
                    editTextRiskCovered.setError("Enter Risk Covered");
                    editTextRiskCovered.requestFocus();
                }else if ( amount.isEmpty()){
                    editTextAmount.setError("Enter Amount");
                    editTextAmount.requestFocus();
                }else if ( !checkBoxTermsAndConditions.isChecked()){
                    Toast.makeText(ActivityMarineInsurance.this,"Accept Terms & Conditions",Toast.LENGTH_SHORT).show();
                }else {
                    sendData();
                }
            }
        });
    }

    private void sendData() {
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityMarineInsurance.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.MARINE_INSURANCE,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.matches("1")) {
                                message = jsonObject.getString("message");
                                dialogMessage();
                                resetAll();
                                dialog.dismiss();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(ActivityMarineInsurance.this,
                                        message, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }catch (Exception e){
                            Log.d("Error", e.toString());
                            Log.d("Error2", response.toString());
                            dialog.dismiss();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("name", name);
                param.put("address", address);
                param.put("insured_bank", nameofInsured);
                param.put("insured_bank_address", addressOfInsured);
                param.put("nature", natureOfCommodities);
                param.put("packing_mode", modelOfPacking);
                param.put("start_date", fromDate);
                param.put("end_date", toDate);
                param.put("via", via);
                param.put("shipped_per", shippedFor);
                param.put("risk_cover", riskCovered);
                param.put("amount", amount);
                param.put("phone", mobile);
                param.put("email_address", email);
                param.put("source", "android");
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

    private void resetAll() {
        editTextFullName.setText("");
        editTextMobile.setText("");
        editTextEmail.setText("");
        editTextAddress.setText("");
        editTextNameOfInsured.setText("");
        editTextInsuredAddress.setText("");
        editTextNatureOfCommodities.setText("");
        editTextModeOfPacking.setText("");
        editTextFromDate.setText("");
        editTextToDate.setText("");
        editTextVia.setText("");
        editTextShippedFor.setText("");
        editTextRiskCovered.setText("");
        editTextAmount.setText("");
        checkBoxTermsAndConditions.setChecked(false);
    }

    private void dialogMessage() {
        final Dialog dialog = new Dialog(ActivityMarineInsurance.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_message);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.a);
        text.setText(message);
        imageView.startAnimation(animation);
        RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.relative);
        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.md_blue_900));
        buttonOk.setBackgroundResource(R.drawable.button_style_marine);
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

    private void selectDate1() {
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel1();
            }

        };
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

    private void updateLabel1() {
        String myFormat = "dd-MM-YYYY";
        simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editTextToDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void updateLabel() {
        String myFormat = "dd-MM-YYYY";
        simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editTextFromDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void initUI() {
        editTextFullName = (EditText) findViewById(R.id.editTextProposerName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextNameOfInsured = (EditText) findViewById(R.id.editTextInsuredM_s);
        editTextInsuredAddress = (EditText) findViewById(R.id.editTextInsuredM_sBankAddress);
        editTextNatureOfCommodities = (EditText) findViewById(R.id.editTextNatureOfCommodities);
        editTextModeOfPacking= (EditText) findViewById(R.id.editTextModeOfPacking);
        editTextToDate = (EditText) findViewById(R.id.editTextToDate);
        editTextFromDate = (EditText) findViewById(R.id.editTextFromDate);
        editTextVia = (EditText) findViewById(R.id.editTextVia);
        editTextShippedFor = (EditText) findViewById(R.id.editTextToBeShippedPer);
        editTextRiskCovered = (EditText) findViewById(R.id.editTextRiskCovered);
        editTextAmount = (EditText) findViewById(R.id.editTextAmountTk);
        editTextMobile = (EditText) findViewById(R.id.editTextMobileNumber);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        checkBoxTermsAndConditions = (CheckBox) findViewById(R.id.checkboxTermsAndCondition);
    }

    private void initToolBar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.marineInsurance);
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
