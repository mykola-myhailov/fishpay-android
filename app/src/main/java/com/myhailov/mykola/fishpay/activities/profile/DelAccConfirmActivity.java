package com.myhailov.mykola.fishpay.activities.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.login.BeginActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class DelAccConfirmActivity extends BaseActivity {

    private String requestId;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del_acc_confirm);
        Intent intent = getIntent();
        initToolBar("Удаление аккаунта");
        Bundle extras = intent.getExtras();
        requestId = extras.getString(Keys.REQUEST_ID, "0");

        findViewById(R.id.tvDelete).setOnClickListener(this);
        findViewById(R.id.ivDeleteArrow).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
    }

    @Override
    public void onClick(View view) {
        String password = etPassword.getText().toString();
        if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
        else if (password.length() < 8) Utils.toast(context, getString(R.string.short_password));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else ApiClient.getApiInterface()
                    .removeAccConfirm(TokenStorage.getToken(context), password, requestId)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            SharedPreferences sharedPreferences
                                    = context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
                            String phone = sharedPreferences.getString(PrefKeys.PHONE, "");
                            new AlertDialog.Builder(context)
                                    .setMessage("Подтверждение удаления аккаунта придёт в виде СМС-сообщения на номер "
                                            + phone)
                                    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE).edit().clear().apply();
                                            context.startActivity(new Intent(context, BeginActivity.class));
                                        }
                                    })
                                    .create().show();
                        }
                    });

    }
}
