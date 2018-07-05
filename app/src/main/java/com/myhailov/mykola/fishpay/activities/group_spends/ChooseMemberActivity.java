package com.myhailov.mykola.fishpay.activities.group_spends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.GroupSpendMemberAdapter;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.utils.Keys;

import java.util.ArrayList;

import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;

public class ChooseMemberActivity extends BaseActivity {
    private RecyclerView rvMembers;

    private ArrayList<MemberDetails> members;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_member);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            members = getIntent().getExtras().getParcelableArrayList(Keys.MEMBERS);
            title = getIntent().getExtras().getString(TITLE, "");
        }
        initCustomToolbar(title);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
        }

    }

    private void initView() {
        rvMembers = findViewById(R.id.rv_members);
        rvMembers.setLayoutManager(new LinearLayoutManager(context));
        rvMembers.setAdapter(new GroupSpendMemberAdapter(context, members, listener));

        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private GroupSpendMemberAdapter.OnItemClickListener listener = new GroupSpendMemberAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(MemberDetails details) {
            setResult(RESULT_OK, new Intent().putExtra(Keys.MEMBER, details));
            finish();
        }
    };
}
