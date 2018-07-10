package com.myhailov.mykola.fishpay.activities.group_spends;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.GroupSpendsActivity;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.JSON_ELEMENT;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBERS;
import static com.myhailov.mykola.fishpay.utils.Keys.ROLE;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;
import static com.myhailov.mykola.fishpay.utils.Keys.TRANSACTIONS;

public class GroupSpendsTransitionSuccessActivity extends BaseActivity {

    private SpendDetailResult spendDetail;
    private String json;
    private long myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_spends_transition_success);
        myUserId = Long.valueOf(getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, "0"));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            json = extras.getString(JSON_ELEMENT, "");
            spendDetail = new Gson().fromJson(json, SpendDetailResult.class);
        }

        findViewById(R.id.tv_to_work).setOnClickListener(this);
        ((TextView) findViewById(R.id.textView9)).setText(getString(R.string.tranfer_successfully, spendDetail.getTitle()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_work:
                sendResult();
                break;
        }

    }
    private void sendResult(){
        String role = "";
        for (MemberDetails memberDetails : spendDetail.getMembers()) {
            if (memberDetails.getUserId() != null && memberDetails.getUserId().equals(myUserId + "")) {
                role = memberDetails.getRole();
                break;
            }
        }
        MemberDetails member = new MemberDetails();

        for (MemberDetails memberDetails : spendDetail.getMembers()) {
            if (memberDetails.getId() == spendDetail.getMemberId()){
                member = memberDetails;
            }
        }

        GroupSpend spend = new GroupSpend(spendDetail.getId());
        context.startActivity(new Intent(context, MemberDetailsActivity.class)
                .putExtra(ROLE, role)
                .putExtra(SPEND, spend)
                .putExtra(MEMBERS, spendDetail.getMembers())
                .putExtra(TITLE, spendDetail.getTitle())
                .putExtra(TRANSACTIONS, spendDetail.getTransactions())
                .putExtra(Keys.MEMBER, member));
        finish();
    }

    @Override
    public void onBackPressed() {
        sendResult();
    }
}
