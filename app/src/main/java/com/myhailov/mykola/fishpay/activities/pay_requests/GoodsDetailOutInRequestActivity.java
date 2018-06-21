package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GoodsByIdResult;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_ID;

public class GoodsDetailOutInRequestActivity extends BaseActivity {
    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvId;
    private ImageView ivPhoto;


    private GoodsByIdResult goods;
    private long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail_out_in_request);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong(GOODS_ID, 0);
        }

        assignViews();
        getGoodsById(id + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    private void assignViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvPrice = findViewById(R.id.tv_price);
        tvId = findViewById(R.id.tv_id);
        ivPhoto = findViewById(R.id.ivGoods);
        findViewById(R.id.iv_back).setOnClickListener(this);

    }

    private void setValue() {
        tvTitle.setText(goods.getTitle());
        tvId.setText("ID" + goods.getId());
        tvPrice.setText(Utils.pennyToUah(Integer.parseInt(goods.getPrice())));
        Utils.displayGoods(context, ivPhoto, goods.getMainPhoto(), id);

    }

    private void getGoodsById(String id) {
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
        } else {
            ApiClient.getApiInterface().getGoodsDetails(TokenStorage.getToken(context), id)
                    .enqueue(new BaseCallback<GoodsByIdResult>(context, true) {
                        @Override
                        protected void onResult(int code, GoodsByIdResult result) {
                            if (result != null) {
                                goods = result;
                                setValue();
                            }
                        }
                    });
        }
    }
}
