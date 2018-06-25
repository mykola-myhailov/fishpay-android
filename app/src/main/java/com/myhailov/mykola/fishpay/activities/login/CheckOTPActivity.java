package com.myhailov.mykola.fishpay.activities.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.CheckRecoveryResult;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

import retrofit2.Call;

public class CheckOTPActivity extends BaseActivity {

    private String phone, otpCode, deviceId, deviceInfo;
    private String message;
    private EditText etPassword;
    private SharedPreferences sharedPreferences;
    private boolean isPassRecovering;
    private String recoveryId;
    private int attempt = 0;

    private AlertDialog incorrectSmsAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_otp);
        Bundle extras = getIntent().getExtras();

        assert extras != null;
        phone = extras.getString(Keys.PHONE);
        otpCode = extras.getString(Keys.CODE_OTP);
        isPassRecovering = extras.containsKey(Keys.RECOVERY_ID);
        if (isPassRecovering) recoveryId = extras.getString(Keys.RECOVERY_ID);


        String visiblePhone = "+" + phone;
        ((EditText) findViewById(R.id.etPhone)).setText(visiblePhone);
        findViewById(R.id.tv_repeat).setOnClickListener(this);
        (findViewById(R.id.tvNext)).setOnClickListener(this);
        (findViewById(R.id.ivNext)).setOnClickListener(this);
        (findViewById(R.id.ivBack)).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
        deviceId = DeviceIDStorage.getID(context);
        deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;


        if (isPassRecovering) message = getString(R.string.restore_pass_sms_alert);
        else message = getString(R.string.registration_sms_alert);

        showSmsCodeAlert();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvNext:
            case R.id.ivNext:
                if (attempt < 3) checkOTP();
                else
                    Utils.toast(context, getString(R.string.block_15));
                break;
            case R.id.tv_repeat:
                showSmsCodeAlert();
                break;
            case R.id.tv_action_1:
                incorrectSmsAlert.cancel();
                break;
        }
    }

    private void checkOTP() {
        String otp_code = etPassword.getText().toString();
        if (otp_code.equals("")) Utils.toast(context, getString(R.string.enter_otp_code));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else if (isPassRecovering) ApiClient.getApiInterface()
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
                        attempt++;
                        if (attempt < 3) Utils.alert(context, getString(R.string.incorrect_sms_code));
                        else {
                            Utils.alert(context, getString(R.string.incorrect_sms_code_block));
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
                });

        else ApiClient.getApiInterface()
                    .checkOTP(phone, otp_code)
                    .enqueue(new BaseCallback<String>(context, true) {

                        @Override
                        public void onFailure(@NonNull Call<BaseResponse<String>> call, @NonNull Throwable t) {
                            super.onFailure(call, t);
                            showIncorrectSmsAlert();
                        }

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
                            attempt++;
                            if (attempt < 3) {
                                showIncorrectSmsAlert();
                            } else {
                                showIncorrectSmsAlert();
                                Utils.alert(context, getString(R.string.incorrect_sms_code_block));
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
                    });
    }

    private void showIncorrectSmsAlert() {
        TextView tvTitle;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_with_one_action, null);
        dialogBuilder.setView(dialogView);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.password_sms_entered_incorrect));

        dialogView.findViewById(R.id.tv_action_1).setOnClickListener(this);
        dialogView.findViewById(R.id.tv_description).setVisibility(View.GONE);

        incorrectSmsAlert = dialogBuilder.create();
        incorrectSmsAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        incorrectSmsAlert.show();
    }

    private void showSmsCodeAlert() {
        TextView tvTitle, tvDescription;
        final AlertDialog alertDialog;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_with_one_action, null);
        dialogBuilder.setView(dialogView);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvDescription = dialogView.findViewById(R.id.tv_description);
        tvTitle.setText(otpCode);
        tvDescription.setText(message + " " + otpCode);

        alertDialog = dialogBuilder.create();
        dialogView.findViewById(R.id.tv_action_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
}
