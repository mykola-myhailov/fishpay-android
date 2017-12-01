package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.models.CheckMobileResult;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

public class LoginActivity extends BaseActivity {
    private String phone;
    private EditText etPassword;
    private String deviceId, deviceInfo;

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvNext:
            case R.id.ivNext:
                login();
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
            .enqueue(new BaseCallback<Void>(context, true) {
                @Override
                protected void onResult(int code, @Nullable Void result) {
                    if (code == 200){
                        Utils.toast(context, "Логин успешен!!!");
                        context.startActivity(new Intent(context, NextActivity.class));
                    }
                }
            });
    }
}
