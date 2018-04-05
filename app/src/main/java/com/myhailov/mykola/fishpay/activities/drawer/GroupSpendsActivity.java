package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.group_spends.CreateGroupSpendActivity;
import com.myhailov.mykola.fishpay.activities.joint_purchases.AddJoinPurchaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

public class GroupSpendsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_group_spends);
        setContentView(R.layout.activity_group_spends);

        initDrawerToolbar(getString(R.string.joint_costs));
        createDrawer();

        groupSpendsRequest();
    }

    private void groupSpendsRequest() {
        ApiClient.getApiClient().getSpending(TokenStorage.getToken(context))
        // TODO: .enqueue();
        ;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        context.startActivity(new Intent(context, CreateGroupSpendActivity.class));
        return true;
    }

    @Override
    public void onClick(View view) {

    }


}
