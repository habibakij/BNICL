package com.csi.bnic.Insurance;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.bnic.Activity.MainActivity;
import com.csi.bnic.Insurance.Omp.ActivityOmpPreview;
import com.csi.bnic.Insurance.Web.ActivityWeb;
import com.csi.bnic.R;
import com.csi.bnic.Utility.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization;
import com.sslwireless.sslcommerzlibrary.model.response.TransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.model.util.CurrencyType;
import com.sslwireless.sslcommerzlibrary.model.util.SdkType;
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.TransactionResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityPreview extends AppCompatActivity implements TransactionResponseListener {
    Dialog dialog;
    CheckBox checkBoxConfirm;
    Button buttonBuyNow,buttonGoBack;
    LinearLayout linearLayoutConductorHelper;
    TextView textViewViewPolicy;
    EditText editTextPlanName,editTextVehicleType,editTextPassenger,editTextDiverNumber,editTextEngineCapacity,editTextPolicyStartDate,editTextPolicyEndDate,editTextCity,editTextMailingCity,editTextManufactureYear;
    EditText editTextFullName,editTextAddress,editTextMailingAddress,editTextMobile,editTextEmail,editTextVehicleBrand,editTextRegistrationNumber,editTextRegistrationDate,editTextEngineNumber,editTextChassisNo,editTextconductor,editTextHelper;
    SharedPreferences sharedPreferences,sharedPreferencesInfo;
    String message,amount,transactionId,typeId,source = "motor",generatedKey,cardType,bankTranId,tranDate,currency,currencyAmount,status;
    double payAmount;
    KProgressHUD kProgressHUD;

    String _null= "null";
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        kProgressHUD = KProgressHUD.create(ActivityPreview.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(getResources().getColor(R.color.md_yellow_900))
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        sharedPreferences = getSharedPreferences(Constants.SharedPrefItem.globalPreferenceInsurance,MODE_PRIVATE);
        sharedPreferencesInfo = getSharedPreferences(Constants.SharedPrefItem.globalPreferenceInfo,MODE_PRIVATE);
        try {
            amount = sharedPreferences.getString((Constants.SharedPrefItem.TOTAL_COST), "");
            String finalAmount = amount.replace(",","");
            typeId = sharedPreferences.getString(Constants.SharedPrefItem.SUBTYPE_ID, "");
            payAmount = Double.parseDouble(finalAmount);
        }catch (Exception e){}
        promotePreview();
    }

    private void promotePreview() {
        LayoutInflater layoutInflater = LayoutInflater.from(ActivityPreview.this);
        final View promptsView = layoutInflater.inflate(R.layout.dialog_preview, null);
        dialog = new Dialog(ActivityPreview.this, R.style.MyDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(promptsView);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
        editTextPlanName = (EditText) dialog.findViewById(R.id.editTextPlanName);
        editTextVehicleType = (EditText) dialog.findViewById(R.id.editTextVehicleType);
        editTextPassenger = (EditText) dialog.findViewById(R.id.editTextPassengerNo);
        editTextDiverNumber = (EditText) dialog.findViewById(R.id.editTextDriver);
        editTextEngineCapacity = (EditText) dialog.findViewById(R.id.editTextCapacity);
        editTextPolicyStartDate = (EditText) dialog.findViewById(R.id.editTextPolicyStartDate);
        editTextPolicyEndDate = (EditText) dialog.findViewById(R.id.editTextPolicyEndDate);
        checkBoxConfirm = (CheckBox) dialog.findViewById(R.id.checkboxConfirm);
        editTextCity = (EditText) dialog.findViewById(R.id.editTextCity);
        editTextMailingCity = (EditText) dialog.findViewById(R.id.editTextMailingCity);
        editTextManufactureYear = (EditText) dialog.findViewById(R.id.editTextYearOfManufacture);
        editTextFullName = (EditText) dialog.findViewById(R.id.editTextInsuredFullName);
        editTextAddress = (EditText) dialog.findViewById(R.id.editTextInsuredAddress);
        editTextMailingAddress = (EditText) dialog.findViewById(R.id.editTextMailingAddress);
        editTextMobile = (EditText) dialog.findViewById(R.id.editTextMobile);
        editTextEmail = (EditText) dialog.findViewById(R.id.editTextEmail);
        editTextVehicleBrand = (EditText) dialog.findViewById(R.id.editTextBrand);
        editTextRegistrationNumber = (EditText) dialog.findViewById(R.id.editTextRegistrationNo);
        editTextRegistrationDate = (EditText) dialog.findViewById(R.id.editTextRegistrationDate);
        editTextEngineNumber = (EditText) dialog.findViewById(R.id.editTextEngineNo);
        editTextChassisNo = (EditText) dialog.findViewById(R.id.editTextChassisNo);
        editTextconductor = (EditText) dialog.findViewById(R.id.editTextConductor);
        editTextHelper = (EditText) dialog.findViewById(R.id.editTextHelper);
        linearLayoutConductorHelper = (LinearLayout) dialog.findViewById(R.id.linearConductorHelper);
        buttonBuyNow = (Button) dialog.findViewById(R.id.buttonBuyNow);
        buttonGoBack = (Button) dialog.findViewById(R.id.buttonGoBack);
        textViewViewPolicy = (TextView) dialog.findViewById(R.id.textViewViewPolicy);

        editTextPlanName.setText(sharedPreferences.getString(Constants.SharedPrefItem.PLAN_NAME,""));
        editTextDiverNumber.setText(sharedPreferences.getString(Constants.SharedPrefItem.DRIVER,""));
        editTextEngineCapacity.setText(sharedPreferences.getString(Constants.SharedPrefItem.ENGINE_CAPACITY,""));
        editTextPolicyStartDate.setText(sharedPreferences.getString(Constants.SharedPrefItem.POLICY_START_DATE,""));
        editTextPolicyEndDate.setText(sharedPreferences.getString(Constants.SharedPrefItem.POLICY_END_DATE,""));
        editTextVehicleType.setText(sharedPreferences.getString(Constants.SharedPrefItem.VEHICLE_TYPE,""));
        editTextPassenger.setText(sharedPreferences.getString(Constants.SharedPrefItem.PASSENGER,""));
        editTextCity.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.CITY,""));
        editTextMailingCity.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.MAILING_CITY,""));
        editTextManufactureYear.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.MANUFACTURE_YEAR,""));
        editTextFullName.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.FULL_NAME,""));
        editTextAddress.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.ADDRESS,""));
        editTextMailingAddress.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.MAILING_ADDRESS,""));
        editTextMobile.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.MOBILE,""));
        editTextEmail.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.EMAIL,""));
        editTextVehicleBrand.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.BRAND,""));
        editTextRegistrationNumber.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.REGISTRATION_NUMBER,""));
        editTextRegistrationDate.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.REGISTRATION_DATE,""));
        editTextEngineNumber.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.ENGINE_NUMBER,""));
        editTextChassisNo.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItem.CHASSIS_NUMBER,""));
        editTextconductor.setText(sharedPreferences.getString(Constants.SharedPrefItem.CONDUCTOR,""));
        editTextHelper.setText(sharedPreferences.getString(Constants.SharedPrefItem.HELPER,""));
        if (typeId.matches("1")){
            linearLayoutConductorHelper.setVisibility(View.GONE);
        }
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferencesInfo.edit();
                editor.clear();
                editor.commit();
                dialog.cancel();
                onBackPressed();
            }
        });
        buttonBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxConfirm.isChecked()){
                    SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization(Constants.SharedPrefItem.STORE_ID,
                            Constants.SharedPrefItem.STORE_PASSWORD, payAmount, CurrencyType.BDT, "123456789098765", "food", SdkType.LIVE);
                    callSSL(sslCommerzInitialization);
                }
                else{
                    Toast.makeText(ActivityPreview.this,"Accept Terms & Conditions",Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferencesInfo.edit();
                editor.clear();
                editor.commit();
                dialog.cancel();
                onBackPressed();
            }
        });
        textViewViewPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityPreview.this,ActivityWeb.class);
                intent.putExtra("passValue",1);
                startActivity(intent);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SharedPreferences.Editor editor = sharedPreferencesInfo.edit();
                editor.clear();
                editor.commit();
                dialog.cancel();
                onBackPressed();
            }
        });
        dialog.show();
    }

    private void  callSSL(SSLCommerzInitialization sslCommerzInitialization) {
        IntegrateSSLCommerz
                .getInstance(context)
                .addSSLCommerzInitialization(sslCommerzInitialization)
                .buildApiCall( ActivityPreview.this);
    }

    private void sendData() {
        kProgressHUD.show();
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityPreview.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.MOTOR_INSURANCE,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            Log.d("check_status", status);
                            if (status.matches("1")) {
                                message = jsonObject.getString("message");
                                Toast.makeText(ActivityPreview.this,message,Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                kProgressHUD.dismiss();
                                startActivity(new Intent(ActivityPreview.this,MainActivity.class));
                            } else {
                                message = jsonObject.getString("message");
                                dialogMessage();
                                kProgressHUD.dismiss();
                            }
                        }catch (Exception e){
                            Log.d("Error", e.toString());
                            Log.d("Error2", response.toString());
                            kProgressHUD.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ActivityPreview.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("check_error", "error:"+error.getMessage());
                kProgressHUD.dismiss();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("plan_id", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItem.PLAN_ID, "")));
                param.put("type_id", "1");
                param.put("sub_type_id", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItem.SUBTYPE_ID, "")));
                param.put("vtype", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItem.VEHICLE_ID, "")));
                param.put("passenger_id", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItem.PASSENGER_ID, "")));
                param.put("passenger_no", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItem.PASSENGER_NO, "")));
                param.put("cc", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItem.ENGINE_CAPACITY, "")));
                param.put("psd", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItem.POLICY_START_DATE, "")));
                param.put("name", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.FULL_NAME, "")));
                param.put("address", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.ADDRESS, "")));
                param.put("address2", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.MAILING_ADDRESS, "")));
                param.put("phone_number", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.MOBILE, "")));
                param.put("email_address", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.EMAIL, "")));
                param.put("city1", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.CITY_ID, "")));
                param.put("city2", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.MAILING_CITY_ID, "")));
                param.put("mfg_year", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.MANUFACTURE_YEAR, "")));
                param.put("reg_date", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.REGISTRATION_DATE, "")));
                param.put("reg_number", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.REGISTRATION_NUMBER, "")));
                param.put("brand", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.BRAND, "")));
                param.put("engine_number", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.ENGINE_NUMBER, "")));
                param.put("chassis_no", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItem.CHASSIS_NUMBER, "")));
                param.put("source", source);
                param.put("generated_key", generatedKey);
                param.put("card_type", cardType);
                param.put("tran_id", transactionId);
                param.put("bank_tran_id", bankTranId);
                param.put("tran_date", tranDate);
                param.put("currency", currency);
                param.put("currency_amount", currencyAmount);
                param.put("status", status);
                Log.d("All_Data_post_payment",param.toString());
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
        final Dialog dialog = new Dialog(ActivityPreview.this);
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

    @Override
    public void transactionSuccess(TransactionInfoModel transactionInfoModel) {
        generatedKey = transactionInfoModel.getSessionkey();
        cardType = transactionInfoModel.getCardType();
        transactionId = transactionInfoModel.getTranId();
        bankTranId = transactionInfoModel.getBankTranId();
        tranDate = transactionInfoModel.getTranDate();
        currency = transactionInfoModel.getCurrencyType();
        currencyAmount = transactionInfoModel.getCurrencyAmount();
        status = "success";
        sendData();
    }

    @Override
    public void transactionFail(String s) {
       Toast.makeText(ActivityPreview.this,"Transaction Fail!!! Try Again",Toast.LENGTH_SHORT).show();
        generatedKey = _null;
        cardType = _null;
        transactionId = _null;
        bankTranId = _null;
        tranDate = _null;
        currency = _null;
        currencyAmount = _null;
        status = "fail";
        sendData();
    }

    @Override
    public void merchantValidationError(String s) {
        Toast.makeText(ActivityPreview.this,"Merchant Fail!!! Try Again",Toast.LENGTH_SHORT).show();
    }
}
