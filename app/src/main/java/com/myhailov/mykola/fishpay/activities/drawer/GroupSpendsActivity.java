package com.myhailov.mykola.fishpay.activities.drawer;

import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;

public class GroupSpendsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_group_spends);
        setContentView(R.layout.activity_drawer_sample);

        initToolbar(getString(R.string.joint_costs));
        createDrawer();
    }

    @Override
    public void onClick(View view) {

    }
}
