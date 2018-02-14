package com.myhailov.mykola.fishpay.activities.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.login.BeginActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class NewPasswordActivity extends BaseActivity {

    private String passChangeId;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        passChangeId = getIntent().getExtras().getString(Keys.PASS_CHANGE_ID);
        findViewById(R.id.tvNext).setOnClickListener(this);
        findViewById(R.id.ivNextArrow).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
    }

    @Override
    public void onClick(View view) {

        String password = etPassword.getText().toString();
        if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
        else if (password.length() < 8) Utils.toast(context, getString(R.string.short_password));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else  changePassRequest(password);
    }

    private void changePassRequest(String newPassword) {
        String token = TokenStorage.getToken(context);
        Bundle extras = getIntent().getExtras();
        if (extras != null) passChangeId = extras.getString(Keys.PASS_CHANGE_ID);

        ApiClient.getApiClient().changePass(token, newPassword, passChangeId).enqueue(
                new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, Object result) {
                        new AlertDialog.Builder(context)
                                .setMessage("Пароль успешно изменён. Для входа в систему воспользуйтесь новым паролем")
                                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        context.startActivity(new Intent(context, BeginActivity.class));
                                    }
                                })
                                .create().show();
                    }
                }
        );
    }
}
