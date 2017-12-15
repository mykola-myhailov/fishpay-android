package com.myhailov.mykola.fishpay.activities.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.models.ChangePassVerifyResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

public class ChangePasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    /*
    * title = "изменить пароль"
    *
    * */
    private void changePassVerifyRequest(){
        String oldPassword = "12345678";
        String token = TokenStorage.getToken(context);
        ApiClient.getApiClient().changePassVerify(token, oldPassword)
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

    @Override
    public void onClick(View view) {

    }
}
