package com.myhailov.mykola.fishpay.activities.group_spends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.utils.Keys;

public class MemberDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);

        MemberDetails member = getIntent().getExtras().getParcelable(Keys.MEMBER);
    }

    @Override
    public void onClick(View view) {

    }
}
