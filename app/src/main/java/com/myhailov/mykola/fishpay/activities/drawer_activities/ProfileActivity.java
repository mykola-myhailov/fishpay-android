package com.myhailov.mykola.fishpay.activities.drawer_activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.models.ProfileResult;
import com.myhailov.mykola.fishpay.utils.PrefKeys;

public class ProfileActivity extends DrawerActivity {

    private String name, surname, phone, avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_profile);
        setContentView(R.layout.activity_drawer_sample);

        initToolbar(getString(R.string.profile));

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
                            }

                            createDrawer();
                            openDrawer();
                        }
                    }
                });

    }

    private void saveProfileInfo() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PrefKeys.NAME, name);
        editor.putString(PrefKeys.SURNAME, surname);
        editor.putString(PrefKeys.AVATAR, avatar);
        editor.putString(PrefKeys.PHONE, phone);
        editor.apply();
    }

    @Override
    public void onClick(View view) {

    }
}
