package com.myhailov.mykola.fishpay.activities.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;

public class UserInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        //title = "Основная информация"
    }

    @Override
    public void onClick(View view) {

    }





/*    private void editProfileRequest() {

        String name, surname, birthday, email, password, deviceId, deviceInfo;
        Uri imageUri;

        //TODO:

        ApiClient.getApiClient().editProfile(token,
                Utils.makeRequestBody(name),
                Utils.makeRequestBody(surname),
                Utils.makeRequestBody(birthday),
                Utils.makeRequestBody(email),
                Utils.makeRequestBodyFile(imageUri)
        ).enqueue(
                //TODO:
        );
    }*/
}
