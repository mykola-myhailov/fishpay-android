package com.myhailov.mykola.fishpay.activities.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

public class NewPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
    }

    @Override
    public void onClick(View view) {
        changePassRequest();
    }

    private void changePassRequest() {
        String token = TokenStorage.getToken(context);
        String passChangeId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) passChangeId = extras.getString(Keys.PASS_CHANGE_ID);
        String newPassword = "12345678";
        ApiClient.getApiClient().changePass(token, newPassword, passChangeId).enqueue(
                new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, Object result) {

                    }
                }
        );
    }
}
