package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.CharityActivity;

public class CharitySuccessActivity extends BaseActivity {
    TextView tvLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_success);
        findViewById(R.id.tv_to_work).setOnClickListener(this);
        tvLink = findViewById(R.id.tv_link);
        tvLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_to_work:
                startActivity(new Intent(context, CharityActivity.class));
                break;
            case R.id.tv_link:
                break;
        }

    }
}
