package com.myhailov.mykola.fishpay.activities.drawer_activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.models.ProfileResult;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.Utils;

import retrofit2.http.PUT;

public class ProfileActivity extends DrawerActivity {

    private String name, surname, phone, avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //  setContentView(R.layout.activity_drawer_sample);

        initToolbar(getString(R.string.profile));
        editProfileRequest();
    }


    @Override
    public void onClick(View view) {

    }

    private void editProfileRequest(){

        ApiClient.getApiClient().getProfile(token)
                .enqueue(new BaseCallback<ProfileResult>(context, true) {
                    @Override
                    protected void onResult(int code, ProfileResult result) {
                        if (code == 200){
                            ProfileResult.Profile profile = result.getProfile();
                            if (profile != null){
                                name = profile.getName();
                                surname = profile.getSurname();
                                phone = profile.getPhone();
                                avatar = profile.getPhoto();

                                setNameInDrawer(name);
                                setSurnameInDrawer(surname);
                                setPhoneInDrawer(phone);
                                setAvatarInDrawer(avatar);

                                saveProfileInfo();

                                initViews();

                            }

                            createDrawer();
                            openDrawer();
                        }
                    }
                });
    }

    private void initViews() {

        String visibleName = name + " " + surname;
        ((TextView) findViewById(R.id.tvName)).setText(visibleName);
        String visiblePhone = "+" + phone;
        ((TextView) findViewById(R.id.tvPhone)).setText(visiblePhone);




        findViewById(R.id.vLanguage).setOnClickListener(this);
        findViewById(R.id.vChangePassword).setOnClickListener(this);
        findViewById(R.id.vExit).setOnClickListener(this);
        findViewById(R.id.vDeleteAccount).setOnClickListener(this);

    }

    private void saveProfileInfo() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PrefKeys.NAME, name);
        editor.putString(PrefKeys.SURNAME, surname);
        editor.putString(PrefKeys.AVATAR, avatar);
        editor.putString(PrefKeys.PHONE, phone);
        editor.apply();
    }


/*  Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjM4MDEyMzQ1Njc4MSIsImRldmljZSI6NjIsImp0aSI6IjU5ZjJmOWNkNGJjNjZhN2M3YTZjYjRjZDljOTJkMjQ3MmI4OWVlMDYiLCJleHBpcmF0aW9uX2RhdGUiOiIyMDE3LTEyLTE1IDExOjA0OjQ0Iiwic2Vzc2lvbklkIjoiNWEzMjVhY2M3NWMzYiJ9.3VWICYDwa5Dp65NGGjpmKyx0hGHEW5zFgqr-tcpwLCQ

    private void editProfileRequest() {

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
    }

    private void setPreferencesRequest(){

        String allowMoneyRequests, touchIdLogin, lang;
        ApiClient.getApiClient()
                .setPreferences(token, allowMoneyRequests, touchIdLogin, lang )
                .enqueue(
                        //TODO:
                );
    }

    private void getPreferenceRequest() {
        String key;
        ApiClient.getApiClient().getPreferences(token, key).enqueue(
                //TODO:
        );
    }

    private void getDevicesRequest(){
        boolean isNeededAllDevices;
        ApiClient.getApiClient().getDevices(token, isNeededAllDevices).enqueue(
                //TODO:
        );
    }


    private void changePassVerifyRequest(){
        String oldPassword;
        ApiClient.getApiClient().changePassVerify(token, oldPassword).enqueue(
                //TODO:
        );

    }

    private void changePassRequest(){
        String newPassword, passChageId;
        ApiClient.getApiClient().changePassVerify(token, newPassword, passChageId).enqueue(
                //TODO:
        );
    }
*/
}
