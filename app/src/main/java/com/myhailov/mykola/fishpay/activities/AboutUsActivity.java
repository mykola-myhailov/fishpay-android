package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.BuildConfig;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

public class AboutUsActivity extends BaseActivity {
    TextView tvVersion, tvVersionApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initCustomToolbar(getString(R.string.about_us));

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_site:
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fishpay.net"));
                startActivity(i);
                break;
            case R.id.tv_email:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "support@fishpay.net", null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
        }

    }

    private void initViews() {
        tvVersion = findViewById(R.id.tv_version);
        tvVersionApi = findViewById(R.id.tv_version_api);

        tvVersion.setText(BuildConfig.VERSION_NAME);

        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.tv_site).setOnClickListener(this);
        findViewById(R.id.tv_email).setOnClickListener(this);
    }

    private void getVersion() {
        ApiClient.getApiInterface().getInfoVersion(TokenStorage.getToken(context)).enqueue(new BaseCallback<Object>(context, true) {
            @Override
            protected void onResult(int code, Object result) {

            }
        });
    }
}
