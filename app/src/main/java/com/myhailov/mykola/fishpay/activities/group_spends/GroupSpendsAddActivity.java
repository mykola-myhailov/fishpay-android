package com.myhailov.mykola.fishpay.activities.group_spends;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.utils.Keys;

public class GroupSpendsAddActivity extends BaseActivity {
    private String json;
    private GroupSpend spend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_spends_add);
        json = getIntent().getExtras().getString(Keys.JSON_ELEMENT, "");
        spend = new Gson().fromJson(json, GroupSpend.class);


        ((TextView) findViewById(R.id.tv_description)).setText(getString(R.string.group_spends_add_success, spend.getTitle()));
        findViewById(R.id.tv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                startActivity(new Intent(context, SpendDetailActivity.class)
                        .putExtra(Keys.SPEND, spend));
                finish();
                break;

        }

    }
}
