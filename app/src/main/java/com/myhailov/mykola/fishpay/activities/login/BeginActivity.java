package com.myhailov.mykola.fishpay.activities.login;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.internal.LinkedTreeMap;
import com.myhailov.mykola.fishpay.BuildConfig;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.ProfileSettingsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.LoginResult;
import com.myhailov.mykola.fishpay.services.ContactsIntentService;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;


public class BeginActivity extends BaseActivity {

    EditText etPhone;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        startService(new Intent(this, ContactsIntentService.class));
        quicklogin();

        etPhone = findViewById(R.id.etPhone);
        String versionText = getString(R.string.version)+ ": " +BuildConfig.VERSION_NAME;
        ((TextView) findViewById(R.id.tvVersion)).setText(versionText);
        (findViewById(R.id.tvNext)).setOnClickListener(this);
        (findViewById(R.id.ivNext)).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvNext:
            case R.id.ivNext:
                phone = etPhone.getText().toString();
                if (phone.equals("")) {
                    Utils.toast(context, getString(R.string.enter_phone_number));
                    return;
                }
                if (phone.substring(0, 1).equals("+")) phone = phone.substring(1);
                if (phone.length() < 12) Utils.toast(context, getString(R.string.short_number));
                else if (phone.length() > 13) Utils.toast(context, getString(R.string.long_number));
                else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                else checkMobileRequest();
                break;
        }
    }


  private void checkMobileRequest() {
        ApiClient.getApiClient()
                .checkMobile(phone)
                .enqueue(new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, @Nullable Object result) {
                        Intent intent;

                        switch (code){
                            case 200:
                                //there is user with this phone number in DB.
                                // json contains:  "result": ""
                                intent = new Intent(context, LoginActivity.class);
                                intent.putExtra(Keys.PHONE, phone);
                                startActivity(intent); // go to LoginActivity;
                                break;
                            case  201:
                                //there is no user without this phone number in DB.
                                // json contains: "result": { "codeOTP": XXXX}
                                if (result == null) return;
                                LinkedTreeMap linkedTreeMap = (LinkedTreeMap) result;
                                String codeOTP = (linkedTreeMap.get("codeOTP")).toString().substring(0,4);
                                intent = new Intent(context, CheckOTPActivity.class);
                                intent.putExtra(Keys.PHONE, phone);
                                intent.putExtra(Keys.CODE_OTP, codeOTP);
                                startActivity(intent); // go to CheckOTPActivity;
                                break;
                        }

                    }

                });
    }


    private void quicklogin() {
        String deviceId = DeviceIDStorage.getID(context);
        String deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;
        String password = "12345678";
        String phone = "380123456789";
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else {
            String firebaseToken = FirebaseInstanceId.getInstance().getToken();
            String deviceType = "android";
            ApiClient.getApiClient().login(phone, password, deviceId, deviceInfo, deviceType, firebaseToken)
                    .enqueue(new BaseCallback<LoginResult>(context, true) {
                        @Override
                        protected void onResult(int code, @Nullable LoginResult result) {
                            if (code == 200) {
                                if (result != null)
                                    TokenStorage.setToken(context, result.getToken());

                                context.startActivity(new Intent(context, ProfileSettingsActivity.class));
                            }
                        }
                    });

        }
    }

}
