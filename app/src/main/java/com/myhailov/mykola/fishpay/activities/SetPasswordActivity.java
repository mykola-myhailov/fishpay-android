package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.models.RegistrationResult;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SetPasswordActivity extends BaseActivity{

    private String phone, name, surname, email, birthday, deviceId, deviceInfo, password;
    private ImageView ivAvatar;
    private EditText etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        Bundle extras = getIntent().getExtras();
        assert extras != null;

        phone = extras.getString(Keys.PHONE, "");
        name = extras.getString(Keys.NAME, "");
        surname = extras.getString(Keys.SURNAME, "");
        email = extras.getString(Keys.EMAIL, "");
        birthday = extras.getString(Keys.BIRTHDAY, "");
        deviceId = DeviceIDStorage.getID(context);
        deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;

        String visiblePhone = "+" + phone;
        ((TextView) findViewById(R.id.tvPhone)).setText(visiblePhone);
        String greetings = "Здравствуйте"  + ", "+ name + " " + surname + ".";
        ((TextView) findViewById(R.id.tvGreetings)).setText(greetings);
        String welcome = getString(R.string.welcome) + " " + "в" + " " + getString(R.string.app_name);
        ((TextView) findViewById(R.id.tvWelcome)).setText(welcome);

        (findViewById(R.id.tvNext)).setOnClickListener(this);
        (findViewById(R.id.ivNext)).setOnClickListener(this);

        etPassword = findViewById(R.id.etPassword);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvNext:
            case R.id.ivNext:
                password = etPassword.getText().toString();
                if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
                else if (password.length() < 8) Utils.toast(context, getString(R.string.password8));
                else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                else ApiClient.getApiClient()
                            .registration(parseToRequestBody(phone),
                                    parseToRequestBody(name),
                                    parseToRequestBody(surname),
                                    parseToRequestBody("1990-03-05"),
                                    parseToRequestBody("test@gmail.com"),
                                    parseToRequestBody(password),
                                    parseToRequestBody(deviceId),
                                    parseToRequestBody(deviceInfo),
                                    null)
                            .enqueue(new BaseCallback<RegistrationResult>(context, true) {
                                @Override
                                protected void onResult(int code, @Nullable RegistrationResult result) {
                                    Utils.toast(context, "регистрация успешна!");
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.putExtra(Keys.PHONE, phone);
                                    context.startActivity(intent);
                                }
                            });
                    break;



        }

    }

    private RequestBody parseToRequestBody(String text){
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }
}
