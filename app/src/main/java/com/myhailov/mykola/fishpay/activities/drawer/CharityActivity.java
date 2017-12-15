package com.myhailov.mykola.fishpay.activities.drawer;

import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;

public class CharityActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_charity);
        setContentView(R.layout.activity_drawer_sample);

        initToolbar(getString(R.string.charity));
        createDrawer();
    }

    @Override
    public void onClick(View view) {

    }
}
