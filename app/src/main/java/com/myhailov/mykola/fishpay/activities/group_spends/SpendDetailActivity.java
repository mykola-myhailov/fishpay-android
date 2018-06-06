package com.myhailov.mykola.fishpay.activities.group_spends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.SpendMembersAdapter;
import com.myhailov.mykola.fishpay.adapters.SpendTransactionsAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult.Transaction;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class SpendDetailActivity extends BaseActivity{

    private TextView tvAmount;
    private RecyclerView recyclerView;

    private ArrayList<MemberDetails> members;
    private ArrayList<Transaction> transactions;
    private GroupSpend spend;
    private long spendId;
    private boolean iAmCreator;
    private long myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_detail);
        Bundle extras = getIntent().getExtras();
        spend= extras.getParcelable(Keys.SPEND);
        spendId = spend.getId();
        tvAmount = findViewById(R.id.tv_amount);

        myUserId = Long.valueOf(getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, "0"));

        recyclerView = findViewById(R.id.rvContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        spendDetailRequest();
    }

    private void spendDetailRequest() {
        if (Utils.isOnline(context))
            ApiClient.getApiInterface().getSpendingDetails(TokenStorage.getToken(context), spendId)
            .enqueue(new BaseCallback<SpendDetailResult>(context, true) {
                @Override
                protected void onResult(int code, SpendDetailResult result) {
                    if (result == null) return;
                    tvAmount.setText(String.valueOf(result.getSum()));
                    members = result.getMembers();
                    transactions = result.getTransactions();
                    initCustomToolbar(result.getTitle());
                    iAmCreator = (result.getCreatorId() == myUserId);
                    initToggleButtons();
                    initViews();

                }
            });
    }

    private void initViews() {
        if (iAmCreator) (findViewById(R.id.iv_plus)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.iv_plus:
                context.startActivity(new Intent(context, AddMoreSpendsActivity.class)
                .putExtra(Keys.SPEND, spend));
                break;
            case R.id.rlMemberItem:
                context.startActivity(new Intent(context, MemberDetailsActivity.class)
                        .putExtra(Keys.MEMBER, (MemberDetails) view.getTag()));
                break;
        }
    }

    private void initToggleButtons() {
        final ToggleSwitch toggleSwitch = findViewById(R.id.toggleSwitch);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Список");
        labels.add("Участники");
        toggleSwitch.setLabels(labels);
        toggleSwitch.setCheckedTogglePosition(0);
        recyclerView.setAdapter(new SpendTransactionsAdapter(context, transactions));
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
               if (position == 0)
                    recyclerView.setAdapter(new SpendTransactionsAdapter(context, transactions));
                else recyclerView.setAdapter(new SpendMembersAdapter(context, members));
            }
        });
    }
}

