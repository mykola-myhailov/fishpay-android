package com.myhailov.mykola.fishpay.activities.drawer_activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;

public class JointPurchasesActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchases);

        setContentView(R.layout.activity_drawer_sample);

        initToolbar(getString(R.string.joint_purchase));
        createDrawer();
    }

    @Override
    public void onClick(View view) {

    }
}
