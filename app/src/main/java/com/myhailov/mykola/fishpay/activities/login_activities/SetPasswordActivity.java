package com.myhailov.mykola.fishpay.activities.login_activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.models.RegistrationResult;
import com.myhailov.mykola.fishpay.utils.DeviceIDStorage;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SetPasswordActivity extends BaseActivity {

    private String phone, name, surname, email, birthday, deviceId, deviceInfo, password;
    private Uri imageUri;
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
        String imageUriString = extras.getString(Keys.IMAGE);
        if (imageUriString != null) imageUri = Uri.parse(imageUriString);
        deviceId = DeviceIDStorage.getID(context);
        deviceInfo = Build.DEVICE + " " + Build.MODEL + " " + Build.PRODUCT;

        String visiblePhone = "+" + phone;
        ((TextView) findViewById(R.id.tvPhone)).setText(visiblePhone);
        String greetings = "Здравствуйте"  + ", "+ name + " " + surname + ".";
        ((TextView) findViewById(R.id.tvGreetings)).setText(greetings);
        String welcome = getString(R.string.welcome) + " " + "в" + " " + getString(R.string.app_name);
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
                else ApiClient.getApiClient()
                            .registration(parseToRequestBody(phone),
                                    parseToRequestBody(name),
                                    parseToRequestBody(surname),
                                    parseToRequestBody(birthday),
                                    parseToRequestBody(email),
                                    parseToRequestBody(password),
                                    parseToRequestBody(deviceId),
                                    parseToRequestBody(deviceInfo),
                                    parseToMultipartBodyFile(imageUri))
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

    private MultipartBody.Part parseToMultipartBodyFile(Uri imageUri) {
        if (imageUri == null) return null;
        File file = new File(imageUri.getPath());
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestFile = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
    }

    private RequestBody parseToRequestBody(String text){
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }
}
