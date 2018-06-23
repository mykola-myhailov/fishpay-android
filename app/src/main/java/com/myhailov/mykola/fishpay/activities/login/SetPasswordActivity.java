package com.myhailov.mykola.fishpay.activities.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.BuildConfig;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.RegistrationResult;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

public class SetPasswordActivity extends BaseActivity {

    private String phone, name, surname, email, birthday, deviceId, deviceInfo, password;
    private Uri imageUri;
    private ImageView ivAvatar;
    private EditText etPassword;
    private TextView tvShowPassword;
    private boolean showPass;

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
        String imageUriString = extras.getString(Keys.IMAGE);
        if (imageUriString != null) imageUri = Uri.parse(imageUriString);
        deviceId = DeviceIDStorage.getID(context);
        deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;
      //  deviceId = deviceInfo;

        tvShowPassword = findViewById(R.id.tvShowPassword);
        tvShowPassword.setOnClickListener(this);

        String visiblePhone = "+" + phone;
        ((TextView) findViewById(R.id.tvPhone)).setText(visiblePhone);
        String greetings = getString(R.string.hi_user, name, surname);
        ((TextView) findViewById(R.id.tvGreetings)).setText(greetings);
        String welcome = getString(R.string.welcome_user);
        ((TextView) findViewById(R.id.tvWelcome)).setText(welcome);
        if (imageUri != null){
            ((ImageView) findViewById(R.id.ivAvatar)).setImageURI(imageUri);
            findViewById(R.id.vBackground).setBackground(getResources().getDrawable(R.drawable.awatar_profile));
        }
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
                else {
                    String firebaseToken = "";
                   // String firebaseToken = FirebaseInstanceId.getInstance().getToken();
                    String deviceType = "android";
                    String devicetype = "android";
                    int versionCode = BuildConfig.VERSION_CODE;
                    String language = "ru";
                    ApiClient.getApiInterface()
                            .registration(devicetype, versionCode, language,
                                    Utils.makeRequestBody(phone),
                                    Utils.makeRequestBody(name),
                                    Utils.makeRequestBody(surname),
                                    Utils.makeRequestBody(birthday),
                                    Utils.makeRequestBody(email),
                                    Utils.makeRequestBody(password),
                                    Utils.makeRequestBody(deviceId),
                                    Utils.makeRequestBody(deviceInfo),
                                    Utils.makeRequestBody(deviceType),
                                    Utils.makeRequestBody(firebaseToken),
                                    Utils.makeRequestBodyFile(imageUri))
                            .enqueue(new BaseCallback<RegistrationResult>(context, true) {
                                @Override
                                protected void onResult(int code, @Nullable RegistrationResult result) {
                                    // TODO: 23.06.2018 localization
                                    Utils.toast(context, "регистрация успешна!");
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.putExtra(Keys.PHONE, phone);
                                    context.startActivity(intent);
                                }
                            });
                }
                break;
            case R.id.tvShowPassword:
                if (showPass){
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.getText().length());
                    tvShowPassword.setText(getString(R.string.show_password));

                    showPass = false;
                } else {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPassword.setSelection(etPassword.getText().length());
                    tvShowPassword.setText(getString(R.string.hide_password));
                    showPass = true;
                }
                break;

        }
    }



}
