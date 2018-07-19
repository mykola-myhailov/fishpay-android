package com.myhailov.mykola.fishpay.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

import retrofit2.Call;

import static com.myhailov.mykola.fishpay.utils.Keys.LANG;

public class ChangeLanguageActivity extends BaseActivity {
    public static final int CHANGE_LANG = 671;

    String lang = "";
    CheckedTextView tvUa, tvRu, tvEn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        initCustomToolbar(getString(R.string.lang_ui));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            lang = bundle.getString(LANG, "ru");
        }

        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.rl_save:
                setPreferencesRequest();
                break;
            case R.id.tv_en:
                lang = "en";
                setCheckedTrue(tvEn);
                break;
            case R.id.tv_ua:
                lang = "ua";
                setCheckedTrue(tvUa);
                break;
            case R.id.tv_ru:
                lang = "ru";
                setCheckedTrue(tvRu);
                break;
        }
    }

    private void initView() {
        tvUa = findViewById(R.id.tv_ua);
        tvRu = findViewById(R.id.tv_ru);
        tvEn = findViewById(R.id.tv_en);
        setCheckedFalse();
        setLang();

        tvRu.setOnClickListener(this);
        tvUa.setOnClickListener(this);
        tvEn.setOnClickListener(this);
        findViewById(R.id.rl_save).setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private void setCheckedFalse() {
        tvEn.setChecked(false);
        tvUa.setChecked(false);
        tvRu.setChecked(false);
    }

    private void setCheckedTrue(CheckedTextView textView) {
        setCheckedFalse();
        textView.setChecked(true);
    }

    private void setLang(){
        switch (lang){
            case "ua":
                tvUa.setChecked(true);
                break;
            case "ru":
                tvRu.setChecked(true);
                break;
            case "en":
                tvEn.setChecked(true);
                break;
        }
    }

    private void setPreferencesRequest() {

        String allowMoneyRequests = "1";
        String touchIdLogin = "1";
        String token = TokenStorage.getToken(context);
        ApiClient.getApiInterface()
                .setPreferences(token, allowMoneyRequests, touchIdLogin, lang)
                .enqueue(new BaseCallback<Object>(context, true) {
                             @Override
                             protected void onResult(int code, Object result) {
                                  if (code == 200){
                                      setResult(RESULT_OK, new Intent().putExtra(LANG, lang));
                                      finish();
                                  }

                             }
                         }
                );
    }


}
