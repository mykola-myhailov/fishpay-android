package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ContactsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        createDrawer();
    }

    @Override
    public void onClick(View view) {

    }

}
