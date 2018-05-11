package com.myhailov.mykola.fishpay.activities.charity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.CharityListAdapter;
import com.myhailov.mykola.fishpay.api.results.CharityResult.CharityProgram;

import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_AMOUNT;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_LIST;

public class CharityListActivity extends BaseActivity {
    private List<CharityProgram> charities;
    TextView tvAmount;
    private RecyclerView rvCharityList;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_list);
        initToolBar("Список взносов");
        if (getIntent() != null){
            charities = (ArrayList) getIntent().getSerializableExtra(CHARITY_LIST);
            amount = getIntent().getDoubleExtra(CHARITY_AMOUNT, 0.00);
        }
        tvAmount = findViewById(R.id.tv_amount);
        tvAmount.setText(amount + "");

        rvCharityList = findViewById(R.id.rv_charity_list);
        rvCharityList.setLayoutManager(new LinearLayoutManager(context));
        rvCharityList.setAdapter(new CharityListAdapter(context, charities));
    }

    @Override
    public void onClick(View v) {

    }
}
