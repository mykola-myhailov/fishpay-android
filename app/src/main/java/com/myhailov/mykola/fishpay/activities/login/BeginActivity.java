package com.myhailov.mykola.fishpay.activities.login;


import com.google.gson.internal.LinkedTreeMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.BuildConfig;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.services.ContactsIntentService;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.Locale;


public class BeginActivity extends BaseActivity {

    private TextView tvUa, tvRu, tvEn, tvNext, tvNumber;
    EditText etPhone;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_begin);
        startService(new Intent(this, ContactsIntentService.class));
        //   quicklogin();
        etPhone = findViewById(R.id.etPhone);
        SharedPreferences sharedPreferences = getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
        if (sharedPreferences.contains(PrefKeys.PHONE)) {
            etPhone.setText(sharedPreferences.getString(PrefKeys.PHONE, ""));
        }


        String versionText = getString(R.string.version) + ": " + BuildConfig.VERSION_NAME;
        ((TextView) findViewById(R.id.tvVersion)).setText(versionText);
        tvEn = findViewById(R.id.tv_en);
        tvRu = findViewById(R.id.tv_ru);
        tvUa = findViewById(R.id.tv_ua);
        tvNext = findViewById(R.id.tvNext);
        tvNumber = findViewById(R.id.tvPhone);
        checkLang();
        tvNext.setOnClickListener(this);
        (findViewById(R.id.ivNext)).setOnClickListener(this);


        tvEn.setOnClickListener(this);
        tvRu.setOnClickListener(this);
        tvUa.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.tv_ru:
                setLang("ru");
                break;
            case R.id.tv_en:
                setLang("en");
                break;
            case R.id.tv_ua:
                setLang("uk");
                break;
        }
    }

    private void checkLang() {
        tvRu.setTextColor(getResources().getColor(R.color.grey_disabled));
        tvUa.setTextColor(getResources().getColor(R.color.grey_disabled));
        tvEn.setTextColor(getResources().getColor(R.color.grey_disabled));
        Locale current = getResources().getConfiguration().locale;
        switch (current.toString()) {
            case "ru":
                tvRu.setTextColor(getResources().getColor(R.color.blue1));
                break;
            case "en":
                tvEn.setTextColor(getResources().getColor(R.color.blue1));
                break;
            case "uk":
                tvUa.setTextColor(getResources().getColor(R.color.blue1));
                break;
            default:
                tvRu.setTextColor(getResources().getColor(R.color.blue1));
                setLang("ru");
                break;
        }
    }


    private void setLang(String language) {
        Locale current = getResources().getConfiguration().locale;
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        if (!current.toString().equals(language)) {
            checkLang();
            tvNumber.setText(getString(R.string.your_phone_number));
            tvNext.setText(getString(R.string.next));
        }
    }

    private void checkMobileRequest() {
        String devicetype = "android";
        int versionCode = BuildConfig.VERSION_CODE;
        String language = "ru";
        ApiClient.getApiInterface()
                .checkMobile(devicetype, versionCode, language, phone)
                .enqueue(new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, @Nullable Object result) {
                        Intent intent;

                        switch (code) {
                            case 200:
                                //there is user with this phone number in DB.
                                // json contains:  "result": ""
                                intent = new Intent(context, LoginActivity.class);
                                intent.putExtra(Keys.PHONE, phone);
                                startActivity(intent); // go to LoginActivity;
                                break;
                            case 201:
                                //there is no user without this phone number in DB.
                                // json contains: "result": { "codeOTP": XXXX}
                                if (result == null) return;
                                LinkedTreeMap linkedTreeMap = (LinkedTreeMap) result;
                                String codeOTP = (linkedTreeMap.get("codeOTP")).toString().substring(0, 4);
                                intent = new Intent(context, CheckOTPActivity.class);
                                intent.putExtra(Keys.PHONE, phone);
                                intent.putExtra(Keys.CODE_OTP, codeOTP);
                                startActivity(intent); // go to CheckOTPActivity;
                                break;
                        }

                    }

                });
    }


   /* private void quicklogin() {
        String deviceId = DeviceIDStorage.getID(context);
        String deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;
        String password = "12345678";
        String phone = "380123456789";
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else {
            String firebaseToken = FirebaseInstanceId.getInstance().getToken();
            String deviceType = "android";
            String devicetype = "android";
            int versionCode = BuildConfig.VERSION_CODE;
            String language = "ru";
            ApiClient.getApiInterface().login(phone, password, deviceId, deviceInfo, deviceType, firebaseToken)
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
    }*/

}
