package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;

public class IncomingDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_details);

        initToolBar("Запрос на оплату");
    }

    @Override
    public void onClick(View view) {

    }
}
