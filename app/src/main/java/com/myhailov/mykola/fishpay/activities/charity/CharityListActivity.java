package com.myhailov.mykola.fishpay.activities.charity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.CharityListAdapter;
import com.myhailov.mykola.fishpay.api.results.CharityResult.CharityProgram;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_AMOUNT;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_LIST;

public class CharityListActivity extends BaseActivity {
    TextView tvAmount;
    private List<CharityProgram> charities;
    private RecyclerView rvCharityList;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_list);
        initCustomToolbar("Список взносов");
        if (getIntent() != null) {
            charities = (ArrayList) getIntent().getSerializableExtra(CHARITY_LIST);
            amount = getIntent().getIntExtra(CHARITY_AMOUNT, 0);
        }
        tvAmount = findViewById(R.id.tv_amount);
        tvAmount.setText(Utils.pennyToUah(amount));

        rvCharityList = findViewById(R.id.rv_charity_list);
        rvCharityList.setLayoutManager(new LinearLayoutManager(context));
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
