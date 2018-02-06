package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;

public class ContactsPermissionsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_permissions);

        findViewById(R.id.tvPermisson1).setOnClickListener(this);
        findViewById(R.id.tvPermisson2).setOnClickListener(this);
        setContentView(R.layout.activity_drawer_sample);

        initDrawerToolbar(getString(R.string.my_contacts));
    }

    @Override
    public void onClick(View view) {
        context.startActivity(new Intent(context, ContactsActivity.class));
    }
}
