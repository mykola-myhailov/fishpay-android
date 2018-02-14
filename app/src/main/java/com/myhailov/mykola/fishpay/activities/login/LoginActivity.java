package com.myhailov.mykola.fishpay.activities.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.ProfileSettingsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.ApiInterface;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.CheckMobileResult;
import com.myhailov.mykola.fishpay.api.results.ContactsResult;
import com.myhailov.mykola.fishpay.api.results.LoginResult;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends BaseActivity {
    private String phone;
    private EditText etPassword;
    private String deviceId, deviceInfo;
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
        etPassword = findViewById(R.id.etPassword);
        deviceId = DeviceIDStorage.getID(context);
        deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;
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
                else Utils.toast(context, "15-минутная блокировка ввода пароля ещё не закончилась");
                break;
            case R.id.tvForgot:
                restorePassword();
                break;
        }
    }

    private void restorePassword() {
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else ApiClient.getApiClient().initPassRecovery(phone)
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
            final Retrofit retrofit = ApiClient.getRetrofit();
            retrofit.create(ApiInterface.class).login(phone, password, deviceId, deviceInfo)
                    .enqueue(new BaseCallback<LoginResult>(context, true) {

                        @Override
                        public void onResponse(@NonNull Call<BaseResponse<LoginResult>> call,
                                               @NonNull Response<BaseResponse<LoginResult>> response) {
                            if (response.code() == 403) {
                                closeProgressDialog();
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
                            if (code == 200) {
                                if (result != null)
                                    TokenStorage.setToken(context, result.getToken());
                                uploadContactsRequest();
                                context.startActivity(new Intent(context, ProfileSettingsActivity.class));
                            }
                        }



                        @Override
                        protected void onError(int code, String errorDescription) {
                            switch (code) {
                                case 400:
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
                        }
                    });
        }
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
        List<Contact> contacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        if (!Utils.isOnline(this)) return;
        JSONArray contactsArray = new JSONArray();
        JSONObject preparedContacts = new JSONObject();
        try {
            for (Contact contactInfo: contacts) {
                JSONObject contactObject = new JSONObject();
                contactObject.put("first_name",  contactInfo.getName());
                String phone = contactInfo.getPhone();
                contactObject.put("phone_number", phone);
                contactsArray.put(contactObject);
            }

            preparedContacts.put("contacts_data", contactsArray);
        } catch (Exception ignored){}

        ApiClient.getApiClient().exportContacts(TokenStorage.getToken(this), preparedContacts.toString())
                .enqueue(new BaseCallback<Object>(context, false) {
                    @Override
                    protected void onResult(int code, Object result) {
                        getContactsRequest();
                    }
                });
    }

    private void getContactsRequest(){
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
            return;
        }
        ApiClient.getApiClient()
                .getContacts(TokenStorage.getToken(context), true, true)
                .enqueue(new BaseCallback<ContactsResult>(context, true) {
                    @Override
                    protected void onResult(int code, ContactsResult result) {
                        if (result == null) return;
                        ArrayList<Contact> appContacts = result.getContacts();
                        DBUtils.saveAppContacts(context, appContacts);
                    }
                });
    }

    private void invalidateRequest(String jti) {
        Log.d("jti", jti);
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient()
                    .invalidion(phone, jti).enqueue(new BaseCallback<Object>(context, true) {
                @Override
                protected void onResult(int code, Object result) {
                    login();
                }
            });
        } else Utils.noInternetToast(context);
    }

}
