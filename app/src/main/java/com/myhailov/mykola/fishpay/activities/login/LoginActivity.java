package com.myhailov.mykola.fishpay.activities.login;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.ProfileSettingsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.EmptyCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.ContactsRequestBody;
import com.myhailov.mykola.fishpay.api.results.CheckMobileResult;
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

import java.util.ArrayList;
import java.util.List;

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
        else ApiClient.getApiClient().login(phone, password,  deviceId, deviceInfo)
            .enqueue(new BaseCallback<LoginResult>(context, true) {
                @Override
                protected void onResult(int code, @Nullable LoginResult result) {
                    if (code == 200){
                        if (result != null)
                            TokenStorage.setToken(context, result.getToken());
                        uploadContactsRequest();
                        context.startActivity(new Intent(context, ProfileSettingsActivity.class));
                    }
                }

                @Override
                protected void onError(int code, String errorDescription) {
                    switch (code){
                        case 403:
                            invalidateRequest();
                            break;
                        case 400:
                            attempt ++;
                            if (attempt < 3) Utils.alert(context, "Неверный пароль");
                            else {
                                Utils.alert(context, "Неверный пароль. Количество попыток исчерпано, попробуйте через 15 минут");
                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        attempt = 0;
                                    }
                                };
                                handler.postDelayed(runnable, 15*60*1000);
                            }
                    }
                }
            });
    }


    private void uploadContactsRequest() {
        List<Contact> contacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        if (!Utils.isOnline(this)) return;
        JSONArray contactsArray = new JSONArray();
        JSONObject prepardContacts = new JSONObject();
        try {
            for (Contact contactInfo: contacts) {
                JSONObject contactObject = new JSONObject();
                contactObject.put("first_name",  contactInfo.getName());
                contactObject.put("phone_number",contactInfo.getPhone());
                contactsArray.put(contactObject);
            }

            prepardContacts.put("contacts_data", contactsArray);
        } catch (Exception ignored){
        }



        ApiClient.getApiClient().exportContacts(TokenStorage.getToken(this), prepardContacts.toString())
                .enqueue(new EmptyCallback());
    }

    private void invalidateRequest() {
        if (Utils.isOnline(context)) {
           // ApiClient.getApiClient()

        } else Utils.noInternetToast(context);
    }
}
