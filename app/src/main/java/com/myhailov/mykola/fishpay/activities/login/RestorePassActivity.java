package com.myhailov.mykola.fishpay.activities.login;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

public class RestorePassActivity extends BaseActivity {

    private EditText etPassword;
    private String password, phone, recoveryId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_pass);

        (findViewById(R.id.tvNext)).setOnClickListener(this);
        (findViewById(R.id.ivNext)).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        recoveryId = extras.getString(Keys.RECOVERY_ID);
        userId = extras.getString(Keys.USER_ID);
        phone = extras.getString(Keys.PHONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvNext:
            case R.id.ivNext:
                password = etPassword.getText().toString();
                if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
                else if (password.length() < 8) Utils.toast(context, getString(R.string.password8));
                else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                else ApiClient.getApiInterface().passRecovery(recoveryId, password, userId)
                            .enqueue(new BaseCallback<String>(context, true) {
                                @Override
                                protected void onResult(int code, @Nullable String result) {
                                    Utils.toast(context, "Возобновление пароля произошло успешно!");
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.putExtra(Keys.PHONE, phone);
                                    context.startActivity(intent);
                                }
                            });
                break;
        }
    }
}
