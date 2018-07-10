package com.myhailov.mykola.fishpay.activities.profile;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.Utils;

public class PublicOfferActivity extends BaseActivity {
    private TextView tvOffer;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_offer);
        initCustomToolbar(getString(R.string.offer));
        tvOffer = findViewById(R.id.tv_offer);
        tvOffer.setMovementMethod(new ScrollingMovementMethod());
        getOffer();
        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private void getOffer() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().getOffer().enqueue(new BaseCallback<String>(context, true) {
                @Override
                protected void onResult(int code, String result) {
                    tvOffer.setText(result);
                }
            });

        } else {
            toast(getString(R.string.no_internet));
        }
    }
}
