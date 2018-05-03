package com.myhailov.mykola.fishpay.activities.group_spends;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult.Transaction;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class SpendDetailActivity extends BaseActivity{

    private TextView tvAmount;
    private RecyclerView recyclerView;

    private ArrayList<MemberDetails> members;
    private ArrayList<Transaction> transactions;
    private long spendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_detail);

        spendId = getIntent().getExtras().getLong(Keys.SPEND_ID);

        tvAmount = findViewById(R.id.tv_amount);

        recyclerView = findViewById(R.id.rvContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        
        spendDetailRequest();
    }

    private void spendDetailRequest() {
        if (Utils.isOnline(context))
            ApiClient.getApiClient().getSpendingDetails(TokenStorage.getToken(context), spendId)
            .enqueue(new BaseCallback<SpendDetailResult>(context, true) {
                @Override
                protected void onResult(int code, SpendDetailResult result) {
                    tvAmount.setText(String.valueOf(result.getSum()));
                    members = result.getMembers();
                    transactions = result.getTransactions();
                    initToggleButtons();
                }
            });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void initToggleButtons() {
        final ToggleSwitch toggleSwitch = findViewById(R.id.toggleSwitch);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Список");
        labels.add("Участники");;
        toggleSwitch.setLabels(labels);
        toggleSwitch.setCheckedTogglePosition(0);
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
              //  if (position == 0) recyclerView.setAdapter(new );
            }
        });
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                toggleSwitch.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
            }
        });
    }
}

