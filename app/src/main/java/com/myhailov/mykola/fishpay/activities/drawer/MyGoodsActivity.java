package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.goods.CreateGodsActivity;

public class MyGoodsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_drawer_my_goods);

        initToolbar(getString(R.string.my_purchases));
        findViewById(R.id.ivPlus).setOnClickListener(this);
        createDrawer();
    }

    @Override
    public void onClick(View view) {
        context.startActivity(new Intent(context, CreateGodsActivity.class));
    }
}
