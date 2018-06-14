package com.myhailov.mykola.fishpay.activities.group_spends;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.SpendTransactionsAdapter;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.Transaction;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

public class MemberDetailsActivity extends BaseActivity {

    private MemberDetails member;
    private ArrayList<Transaction> memberTransactions, allTransactions;
    private long memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);

        member = getIntent().getExtras().getParcelable(Keys.MEMBER);
        allTransactions = getIntent().getExtras().getParcelableArrayList(Keys.TRANSACTIONS);
        initViews();
        if (allTransactions != null) initRecyclerView();
        else (findViewById(R.id.tvNoTransactions)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

    }

    private void initViews() {
        String name = member.getName();
        String surname = member.getSurname();
        ((TextView) findViewById(R.id.tvName)).setText(String.format("%s %s", name, surname));
        String initials = Utils.extractInitials(name, surname);
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        String photo = member.getPhoto();
        Utils.displayAvatar(context, ivAvatar, photo, initials);
        Utils.setText((TextView) findViewById(R.id.tvSum), Utils.pennyToUah(member.getSum()));
        Utils.setText((TextView) findViewById(R.id.tvBalance), Utils.pennyToUah(member.getOverpaiment()));
        Utils.setText((TextView) findViewById(R.id.tvForYou), member.getRelativeBallance());
    }


    private void initRecyclerView() {
        memberTransactions = new ArrayList<>();
        memberId = member.getId();
        for (Transaction transaction : allTransactions) {
            if( transaction.getMemberFromId() == memberId || transaction.getMemberToId() == memberId)
                memberTransactions.add(transaction);
            }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new SpendTransactionsAdapter(context, memberTransactions));
    }
}
