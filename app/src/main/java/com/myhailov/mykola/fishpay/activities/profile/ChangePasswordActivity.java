package com.myhailov.mykola.fishpay.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.ChangePassVerifyResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class ChangePasswordActivity extends BaseActivity {

    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initToolBar("Изменить пароль");

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
        else changePassVerifyRequest(password);
    }

    /*  title = "изменить пароль"  */
    private void changePassVerifyRequest(String password){
        String token = TokenStorage.getToken(context);
        ApiClient.getApiInterface().changePassVerify(token, password)
                .enqueue(new BaseCallback<ChangePassVerifyResult>(context, true) {
                    @Override
                    protected void onResult(int code, ChangePassVerifyResult result) {
                        if (code == 200){
                            Intent intent = new Intent(context, NewPasswordActivity.class);
                            intent.putExtra(Keys.PASS_CHANGE_ID,  result.getPassChangeId());
                            context.startActivity(intent);
                        }
                    }
                });
    }


}
