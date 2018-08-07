package com.myhailov.mykola.fishpay.activities.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.myhailov.mykola.fishpay.BuildConfig;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.ProfileSettingsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.CheckMobileResult;
import com.myhailov.mykola.fishpay.api.results.ContactsResult;
import com.myhailov.mykola.fishpay.api.results.UapayInfo;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.UapayInfoStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private String phone;
    private EditText etPassword;
    private String deviceId, deviceInfo;
    private TextView tvShowPassword;
    private boolean showPass;
    private int attempt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle extras  = getIntent().getExtras();

        assert extras != null;
        phone = extras.getString(Keys.PHONE);

        String visiblePhone = "+" + phone;
        ((EditText) findViewById(R.id.etPhone)).setText(visiblePhone);
        (findViewById(R.id.tvNext)).setOnClickListener(this);
        (findViewById(R.id.ivNext)).setOnClickListener(this);
        (findViewById(R.id.ivBack)).setOnClickListener(this);
        (findViewById(R.id.tvForgot)).setOnClickListener(this);
        tvShowPassword = findViewById(R.id.tvShowPassword);
        tvShowPassword.setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
        deviceId = DeviceIDStorage.getID(context);
        deviceInfo = Build.MODEL;
      //  deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;
     //   deviceId = deviceInfo;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvNext:
            case R.id.ivNext:
                if (attempt < 3) login();
                else Utils.toast(context, getString(R.string.password_block_15));
                break;
            case R.id.tvForgot:
                restorePassword();
                break;
            case R.id.tvShowPassword:
                if (showPass){
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.getText().length());
                    tvShowPassword.setText(getString(R.string.show_password));

                    showPass = false;
                } else {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPassword.setSelection(etPassword.getText().length());
                    tvShowPassword.setText(getString(R.string.hide_password));
                    showPass = true;
                }
                break;
        }
    }

    private void restorePassword() {
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else ApiClient.getApiInterface().initPassRecovery(phone)
        .enqueue(new BaseCallback<CheckMobileResult>(context, true) {
            @Override
            protected void onResult(int code, @Nullable CheckMobileResult result) {
                if (result == null) return;
                String codeOTP = result.getCodeOTP();
                String recoveryID =  result.getRecoveryId();
                Intent intent = new Intent(context, CheckOTPActivity.class);
                intent.putExtra(Keys.PHONE, phone);
                intent.putExtra(Keys.CODE_OTP, codeOTP);
                intent.putExtra(Keys.RECOVERY_ID, recoveryID);
                startActivity(intent); // go to CheckOTPActivity;
            }
        });
    }

    private void login() {
        String password = etPassword.getText().toString();
        if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
        else if (password.length() < 8) Utils.toast(context, getString(R.string.short_password));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else {
            String firebaseToken = "";
            try{
                firebaseToken = FirebaseInstanceId.getInstance().getToken();
                Log.d("firebase", "login: " + firebaseToken);
            }
            catch (Exception ignored){

            }
            String deviceType = "android";
            String devicetype = "android";
            int versionCode = BuildConfig.VERSION_CODE;
            String language = "ru";
            ApiClient.getApiInterface()
                    .login(devicetype, versionCode, language, phone, password, deviceId, deviceInfo, deviceType, firebaseToken)
                    .enqueue(new BaseCallback<JsonElement>(context, true) {

                        @Override
                        public void onResponse(@NonNull Call<BaseResponse<JsonElement>> call,
                                               @NonNull Response<BaseResponse<JsonElement>> response) {
                            closeProgressDialog();
                            BaseResponse<JsonElement> loginResponse = response.body();
                            if (loginResponse == null) return;
                            if (response.code() == 243) {
                                try {
                                    String message = loginResponse.getErrorDescription();
                                    JsonObject jtiResult = loginResponse.getResult().getAsJsonObject();
                                    if (jtiResult != null) {
                                        String jti = jtiResult.get("jti").getAsString();
                                        if (jti != null ) showInvalDialog(jti, message);
                                    }
                                } catch (Exception e) {e.printStackTrace();}
                            }
                            else if(response.code() == 240) {
                                attempt++;
                                if (attempt < 3) Utils.alert(context, getString(R.string.password_incorrect));
                                else {
                                    Utils.alert(context, getString(R.string.password_incorrect) + " " +
                                            getString(R.string.incorrect_password_block_15));
                                    Handler handler = new Handler();
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            attempt = 0;
                                        }
                                    };
                                    handler.postDelayed(runnable, 15 * 60 * 1000);
                                }
                            }

                            else super.onResponse(call, response);
                        }


                        @Override
                        protected void onResult(int code, JsonElement result) {
                            switch (code){
                                case 200:
                                    JsonObject loginResult = result.getAsJsonObject();
                                    TokenStorage.setToken(context, loginResult.get("access_token").getAsString());
                                    getContactsRequest();
                                    uploadContactsRequest();
                                    getUapayInfoRequest();
                                    setLang(loginResult.get("UI_LANG").getAsString());
                                    context.startActivity(new Intent(context, ProfileSettingsActivity.class));
                                    break;
                            }

                        }
                    });
            //final Retrofit retrofit = ApiClient.getRetrofit();
            /*retrofit.create(ApiInterface.class).login(devicetype, versionCode, language, phone, password, deviceId, deviceInfo, deviceType, firebaseToken)
                    .enqueue(new BaseCallback<LoginResult>(context, true) {

                        @Override
                        public void onResponse(@NonNull Call<BaseResponse<LoginResult>> call,
                                               @NonNull Response<BaseResponse<LoginResult>> response) {
                            closeProgressDialog();
                            if (response.code() == 203) {

                                ResponseBody responseBody = response.errorBody();
                                try {
                                    String responseBodyString = responseBody.string();
                                    JSONObject jtiContainer = new JSONObject(responseBodyString);
                                    JSONObject jtiResult = jtiContainer.getJSONObject("result");
                                    if (jtiResult != null) {
                                        Log.d("jtiResult", jtiResult.toString());
                                        String jti = jtiResult.getString("jti");
                                        String message = jtiContainer.getString("errorDescription");
                                        if (jti != null) showInvalDialog(jti, message);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            else super.onResponse(call, response);
                        }

                        @Override
                        protected void onResult(int code, @Nullable LoginResult result) {
                            switch (code) {
                                case 200:
                                    if (result != null)
                                        TokenStorage.setToken(context, result.getToken());
                                    uploadContactsRequest();
                                    context.startActivity(new Intent(context, ProfileSettingsActivity.class));
                                case 240:
                                    attempt++;
                                    if (attempt < 3) Utils.alert(context, "Неверный пароль");
                                    else {
                                        Utils.alert(context, "Неверный пароль." +
                                                " Количество попыток исчерпано, попробуйте через 15 минут");
                                        Handler handler = new Handler();
                                        Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                attempt = 0;
                                            }
                                        };
                                        handler.postDelayed(runnable, 15 * 60 * 1000);
                                    }

                            }
                            if (code == 200) {
                                if (result != null)
                                    TokenStorage.setToken(context, result.getToken());
                                uploadContactsRequest();
                                context.startActivity(new Intent(context, ProfileSettingsActivity.class));
                            }
                        }



                        @Override
                        protected void onError(int code, String errorDescription) {

                        }
                    });*/
        }
    }

    private void getUapayInfoRequest() {
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
            return;
        }
        ApiClient.getApiInterface().getUapayInfo(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<UapayInfo>(context, false) {
                    @Override
                    protected void onResult(int code, UapayInfo result) {
                        SharedPreferences sharedPreferences
                                = getSharedPreferences(UapayInfoStorage.UAPAY_STORAGE, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(UapayInfoStorage.UAPAY_ID_KEY, result.getUapayId());
                        editor.putString(UapayInfoStorage.UAPAY_ID_KEY, result.getUapayKey());
                        editor.apply();
                    }
                });
    }

    private void showInvalDialog(final String jti, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        invalidateRequest(jti);
                    }
                })
                .create().show();
    }


    private void uploadContactsRequest() {
        Log.d("sss", "uploadContactsRequest: 1");
        List<Contact> contacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        if (!Utils.isOnline(this)) return;
        JSONArray contactsArray = new JSONArray();
        JSONObject preparedContacts = new JSONObject();
        try {
            for (Contact contactInfo: contacts) {
                JSONObject contactObject = new JSONObject();
                contactObject.put("first_name",  contactInfo.getName());
                contactObject.put("last_name",  contactInfo.getSurname());
                String phone = contactInfo.getPhone();
                contactObject.put("phone_number", phone);
                contactsArray.put(contactObject);
            }

            preparedContacts.put("contacts_data", contactsArray);
        } catch (Exception ignored){}
        ApiClient.getApiInterface().exportContacts(TokenStorage.getToken(context), preparedContacts.toString())
                .enqueue(new BaseCallback<Object>(context, false) {
                    @Override
                    protected void onResult(int code, Object result) {
//                        getContactsRequest();
                    }
                });
    }

    private void getContactsRequest(){
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
            return;
        }

        ApiClient.getApiInterface()
                .getContacts(TokenStorage.getToken(context), true, true)
                .enqueue(new BaseCallback<ContactsResult>(context, true) {
                    @Override
                    protected void onResult(int code, ContactsResult result) {
                        if (result == null) return;
                        Log.d("sss", "onResult: 2");
                        ArrayList<Contact> appContacts = result.getContacts();
                        DBUtils.saveAppContacts(context, appContacts);
                        Log.d("sss", "onResult: 3");
                    }
                });
    }

    private void invalidateRequest(String jti) {
        Log.d("jti", jti);
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface()
                    .invalidion(phone, jti).enqueue(new BaseCallback<Object>(context, true) {
                @Override
                protected void onResult(int code, Object result) {
                    login();
                }
            });
        } else Utils.noInternetToast(context);
    }

    private void setLang(String language) {
        if (language.equals("ua")) language = "uk";
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
