package com.myhailov.mykola.fishpay.activities.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

public class ChangeLanguageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
    }



    private void setPreferencesRequest(){

        String allowMoneyRequests = "1";
        String touchIdLogin = "1";
        String lang = "ru";
        String token = TokenStorage.getToken(context);
        ApiClient.getApiClient()
                .setPreferences(token, allowMoneyRequests, touchIdLogin, lang )
                .enqueue(new BaseCallback<Object>(context, false) {
                             @Override
                             protected void onResult(int code, Object result) {
                                 // if (code == 200)
                                 //change language & e. t.s ??
                             }
                         }
                );
    }

    @Override
    public void onClick(View view) {

    }
}
