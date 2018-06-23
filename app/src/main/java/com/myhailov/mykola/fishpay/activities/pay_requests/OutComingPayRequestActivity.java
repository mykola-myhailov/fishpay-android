package com.myhailov.mykola.fishpay.activities.pay_requests;

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
import com.myhailov.mykola.fishpay.adapters.GoodsOutIncomAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.InvoiceDetailsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_ID;

public class OutComingPayRequestActivity extends BaseActivity {
    private InvoiceDetailsResult detailsResult;
    private long requestId;

    private RecyclerView rvGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_coming_pay_request);
        initToolBar(getString(R.string.out_coming_pay_request), R.color.black2);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            requestId = extras.getLong(Keys.REQUEST_ID, 0);
        }
        getDetails();


    }

    @Override
    public void onClick(View v) {

    }

    private void getDetails() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().getInvoiceDetails(TokenStorage.getToken(context), requestId)
                    .enqueue(new BaseCallback<InvoiceDetailsResult>(context, true) {
                        @Override
                        protected void onResult(int code, InvoiceDetailsResult result) {
                            if (code == 200) {
                                detailsResult = result;
                                initViews();
                            }

                        }
                    });
        }
    }

    private void initViews() {
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        ((TextView) findViewById(R.id.tv_amount)).setText(Utils.pennyToUah(detailsResult.getAmount()));
        ((TextView) findViewById(R.id.tvPhone)).setText(detailsResult.getRequester().getPhone());
        ((TextView) findViewById(R.id.tvName)).setText(detailsResult.getRequester().getName());


        String initials = Utils.extractInitials(detailsResult.getRequester().getName(), "");
        Utils.displayAvatar(context, ivAvatar, detailsResult.getRequester().getPhoto(), initials);

        String cardNumber = "";
        if (detailsResult.getPan_masked() != null && detailsResult.getPan_masked().length() == 10)
            cardNumber = detailsResult.getPan_masked().substring(0, 4) + " " + detailsResult.getPan_masked().substring(4, 6) + "** **** " + detailsResult.getPan_masked().substring(6);
        ((TextView) findViewById(R.id.tv_card)).setText(cardNumber);


        ((TextView) findViewById(R.id.tv_status)).setText(getStatus(detailsResult.getStatus(), detailsResult.getStatusChangedAt()));

        if (detailsResult.getGoods() != null && detailsResult.getGoods().size() > 0) {
            rvGoods = findViewById(R.id.rv_goods);
            rvGoods.setLayoutManager(new LinearLayoutManager(context));
            rvGoods.setAdapter(new GoodsOutIncomAdapter(context, detailsResult.getGoods(), rvListener));
            findViewById(R.id.constraint_goods).setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(detailsResult.getComment())) {
            ((TextView) findViewById(R.id.tv_comment)).setText(detailsResult.getComment());
            findViewById(R.id.constraint_comment).setVisibility(View.VISIBLE);
        }


    }

    private GoodsOutIncomAdapter.OnItemClickListener rvListener = new GoodsOutIncomAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(long id) {
            Intent intent = new Intent(context, GoodsDetailOutInRequestActivity.class);
            intent.putExtra(GOODS_ID, id);
            startActivity(intent);
        }
    };

    private String getStatus(String st, String date) {
        String msg = "";
        switch (Status.valueOf(st)) {
            case ACTIVE:
                msg = getString(R.string.delivered);
                break;
            case VIEWED:
                msg = getString(R.string.read);
                break;
            case DELETED_BY_RECIPIENT:
                msg = getString(R.string.read);
                break;
            case REJECTED:
                msg = getString(R.string.rejected);
                break;
            case ACCEPTED:
                msg = getString(R.string.approved);
                break;
        }

        return getString(R.string.status, msg, Utils.checkDateIsToday(context, date));
    }

    private enum Status {
        ACTIVE,
        VIEWED,
        DELETED_BY_RECIPIENT,
        REJECTED,
        ACCEPTED;
    }
}
