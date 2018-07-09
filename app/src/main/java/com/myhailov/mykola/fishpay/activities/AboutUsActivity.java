package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.BuildConfig;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.profile.UserInfoActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

public class AboutUsActivity extends BaseActivity {
    private TextView tvVersion, tvVersionApi, tvLicence;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initCustomToolbar(getString(R.string.about_us));

        initViews();
//        getVersion();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;

        }

    }

    private void initViews() {
        tvVersion = findViewById(R.id.tv_version);
        tvVersionApi = findViewById(R.id.tv_version_api);
        tvLicence = findViewById(R.id.tv_licence);

        tvVersion.setText(BuildConfig.VERSION_NAME);
        preferences = context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);

        SpannableString ss = new SpannableString(getString(R.string.privacy_licence));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(context, UserInfoActivity.class)
                        .putExtra(Keys.PHOTO, preferences.getString(PrefKeys.AVATAR, ""))
                        .putExtra(Keys.NAME, preferences.getString(PrefKeys.NAME, ""))
                        .putExtra(Keys.SURNAME, preferences.getString(PrefKeys.SURNAME, ""))
                        .putExtra(Keys.BIRTHDAY, preferences.getString(PrefKeys.BIRTHDAY, ""))
                        .putExtra(Keys.EMAIL, preferences.getString(PrefKeys.EMAIL, "")));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, 20, ss.length(), 0);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey_material_500)), 20, ss.length(), 0);


        tvLicence.setText(ss);
        tvLicence.setMovementMethod(LinkMovementMethod.getInstance());
//        tvLicence.setHighlightColor(getResources().getColor(R.color.grey_material_500));

        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private void getVersion() {
        ApiClient.getApiInterface().getInfoVersion(TokenStorage.getToken(context)).enqueue(new BaseCallback<Object>(context, true) {
            @Override
            protected void onResult(int code, Object result) {

            }
        });
    }
}
