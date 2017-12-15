package com.myhailov.mykola.fishpay.activities.drawer;

import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;

public class PayRequestActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_pay_request);

        setContentView(R.layout.activity_drawer_sample);

        initToolbar(getString(R.string.pay_request));
        createDrawer();
    }

    @Override
    public void onClick(View view) {

    }
}
