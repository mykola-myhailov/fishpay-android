package com.myhailov.mykola.fishpay.activities.group_spends;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.TransactionActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.CreatePayRequestActivity;
import com.myhailov.mykola.fishpay.adapters.SpendTransactionsAdapter;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.Transaction;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import static com.myhailov.mykola.fishpay.activities.TransactionActivity.COMMON_SPENDING;
import static com.myhailov.mykola.fishpay.utils.Keys.COMMENT;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBERS;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.ROLE;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND_CREATOR;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;
import static com.myhailov.mykola.fishpay.utils.Keys.TYPE;

public class MemberDetailsActivity extends BaseActivity {

    private TextView tvManually, tvEqualiseExpenses, tvExpense, tvSum, tvForYou, tvBalance, tvPecent;

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
        if (allTransactions != null && allTransactions.size() > 0) initRecyclerView();
        else (findViewById(R.id.tvNoTransactions)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_manually:
//                showInfoAlert(context);
                // TODO: 06.07.2018 в розробці
                context.startActivity((new Intent(context, ManualTransferActivity.class))
                        .putExtra(SPEND, spend)
                        .putExtra(MEMBER, member)
                        .putExtra(MEMBERS, members));

                break;
            case R.id.tv_equalise_expenses:
//                showInfoAlert(context);
                // TODO: 06.07.2018 в розробці
                // TODO: 06.07.2018 fix request
                if (member.getRelativeBallance() > 0) {
//                    context.startActivity((new Intent(context, ManualTransferActivity.class))
//                            .putExtra(SPEND, spend)
//                            .putExtra(MEMBER, member)
//                            .putExtra(MEMBERS, members));
                    long myUserId = Long.valueOf(getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                            .getString(PrefKeys.ID, "0"));
                    String idCreator = "";
                    for (MemberDetails memberDetails : members) {
                        if (!TextUtils.isEmpty(memberDetails.getUserId()) && memberDetails.getUserId().equals(myUserId + "")) {
                            idCreator = memberDetails.getId() + "";
                        }
                    }

                    context.startActivity(new Intent(context, TransactionActivity.class)
                            .putExtra(TYPE, COMMON_SPENDING)
                            .putExtra(SPEND_ID, spend.getId() + "")
                            .putExtra(MEMBER_ID, member.getId() + "")
                            .putExtra(SPEND_CREATOR, idCreator)
                            .putExtra(COMMENT, getString(R.string.contribution_of_common_spend))
                            .putExtra(MEMBER, member));
                }
                if (member.getRelativeBallance() < 0) {

                    String idCreator = "";
                    for (MemberDetails memberDetails : members) {
                        if (memberDetails.getRole().equals("creator")) {
                            idCreator = memberDetails.getId() + "";
                        }
                    }

                    context.startActivity(new Intent(context, CreatePayRequestActivity.class)
                            .putExtra(SPEND, "")
                            .putExtra(SPEND_ID, spend.getId() + "")
                            .putExtra(MEMBER_ID, member.getId() + "")
                            .putExtra(SPEND_CREATOR, idCreator)
                            .putExtra(COMMENT, getString(R.string.contribution_of_common_spend))
                            .putExtra(MEMBERS, member));


//                    SearchedContactsResult.SearchedContact contact = new SearchedContactsResult.SearchedContact();
//                    contact.setPhone(member.getPhone());
//                    contact.setName(member.getName());
//                    contact.setSurname(member.getSurname());
//
//                    Member memb = new Member();
//                    memb.setPhone(member.getPhone());
//                    memb.setFirstName(member.getName());
//                    memb.setLastName(member.getSurname());
//                    memb.setAmountToPay((int) Math.abs(member.getRelativeBallance()));
////                    memb.setAmountToPay(500);
//                    context.startActivity((new Intent(context, CreatePayRequestActivity.class))
//                            .putExtra(SEARCHED_CONTACT, contact)
//                            .putExtra(COMMENT, getString(R.string.contribution_of_common_spend))
//                            .putExtra(MEMBER, memb));
                }
                break;
            case R.id.tv_expense:
//                showInfoAlert(context);
                // TODO: 06.07.2018 в розробці
                context.startActivity(new Intent(context, AddMoreSpendsActivity.class)
                        .putExtra(MEMBER, member)
                        .putExtra(Keys.SPEND, spend));

                break;
        }
    }

    private void initViews() {
        tvManually = findViewById(R.id.tv_manually);
        tvEqualiseExpenses = findViewById(R.id.tv_equalise_expenses);
        tvExpense = findViewById(R.id.tv_expense);

        String name = member.getName();
        String surname = member.getSurname();
        ((TextView) findViewById(R.id.tvName)).setText(String.format("%s %s", name, surname));
        String initials = Utils.extractInitials(name, surname);
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        String photo = member.getPhoto();
        Utils.displayAvatar(context, ivAvatar, photo, initials);
        tvBalance = findViewById(R.id.tvBalance);
        tvForYou = findViewById(R.id.tvForYou);
        tvSum = findViewById(R.id.tvSum);
        tvPecent = findViewById(R.id.tv_percent);
//        int percent =(int) Math.round(Math.abs(member.getPartInOverpaiment() * 100));
        tvPecent.setText(member.getPart() + "");
        if (member.getSum() == 0) {
            tvSum.setText("0.00");
        } else {
            Utils.setText(tvSum, Utils.pennyToUah(member.getSum()));
        }
        if (member.getOverpaiment() == 0) {
            tvBalance.setText("0.00");
        } else {
            Utils.setText(tvBalance, Utils.pennyToUah(member.getOverpaiment()));
        }
        if ((long) member.getRelativeBallance() == 0) {
            tvForYou.setText("0.00");
            tvEqualiseExpenses.setVisibility(View.GONE);
        } else {
            Utils.setText(tvForYou, Utils.pennyToUah((long) member.getRelativeBallance()));
        }

        if (!TextUtils.isEmpty(member.getPhone()) && !member.getRole().equals("no_account")) {
            ((TextView) findViewById(R.id.tvRole)).setText(member.getPhone());
        } else {
            ((TextView) findViewById(R.id.tvRole)).setText(getString(R.string.user_unknown));
        }


        if (role.equals("creator")) {
            tvExpense.setVisibility(View.VISIBLE);
        }
        if (member.getRole().equals("no_account") || role.isEmpty()) {
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
        if (memberTransactions.size() > 0) {
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new SpendTransactionsAdapter(context, memberTransactions));
        } else (findViewById(R.id.tvNoTransactions)).setVisibility(View.VISIBLE);
    }
}
