package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.CharityActivity;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_ID;

public class CharitySuccessActivity extends BaseActivity {
    TextView tvLink;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_success);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString(CHARITY_ID);
        }

        findViewById(R.id.tv_to_work).setOnClickListener(this);
        tvLink = findViewById(R.id.tv_link);
        tvLink.setText(getString(R.string.charity_link, id));
        tvLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_work:
                Intent intent = new Intent(context, CharityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.tv_link:
                break;
        }

    }
}
