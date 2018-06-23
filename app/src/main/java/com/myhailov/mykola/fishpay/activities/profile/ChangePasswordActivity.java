package com.myhailov.mykola.fishpay.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.ChangePassVerifyResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import retrofit2.Call;

public class ChangePasswordActivity extends BaseActivity {

    private EditText etPassword;
    private TextView tvPasswordIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initCustomToolbar(getString(R.string.change_password_));

        tvPasswordIncorrect = findViewById(R.id.tv_password_incorrect);
        findViewById(R.id.tvNext).setOnClickListener(this);
        findViewById(R.id.ivNextArrow).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    checkPassword();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvNext:
                checkPassword();
                break;
            case R.id.ivNextArrow:
                checkPassword();
                break;
        }

    }

    private void checkPassword() {
        String password = etPassword.getText().toString();
        if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
        else if (password.length() < 8)
            Utils.toast(context, getString(R.string.short_password));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else {
            changePassVerifyRequest(password);
        }
    }

    /*  title = "изменить пароль"  */
    private void changePassVerifyRequest(String password) {
        String token = TokenStorage.getToken(context);
        ApiClient.getApiInterface().changePassVerify(token, password)
                .enqueue(new BaseCallback<ChangePassVerifyResult>(context, true) {
                    @Override
                    public void onFailure(@NonNull Call<BaseResponse<ChangePassVerifyResult>> call, @NonNull Throwable t) {
                        tvPasswordIncorrect.setVisibility(View.VISIBLE);
                        super.onFailure(call, t);
                    }

                    @Override
                    protected void onResult(int code, ChangePassVerifyResult result) {
                        if (code == 200) {
                            tvPasswordIncorrect.setVisibility(View.GONE);
                            Intent intent = new Intent(context, NewPasswordActivity.class);
                            intent.putExtra(Keys.PASS_CHANGE_ID, result.getPassChangeId());
                            context.startActivity(intent);
                        }
                    }
                });
    }


}
