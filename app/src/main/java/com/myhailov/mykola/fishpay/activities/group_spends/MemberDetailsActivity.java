package com.myhailov.mykola.fishpay.activities.group_spends;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.SpendTransactionsAdapter;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.Transaction;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBERS;
import static com.myhailov.mykola.fishpay.utils.Keys.ROLE;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;
import static com.myhailov.mykola.fishpay.utils.Utils.showInfoAlert;

public class MemberDetailsActivity extends BaseActivity {

    private TextView tvManually, tvEqualiseExpenses, tvExpense;

    private GroupSpend spend;
    private MemberDetails member;
    private ArrayList<Transaction> memberTransactions, allTransactions;
    private ArrayList<MemberDetails> members;
    private long memberId;
    private String title, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);

        member = getIntent().getExtras().getParcelable(MEMBER);
        spend = getIntent().getExtras().getParcelable(Keys.SPEND);
        title = getIntent().getExtras().getString(TITLE, "");
        role = getIntent().getExtras().getString(ROLE, "");
        allTransactions = getIntent().getExtras().getParcelableArrayList(Keys.TRANSACTIONS);
        members = getIntent().getExtras().getParcelableArrayList(Keys.MEMBERS);
        initCustomToolbar(title);
        initViews();
        if (allTransactions != null) initRecyclerView();
        else (findViewById(R.id.tvNoTransactions)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_manually:
                showInfoAlert(context);
                // TODO: 06.07.2018 в розробці
//                context.startActivity((new Intent(context, ManualTransferActivity.class))
//                        .putExtra(SPEND, spend)
//                        .putExtra(MEMBER, member)
//                        .putExtra(MEMBERS, members));
                break;
            case R.id.tv_equalise_expenses:
                showInfoAlert(context);
                // TODO: 06.07.2018 в розробці
                // TODO: 06.07.2018 fix request
//                if (member.getRelativeBallance() > 0) {
//                    context.startActivity((new Intent(context, TransactionActivity.class))
//                            .putExtra(AMOUNT, Utils.pennyToUah((long) member.getRelativeBallance()))
//                            .putExtra(USER_ID, member.getUserId())
//                            .putExtra(NAME, member.getName() + " " + member.getSurname()));
//                }
//                if (member.getRelativeBallance() < 0) {
//                    SearchedContactsResult.SearchedContact contact = new SearchedContactsResult.SearchedContact();
//                    contact.setPhone(member.getPhone());
//                    contact.setName(member.getName());
//                    contact.setSurname(member.getSurname());
//
//                    Member memb = new Member();
//                    memb.setPhone(member.getPhone());
//                    memb.setFirstName(member.getName());
//                    memb.setLastName(member.getSurname());
//                    memb.setAmountToPay((int) member.getRelativeBallance());
//                    context.startActivity((new Intent(context, CreatePayRequestActivity.class))
//                            .putExtra(SEARCHED_CONTACT, contact)
//                            .putExtra(MEMBER, memb));
//                }
                break;
            case R.id.tv_expense:
                showInfoAlert(context);
                // TODO: 06.07.2018 в розробці
//                context.startActivity(new Intent(context, AddMoreSpendsActivity.class)
//                        .putExtra(MEMBER, member)
//                        .putExtra(Keys.SPEND, spend));

                break;
        }
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

        tvManually = findViewById(R.id.tv_manually);
        tvEqualiseExpenses = findViewById(R.id.tv_equalise_expenses);
        tvExpense = findViewById(R.id.tv_expense);


        if (role.equals("creator")) {
            tvExpense.setVisibility(View.VISIBLE);
        }
        if (role.equals("no_account") || role.isEmpty()) {
            tvExpense.setVisibility(View.VISIBLE);
            tvEqualiseExpenses.setVisibility(View.GONE);
        }

        tvManually.setOnClickListener(this);
        tvEqualiseExpenses.setOnClickListener(this);
        tvExpense.setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
    }


    private void initRecyclerView() {
        memberTransactions = new ArrayList<>();
        memberId = member.getId();
        for (Transaction transaction : allTransactions) {
            if (transaction.getMemberFromId() == memberId || transaction.getMemberToId() == memberId)
                memberTransactions.add(transaction);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new SpendTransactionsAdapter(context, memberTransactions));
    }
}
