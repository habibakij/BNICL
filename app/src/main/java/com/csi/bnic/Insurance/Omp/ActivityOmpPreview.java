package com.csi.bnic.Insurance.Omp;

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

public class ActivityOmpPreview extends AppCompatActivity implements TransactionResponseListener {
    Dialog dialog;
    Button buttonGoBack,buttonPayNow;
    CheckBox checkBoxConfirm;
    TextView textViewPrivacyPolicy;
    EditText editTextFullName,editTextPermanentAddress,editTextPermanentCity,editTextMailingAddress,editTextMailingCity,editTextMobile,editTextEmail,editTextPassport,editTextVisitedCountry,editTextBirthDate,editTextType,editTextSubType,editTextStayPeriod,editTextAirport,editTextCategory;
    SharedPreferences sharedPreferences,sharedPreferencesInfo;
    double payAmount;
    String transactionId,message,source = "omp",generatedKey,cardType,bankTranId,tranDate,currency,currencyAmount,status,address;
    Context context = this;
    KProgressHUD kProgressHUD;
    String _null= "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omp_preview);
        kProgressHUD = KProgressHUD.create(ActivityOmpPreview.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(getResources().getColor(R.color.ompBar))
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        sharedPreferences = getSharedPreferences(Constants.SharedPrefItemOMP.globalPreferenceOmpInsurance,MODE_PRIVATE);
        sharedPreferencesInfo = getSharedPreferences(Constants.SharedPrefItemOMP.globalPreferenceOmpInfo,MODE_PRIVATE);
        promotView();
        try {
            String amount = sharedPreferences.getString((Constants.SharedPrefItem.TOTAL_COST), "");
            String finalAmount = amount.replace(",","");
            payAmount = Double.parseDouble(finalAmount);
        }catch (Exception e){}
    }

