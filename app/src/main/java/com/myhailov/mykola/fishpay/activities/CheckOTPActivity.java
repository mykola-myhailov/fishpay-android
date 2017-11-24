package com.myhailov.mykola.fishpay.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckOTPActivity extends BaseActivity {

    private String phone, otpCode, deviceId, deviceInfo;
    private EditText etPassword;
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_otp);
        Bundle extras  = getIntent().getExtras();

        assert extras != null;
        phone = extras.getString(Keys.PHONE);
        otpCode = extras.getString(Keys.CODE_OTP);


        String visiblePhone = "+" + phone;
        ((EditText) findViewById(R.id.etPhone)).setText(visiblePhone);
        (findViewById(R.id.tvNext)).setOnClickListener(this);
        (findViewById(R.id.ivNext)).setOnClickListener(this);
        (findViewById(R.id.ivBack)).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
        deviceId = DeviceIDStorage.getID(context);
        deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;

        new AlertDialog.Builder(context)
                .setTitle("OTP:").setMessage(otpCode)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvNext:
            case R.id.ivNext:
                registration();
                break;
        }
    }

    private void registration() {
        String code = etPassword.getText().toString();
        if (code.equals("")) Utils.toast(context, getString(R.string.enter_otp_code));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else ApiClient.getApiClient().checkOTP(phone, code)
                    .enqueue(new BaseCallback<String>(context, true) {
                        @Override
                        protected void onResult(int code, @Nullable String result) {
                            if (code == 200){
                               Intent intent = new Intent(context, RegistrationActivity.class);
                               intent.putExtra(Keys.PHONE, phone);
                               context.startActivity(intent);
                            }
                        }
                    });
    }
}
