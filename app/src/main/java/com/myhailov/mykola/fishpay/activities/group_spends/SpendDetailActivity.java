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
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult;
import com.myhailov.mykola.fishpay.api.results.Transaction;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBERS;
import static com.myhailov.mykola.fishpay.utils.Keys.ROLE;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;
import static com.myhailov.mykola.fishpay.utils.Keys.TRANSACTIONS;
import static com.myhailov.mykola.fishpay.utils.Utils.showInfoAlert;

public class SpendDetailActivity extends BaseActivity {
    public static final int ADD_SPEND_REQUESR = 96;

    private TextView tvAmount;
    private RecyclerView recyclerView;

    private SpendDetailResult spendDetail;
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
        spend = extras.getParcelable(Keys.SPEND);
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
                            spendDetail = result;
                            tvAmount.setText(Utils.pennyToUah(result.getSum()));
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
        findViewById(R.id.iv_settings).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.iv_settings:
                spendDetail.setMembers(null);
                spendDetail.setTransactions(null);
                startActivity(new Intent(context, DetailGroupSpendsActivity.class)
                        .putExtra(SPEND, spendDetail));
                break;
            case R.id.iv_plus:
                showInfoAlert(context);
                // TODO: 06.07.2018 в розробці
//                startActivityForResult(new Intent(context, AddMoreSpendsActivity.class)
//                        .putExtra(Keys.SPEND, spend), ADD_SPEND_REQUESR);
                break;
            case R.id.rlMemberItem:
                String role = "";
                for (MemberDetails memberDetails : spendDetail.getMembers()) {
                    if (memberDetails.getUserId() != null && memberDetails.getUserId().equals(myUserId + "")) {
                        role = memberDetails.getRole();
                        break;
                    }
                }
                context.startActivity(new Intent(context, MemberDetailsActivity.class)
                        .putExtra(ROLE, role)
                        .putExtra(SPEND, spend)
                        .putExtra(MEMBERS, spendDetail.getMembers())
                        .putExtra(TITLE, spendDetail.getTitle())
                        .putExtra(TRANSACTIONS, spendDetail.getTransactions())
                        .putExtra(Keys.MEMBER, (MemberDetails) view.getTag()));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_SPEND_REQUESR && resultCode == RESULT_OK) {
            recreate();
        }
    }

    private void initToggleButtons() {
        final ToggleSwitch toggleSwitch = findViewById(R.id.toggleSwitch);
        ArrayList<String> labels = new ArrayList<>();
        labels.add(getString(R.string.list));
        labels.add(getString(R.string.participants));
        toggleSwitch.setLabels(labels);
        toggleSwitch.setCheckedTogglePosition(0);
        if (transactions.size() != 0) {
            hideEmptyList();
        } else {
            showEmptyList();
        }
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener() {

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position == 0) {
                    if (transactions.size() != 0) {
                        hideEmptyList();
                    } else {
                        showEmptyList();
                    }
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_empty_list).setVisibility(View.GONE);
                    recyclerView.setAdapter(new SpendMembersAdapter(context, members));
                }
            }
        });
    }

    private void showEmptyList() {
        recyclerView.setVisibility(View.GONE);
        findViewById(R.id.tv_empty_list).setVisibility(View.VISIBLE);
    }

    private void hideEmptyList() {
        recyclerView.setAdapter(new SpendTransactionsAdapter(context, transactions));
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.tv_empty_list).setVisibility(View.GONE);
    }
}