    private void promotView() {
        LayoutInflater layoutInflater = LayoutInflater.from(ActivityOmpPreview.this);
        final View promptsView = layoutInflater.inflate(R.layout.dialog_omp_preview, null);
        dialog = new Dialog(ActivityOmpPreview.this, R.style.MyDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(promptsView);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
        editTextFullName = (EditText) dialog.findViewById(R.id.editTextFullName);
        editTextPermanentAddress = (EditText) dialog.findViewById(R.id.editTextPermanentAddress);
        editTextPermanentCity = (EditText) dialog.findViewById(R.id.editTextPermanentCity);
        editTextMailingAddress = (EditText) dialog.findViewById(R.id.editTextMailingAddress);
        editTextVisitedCountry = (EditText) dialog.findViewById(R.id.editTextVisitedCountry);
        editTextMailingCity = (EditText) dialog.findViewById(R.id.editTextMailingCity);
        editTextMobile = (EditText) dialog.findViewById(R.id.editTextMobile);
        editTextEmail = (EditText) dialog.findViewById(R.id.editTextEmail);
        editTextPassport = (EditText) dialog.findViewById(R.id.editTextPassportNo);
        editTextBirthDate = (EditText) dialog.findViewById(R.id.editTextBirthDate);
        editTextType = (EditText) dialog.findViewById(R.id.editTextType);
        editTextSubType = (EditText) dialog.findViewById(R.id.editTextSubType);
        editTextStayPeriod = (EditText) dialog.findViewById(R.id.editTextStayPeriod);
        editTextCategory = (EditText) dialog.findViewById(R.id.editTextCategory);
        editTextAirport = (EditText) dialog.findViewById(R.id.editTextAirport);
        buttonGoBack = (Button) dialog.findViewById(R.id.buttonGoBack);
        buttonPayNow = (Button) dialog.findViewById(R.id.buttonPayNow);
        checkBoxConfirm = (CheckBox) dialog.findViewById(R.id.checkboxConfirm);
        textViewPrivacyPolicy = (TextView) dialog.findViewById(R.id.textViewViewPolicy);

        editTextFullName.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.FULL_NAME,""));
        editTextPermanentAddress.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.PERMANENT_ADDRESS,""));
        editTextPermanentCity.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.PERMANENT_CITY,""));
        editTextMailingAddress.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.MAILING_ADDRESS,""));
        editTextMailingCity.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.MAILING_CITY,""));
        editTextMobile.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.MOBILE,""));
        editTextEmail.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.EMAIL,""));
        editTextPassport.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.PASSPORT_NO,""));
        editTextVisitedCountry.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.VISITED_COUNTRY,""));
        editTextCategory.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.CATEGORY,""));
        editTextAirport.setText(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.AIRPORT,""));
        editTextBirthDate.setText(sharedPreferences.getString(Constants.SharedPrefItemOMP.BIRTH_DATE,""));
        editTextStayPeriod.setText(sharedPreferences.getString(Constants.SharedPrefItemOMP.STAY_PERIOD,""));
        editTextSubType.setText(sharedPreferences.getString(Constants.SharedPrefItemOMP.CATEGORY,""));
        editTextType.setText(sharedPreferences.getString(Constants.SharedPrefItemOMP.TYPE,""));

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferencesInfo.edit();
                editor.clear();
                editor.commit();
                dialog.cancel();
                onBackPressed();
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
        buttonPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = editTextPermanentAddress.getText().toString() + ","+ editTextPermanentCity.getText().toString();
                if (checkBoxConfirm.isChecked()){
                    SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization(Constants.SharedPrefItem.STORE_ID,
                            Constants.SharedPrefItem.STORE_PASSWORD, payAmount, CurrencyType.BDT, "123456789098765", "food", SdkType.LIVE);
                    callSSL(sslCommerzInitialization);
                }
                else{
                    Toast.makeText(ActivityOmpPreview.this,"Accept Terms & Conditions",Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityOmpPreview.this,ActivityWeb.class);
                intent.putExtra("passValue",2);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void callSSL(SSLCommerzInitialization sslCommerzInitialization) {
        IntegrateSSLCommerz
                .getInstance(context)
                .addSSLCommerzInitialization(sslCommerzInitialization)
                .buildApiCall( ActivityOmpPreview.this);
    }

    private void sendData() {
        kProgressHUD.show();
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityOmpPreview.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.API.OMP_SUBMIT,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.matches("1")) {
                                message = jsonObject.getString("message");
                                Toast.makeText(ActivityOmpPreview.this,message,Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                kProgressHUD.dismiss();
                                startActivity(new Intent(ActivityOmpPreview.this,MainActivity.class));
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
                //Toast.makeText(ActivityOmpPreview.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("check_error", "error:"+error.getMessage());
                kProgressHUD.dismiss();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("type_id", "4");
                param.put("category_id", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItemOMP.CATEGORY_ID, "")));
                param.put("sub_type_id", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItemOMP.TYPE_ID, "")));
                param.put("dob", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItemOMP.BIRTH_DATE, "")));
                param.put("min_stay", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItemOMP.STAY_MIN, "")));
                param.put("max_stay", Objects.requireNonNull(sharedPreferences.getString(Constants.SharedPrefItemOMP.STAY_MAX, "")));
                param.put("name", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.FULL_NAME, "")));
                param.put("address", address);
                param.put("phone", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.MOBILE, "")));
                param.put("email_address", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.EMAIL, "")));
                param.put("passport_no", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.PASSPORT_NO, "")));
                param.put("category", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.CATEGORY, "")));
                param.put("airport", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.AIRPORT, "")));
                param.put("visited_country", Objects.requireNonNull(sharedPreferencesInfo.getString(Constants.SharedPrefItemOMP.VISITED_COUNTRY, "")));
                param.put("source", "android");
                param.put("generated_key", generatedKey);
                param.put("card_type", cardType);
                param.put("tran_id", transactionId);
                param.put("bank_tran_id", bankTranId);
                param.put("tran_date", tranDate);
                param.put("currency", currency);
                param.put("currency_amount", currencyAmount);
                param.put("status", status);
                Log.d("All_Data_Post_check",param.toString());
                return param;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
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
        Toast.makeText(ActivityOmpPreview.this,"Transaction Fail!!! Try Again",Toast.LENGTH_SHORT).show();
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
        Log.d("hello","error");
    }

    private void dialogMessage() {
        final Dialog dialog = new Dialog(ActivityOmpPreview.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_message);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.a);
        text.setText(message);
        imageView.startAnimation(animation);
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

}
