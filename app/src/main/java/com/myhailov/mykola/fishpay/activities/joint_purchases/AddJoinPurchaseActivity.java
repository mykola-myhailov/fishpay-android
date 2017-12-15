package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;

public class AddJoinPurchaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join_purchase);
    }


    @Override
    public void onClick(View view) {

        nextActivity();
    }

    private void nextActivity() {
        Intent intent = new Intent(context, AddMembersActivity.class);
     //   intent.putExtra()
        context.startActivity(intent);
    }


    //title = "создание общей покупки"



}
