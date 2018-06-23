package com.myhailov.mykola.fishpay.activities.charity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.CharityListAdapter;
import com.myhailov.mykola.fishpay.api.results.CharityResult;
import com.myhailov.mykola.fishpay.api.results.CharityResult.CharityProgram;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_AMOUNT;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_LIST;

public class CharityListActivity extends BaseActivity {
    TextView tvAmount;
    private List<CharityResult.Donation> charities;
    private RecyclerView rvCharityList;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_list);
        initCustomToolbar(getString(R.string.list_of_contributions));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            charities = (ArrayList) extras.getSerializable(CHARITY_LIST);
            amount = extras.getInt(CHARITY_AMOUNT, 0);
        }
        tvAmount = findViewById(R.id.tv_amount);
        String am = Utils.pennyToUah(amount);
        if (am.equals("0")){
            am = "0.00";
        }
        tvAmount.setText(am);

        rvCharityList = findViewById(R.id.rv_charity_list);
        rvCharityList.setLayoutManager(new LinearLayoutManager(context));
        Collections.reverse(charities);
        rvCharityList.setAdapter(new CharityListAdapter(context, charities));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }
}
