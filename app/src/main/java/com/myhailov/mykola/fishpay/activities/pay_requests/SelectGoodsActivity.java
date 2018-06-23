package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.GoodsSelectAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.GOODS;
import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_TOTAL_PRICE;

public class SelectGoodsActivity extends BaseActivity {
    public static final int REQUEST_GOODS = 189;

    private ArrayList<GoodsResults> goods = new ArrayList<>();
    private List<GoodsResults> selectedGoods = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private long totalPrice = 0;
    private GoodsSelectAdapter.OnItemClickListener rvListener = new GoodsSelectAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(boolean isPlus, int position, TextView textView) {
            if (isPlus) {
                plusGoods(position, textView);
            } else {
                minusGoods(position, textView);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_goods);

        initCustomToolbar(getString(R.string.select_goods));
        initViews();
        getGoods();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_form_account:
                selectedGoods.clear();
                addGoods();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(GOODS, (ArrayList) selectedGoods);
                resultIntent.putExtra(GOODS_TOTAL_PRICE, totalPrice);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_goods);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        tvTotalPrice = findViewById(R.id.total_price);

        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.tv_form_account).setOnClickListener(this);
    }


    private void getGoods() {
        ApiClient.getApiInterface()
                .getUserGoods(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GoodsResults>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GoodsResults> result) {
                        if (result != null && result.size() != 0) {
                            goods = result;
                            recyclerView.setAdapter(new GoodsSelectAdapter(context, goods, rvListener));

                        }
                    }
                });

    }


    private void plusGoods(int position, TextView textView) {
        GoodsResults item = goods.get(position);
        if (item.getCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        }
        totalPrice += item.getPrice();
        item.setCount(item.getCount() + 1);
        textView.setText(item.getCount() + "");
        tvTotalPrice.setText(Utils.pennyToUah(totalPrice));

    }

    private void minusGoods(int position, TextView textView) {
        GoodsResults item = goods.get(position);
        if (item.getCount() == 1) {
            textView.setVisibility(View.INVISIBLE);
        }
        if (item.getCount() != 0) {
            totalPrice -= item.getPrice();
            item.setCount(item.getCount() - 1);
            textView.setText(item.getCount() + "");
            tvTotalPrice.setText(Utils.pennyToUah(totalPrice));
        }
    }

    private void addGoods(){
        for (GoodsResults item : goods) {
            if (item.getCount() != 0){
                selectedGoods.add(item);
            }
        }
    }


}
