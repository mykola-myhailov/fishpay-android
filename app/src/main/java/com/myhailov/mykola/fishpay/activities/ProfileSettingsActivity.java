package com.myhailov.mykola.fishpay.activities;

import com.google.gson.Gson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.login.BeginActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.activities.profile.ChangeLanguageActivity;
import com.myhailov.mykola.fishpay.activities.profile.ChangePasswordActivity;
import com.myhailov.mykola.fishpay.activities.profile.DeleteAccountActivity;
import com.myhailov.mykola.fishpay.activities.profile.UserInfoActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.ProfileResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.activities.profile.ChangeLanguageActivity.CHANGE_LANG;
import static com.myhailov.mykola.fishpay.utils.Keys.LANG;

public class ProfileSettingsActivity extends DrawerActivity {

    private String name, surname, phone, avatar, email, birthday, id, lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //  setContentView(R.layout.activity_drawer_sample);

        initDrawerToolbar(getString(R.string.profile));
        getProfileRequest();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivAvatar:
                Intent userInfoIntent = new Intent(context, UserInfoActivity.class);
                userInfoIntent.putExtra(Keys.PHOTO, avatar);
                userInfoIntent.putExtra(Keys.NAME, name);
                userInfoIntent.putExtra(Keys.SURNAME, surname);
                userInfoIntent.putExtra(Keys.BIRTHDAY, birthday);
                userInfoIntent.putExtra(Keys.EMAIL, email);
                context.startActivity(userInfoIntent);
                break;
            case R.id.vLanguage:
                Intent intent = new Intent(context, ChangeLanguageActivity.class);
                intent.putExtra(LANG, lang);
                startActivityForResult(intent, CHANGE_LANG);
                break;
            case R.id.vChangePass:
                context.startActivity(new Intent(context, ChangePasswordActivity.class));
                break;
            case R.id.vExit:
                context.startActivity(new Intent(context, BeginActivity.class));
                break;
            case R.id.vDelete:
                context.startActivity(new Intent(context, DeleteAccountActivity.class));
                break;
            case R.id.ll_card:
                startActivityForResult(new Intent(context, CardsActivity.class), REQUEST_CARD);
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_LANG && resultCode == RESULT_OK) {
            String language = data.getStringExtra(LANG);
            setLang(language);
            recreate();
        }
        if (requestCode == REQUEST_CARD && resultCode == RESULT_OK) {
            recreate();
        }
    }

    private void getProfileRequest() {

        ApiClient.getApiInterface().getProfile(token)
                .enqueue(new BaseCallback<ProfileResult>(context, true) {
                    @Override
                    protected void onResult(int code, ProfileResult result) {
                        if (code == 200) {
                            ProfileResult.Profile profile = result.getProfile();
                            if (profile != null) {
                                id = result.getUserId();
                                name = profile.getName();
                                surname = profile.getSurname();
                                phone = profile.getPhone();
                                avatar = profile.getPhoto();
                                email = profile.getEmail();
                                birthday = profile.getBirthday();
                                lang = profile.getProfileProperties().getLang();
                                setLang(lang);
                                Card card = profile.getCard();
                                String cardJson = card == null ? null : new Gson().toJson(card);
                                //       Log.e("cardJson1", cardJson);
                                preferences.edit().putString(PrefKeys.CARD, cardJson).apply();


                                setNameInDrawer(name);
                                setSurnameInDrawer(surname);
                                setPhoneInDrawer(phone);
                                setAvatarInDrawer(avatar);
                                saveProfileInfo();

                                View tvAddCard = findViewById(R.id.tv_add_card);
                                View llPublicCard = findViewById(R.id.ll_public_card);
                                if (profile.getCard() != null) {
                                    tvAddCard.setVisibility(View.GONE);
                                    llPublicCard.setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.tv_card_name)).setText(profile.getCard().getName());
                                    ((TextView) findViewById(R.id.tv_card_number))
                                            .setText(profile.getCard().getLastFourNumbers());
                                } else {
                                    tvAddCard.setVisibility(View.VISIBLE);
                                    llPublicCard.setVisibility(View.GONE);
                                }
                                initViews();
                            }

                            createDrawer();
                        }
                    }
                });
    }

    private void setLang(String language) {
        if (language.equals("ua")) language = "uk";
        Locale current = getResources().getConfiguration().locale;
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        if (!current.toString().equals(language)) {
            recreate();
        }
    }


    private void initViews() {

        String visibleName = name + " " + surname;
        ((TextView) findViewById(R.id.tvName)).setText(visibleName);
        String visiblePhone = "+" + phone;
        ((TextView) findViewById(R.id.tvPhone)).setText(visiblePhone);

        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        String initials = Utils.extractInitials(name, surname);
        Utils.displayAvatar(context, ivAvatar, avatar, initials);

        findViewById(R.id.ivAvatar).setOnClickListener(this);
        findViewById(R.id.vLanguage).setOnClickListener(this);
        findViewById(R.id.vChangePass).setOnClickListener(this);
        findViewById(R.id.vExit).setOnClickListener(this);
        findViewById(R.id.vDelete).setOnClickListener(this);
        findViewById(R.id.ll_card).setOnClickListener(this);

    }

    private void saveProfileInfo() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PrefKeys.ID, id);
        editor.putString(PrefKeys.NAME, name);
        editor.putString(PrefKeys.SURNAME, surname);
        editor.putString(PrefKeys.AVATAR, avatar);
        editor.putString(PrefKeys.PHONE, phone);
        editor.apply();
    }


    private void setPreferencesRequest() {

        String allowMoneyRequests = "1";
        String touchIdLogin = "1";
        String lang = "ru";
        ApiClient.getApiInterface()
                .setPreferences(token, allowMoneyRequests, touchIdLogin, lang)
                .enqueue(new BaseCallback<Object>(context, false) {
                             @Override
                             protected void onResult(int code, Object result) {
                                 // if (code == 200)
                             }
                         }
                );
    }

    private MultipartBody.Part makeRequestBodyFile(Uri imageUri) {
        if (imageUri == null) return null;
        File file = new File(imageUri.getPath());
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestFile = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData("img", file.getName(), requestFile);
    }

/*  Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjM4MDEyMzQ1Njc4MSIsImRldmljZSI6NjIsImp0aSI6IjU5ZjJmOWNkNGJjNjZhN2M3YTZjYjRjZDljOTJkMjQ3MmI4OWVlMDYiLCJleHBpcmF0aW9uX2RhdGUiOiIyMDE3LTEyLTE1IDExOjA0OjQ0Iiwic2Vzc2lvbklkIjoiNWEzMjVhY2M3NWMzYiJ9.3VWICYDwa5Dp65NGGjpmKyx0hGHEW5zFgqr-tcpwLCQ

    private void getPreferenceRequest() {
        String key;
        ApiClient.getApiInterface().getPreferences(token, key).enqueue(
                //TODO:
        );
    }

    private void getDevicesRequest(){
        boolean isNeededAllDevices;
        ApiClient.getApiInterface().getDevices(token, isNeededAllDevices).enqueue(
                //TODO:
        );
    }
*/

}
