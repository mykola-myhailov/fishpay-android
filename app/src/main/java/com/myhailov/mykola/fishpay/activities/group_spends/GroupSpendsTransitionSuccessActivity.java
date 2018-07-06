package com.myhailov.mykola.fishpay.activities.group_spends;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.GroupSpendsActivity;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult;
import com.myhailov.mykola.fishpay.utils.PrefKeys;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_ID;

public class GroupSpendsTransitionSuccessActivity extends BaseActivity {

    private String title;
    private SpendDetailResult spendDetail;
    private long myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_spends_transition_success);
        myUserId = Long.valueOf(getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, "0"));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString(CHARITY_ID);
        }

        ((TextView) findViewById(R.id.textView9)).setText(getString(R.string.tranfer_successfully, title));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_work:
                Intent intent = new Intent(context, GroupSpendsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }

    }

}
