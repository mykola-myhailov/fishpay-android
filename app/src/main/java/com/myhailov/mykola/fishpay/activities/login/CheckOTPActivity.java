package com.myhailov.mykola.fishpay.activities.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.CheckRecoveryResult;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

public class CheckOTPActivity extends BaseActivity {

    private String phone, otpCode, deviceId, deviceInfo;
    private EditText etPassword;
    private SharedPreferences sharedPreferences;
    private boolean isPassRecovering;
    private String recoveryId;
    private int attempt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_otp);
        Bundle extras  = getIntent().getExtras();

        assert extras != null;
        phone = extras.getString(Keys.PHONE);
        otpCode = extras.getString(Keys.CODE_OTP);
        isPassRecovering = extras.containsKey(Keys.RECOVERY_ID);
        if (isPassRecovering) recoveryId = extras.getString(Keys.RECOVERY_ID);


        String visiblePhone = "+" + phone;
        ((EditText) findViewById(R.id.etPhone)).setText(visiblePhone);
        (findViewById(R.id.tvNext)).setOnClickListener(this);
        (findViewById(R.id.ivNext)).setOnClickListener(this);
        (findViewById(R.id.ivBack)).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
        deviceId = DeviceIDStorage.getID(context);
        deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;

        String message;
        if (isPassRecovering) message = getString(R.string.restore_pass_sms_alert);
        else message = getString(R.string.registration_sms_alert);

        new AlertDialog.Builder(context)
                .setTitle(otpCode).setMessage(message + " " + otpCode)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                if (attempt < 3) checkOTP();
                else Utils.toast(context, "15-минутная блокировка ввода sms-кода ещё не закончилась");
                break;
        }
    }

    private void checkOTP() {
        String otp_code = etPassword.getText().toString();
        if (otp_code.equals("")) Utils.toast(context, getString(R.string.enter_otp_code));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else if (isPassRecovering) ApiClient.getApiClient()
                .checkRecoveryOTP(phone, otp_code, recoveryId)
        .enqueue(new BaseCallback<CheckRecoveryResult>(context, true) {
            @Override
            protected void onResult(int code, CheckRecoveryResult result) {
                Intent intent = new Intent(context, RestorePassActivity.class);
                intent.putExtra(Keys.PHONE, phone);
                intent.putExtra(Keys.USER_ID, result.getUserId());
                intent.putExtra(Keys.RECOVERY_ID, recoveryId);
                context.startActivity(intent);
            }

            @Override
            protected void onError(int code, String errorDescription) {
                //super.onError(code, errorDescription);
                attempt ++;
                if (attempt < 3) Utils.alert(context, "Неверный SMS-код");
                 else {
                    Utils.alert(context, "Неправильный SMS-код. Количество попыток исчерпано, попробуйте через 15 минут");
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
        });

        else ApiClient.getApiClient()
                    .checkOTP(phone, otp_code)
                    .enqueue(new BaseCallback<String>(context, true) {
                        @Override
                        protected void onResult(int code, @Nullable String result) {
                            if (code == 200) {

                                Intent intent = new Intent(context, RegistrationActivity.class);
                                intent.putExtra(Keys.PHONE, phone);
                                context.startActivity(intent);
                            }

                        }

                        @Override
                        protected void onError(int code, String errorDescription) {
                            //super.onError(code, errorDescription);
                            attempt ++;
                            if (attempt < 3) Utils.alert(context, "Неправильынй SMS-код");
                            else {
                                Utils.alert(context, "Неправильный SMS-код. Количество попыток исчерпано, попробуйте через 15 минут");
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
                    });
    }
}
