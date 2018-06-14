package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.PayRequestActivity;

public class SuccessPayRequestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_pay_request);
        findViewById(R.id.tv_return_to_work).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_return_to_work:
                startActivity(new Intent(context, PayRequestActivity.class));
                break;
        }

    }


}
