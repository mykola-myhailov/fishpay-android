package com.myhailov.mykola.fishpay.activities.group_spends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.GroupSpendsMenuAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class GroupSpendMenuActivity extends BaseActivity {
    private RecyclerView rvGroupSpends;

    private String title;
    private long myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_spend_menu);
        myUserId = Long.valueOf(getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, "0"));

        title = getIntent().getExtras().getString(Keys.TITLE, "");
        initCustomToolbar(title);

        initView();
        groupSpendsRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void initView(){
        findViewById(R.id.ivBack).setOnClickListener(this);
        rvGroupSpends = findViewById(R.id.rv_spends_menu);
        rvGroupSpends.setLayoutManager(new LinearLayoutManager(context));
    }

    private void groupSpendsRequest() {
        ApiClient.getApiInterface().getSpending(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GroupSpend>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GroupSpend> result) {
                        Collections.reverse(result);
                        rvGroupSpends.setAdapter(new GroupSpendsMenuAdapter(context, result, rvListener));
                    }
                });
    }

    private GroupSpendsMenuAdapter.OnItemClickListener rvListener = new GroupSpendsMenuAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(GroupSpend item) {
            spendDetailRequest(item, item.getId());

        }
    };

    private void spendDetailRequest(final GroupSpend item, long spendId) {
        if (Utils.isOnline(context))
            ApiClient.getApiInterface().getSpendingDetails(TokenStorage.getToken(context), spendId)
                    .enqueue(new BaseCallback<SpendDetailResult>(context, true) {
                        @Override
                        protected void onResult(int code, SpendDetailResult result) {
                            if (result == null) return;
                            String memberId = "";
                            for (MemberDetails details : result.getMembers()) {
                                if (!TextUtils.isEmpty(details.getUserId()) && details.getUserId().equals(myUserId + "")){
                                    memberId = details.getId() + "";
                                    break;
                                }
                            }

                            setResult(RESULT_OK, new Intent().putExtra(Keys.SPEND, item)
                            .putExtra(Keys.MEMBER_ID, memberId));
                            finish();
                        }
                    });
    }
}
