package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;

public class AddMembersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
    }


    //title = "добавить участника"


    @Override
    public void onClick(View view) {

        nextActivity();
    }

    private void nextActivity() {
        Intent intent = new Intent(context, DistributionActivity.class);
        //   intent.putExtra()
        context.startActivity(intent);
    }

}


