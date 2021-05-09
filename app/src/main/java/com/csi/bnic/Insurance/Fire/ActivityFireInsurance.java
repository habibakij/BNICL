package com.csi.bnic.Insurance.Fire;

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
import java.util.HashMap;
import java.util.Map;

public class ActivityFireInsurance extends AppCompatActivity {
    Toolbar toolbar;
    EditText editTextFullName,editTextAddress,editTextBankName,editTextBankAddress,editTextTradeOrProfession,editTextTermsOfInsuranceForm,editTextMobileNo,editTextEmail;
    String proposerName,address,bankName,bankAddress,tradeOrProfession,insuranceForm,mobile,email,message;
    Button buttonSubmit;
    CheckBox checkBoxTermsCondition;
    KProgressHUD dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_insurance);
        dialog = KProgressHUD.create(ActivityFireInsurance.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(getResources().getColor(R.color.md_red_900))
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        initToolBar();
        initUI();
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proposerName = editTextFullName.getText().toString();
                mobile = editTextMobileNo.getText().toString();
                email = editTextEmail.getText().toString();
                address = editTextAddress.getText().toString();
                bankName = editTextBankName.getText().toString();
                bankAddress = editTextBankAddress.getText().toString();
                tradeOrProfession = editTextTradeOrProfession.getText().toString();
                insuranceForm = editTextTermsOfInsuranceForm.getText().toString();
                if (proposerName.isEmpty()){
                    editTextFullName.setError("Enter Name");
                    editTextFullName.requestFocus();
                }else if (mobile.isEmpty()){
                    editTextMobileNo.setError("Enter Mobile No");
                    editTextMobileNo.requestFocus();
                }else if (address.isEmpty()){
                    editTextAddress.setError("Enter Address");
                    editTextAddress.requestFocus();
                }else if (tradeOrProfession.isEmpty()){
                    editTextTradeOrProfession.setError("Enter Trade or Profession");
                    editTextTradeOrProfession.requestFocus();
                }else if (insuranceForm.isEmpty()){
                    editTextTermsOfInsuranceForm.setError("Enter Insurance form");
                    editTextTermsOfInsuranceForm.requestFocus();
                }
                else if ( !checkBoxTermsCondition.isChecked()){
                    Toast.makeText(ActivityFireInsurance.this,"Accept Terms & Conditions",Toast.LENGTH_SHORT).show();
                }
                else {
                    sendData();
                }
            }
        });
    }

    private void sendData() {
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityFireInsurance.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.FIRE_INSURANCE,
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
                                Toast.makeText(ActivityFireInsurance.this,
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
                param.put("name", proposerName);
                param.put("address", address);
                param.put("partner_bank", bankName);
                param.put("partner_bank_address", bankAddress);
                param.put("profession", tradeOrProfession);
                param.put("terms", insuranceForm);
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

    private void dialogMessage() {
        final Dialog dialog = new Dialog(ActivityFireInsurance.this);
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
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.md_red_900));
        buttonOk.setBackgroundResource(R.drawable.button_style_fire);
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

    private void resetAll() {
        editTextFullName.setText("");
        editTextEmail.setText("");
        editTextMobileNo.setText("");
        editTextAddress.setText("");
        editTextBankAddress.setText("");
        editTextBankName.setText("");
        editTextTermsOfInsuranceForm.setText("");
        editTextTradeOrProfession.setText("");
        checkBoxTermsCondition.setChecked(false);
    }


    private void initUI() {
        editTextFullName = (EditText) findViewById(R.id.editTextProposerName);
        editTextMobileNo = (EditText) findViewById(R.id.editTextMobileNumber);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextBankName = (EditText) findViewById(R.id.editTextPartnersBank);
        editTextBankAddress = (EditText) findViewById(R.id.editTextPartnersBankAddress);
        editTextTradeOrProfession = (EditText) findViewById(R.id.editTextTradeOrProfession);
        editTextTermsOfInsuranceForm = (EditText) findViewById(R.id.editTextTermsOfInsuranceForm);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        checkBoxTermsCondition = (CheckBox) findViewById(R.id.checkboxTermsAndCondition);
    }

    private void initToolBar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.fireInsurance);
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
