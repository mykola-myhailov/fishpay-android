package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;

public class AddMembersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        initCustomToolbar("Выберите участников");
        
        initViews();


    }

    private void initViews() {

    }


    //title = "добавить участника"


    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    private void nextActivity() {
        Intent intent = new Intent(context, DistributionActivity.class);
        //   intent.putExtra()
        context.startActivity(intent);
    }

}


