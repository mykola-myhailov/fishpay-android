package com.myhailov.mykola.fishpay.activities.drawer;

import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;

public class ActivityActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_activity);
        setContentView(R.layout.activity_drawer_sample);

        initDrawerToolbar(getString(R.string.activity));
        createDrawer();
    }

    @Override
    public void onClick(View view) {

    }
}
