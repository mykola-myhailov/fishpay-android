package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.InvoiceDetailsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class OutComingPayRequestActivity extends BaseActivity {
    private InvoiceDetailsResult detailsResult;
    private long requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_coming_pay_request);
        initToolBar(getString(R.string.out_coming_pay_request), R.color.black2);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            requestId = extras.getLong(Keys.REQUEST_ID, 0);
            Log.d("sss", "onCreate: " + requestId);
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

        if (!TextUtils.isEmpty(detailsResult.getComment())) {
            ((TextView) findViewById(R.id.tv_comment)).setText(detailsResult.getComment());
            findViewById(R.id.constraint_comment).setVisibility(View.VISIBLE);
        }


    }

    private String getStatus(String st, String date) {
        String msg = "";
        switch (Status.valueOf(st)) {
            case ACTIVE:
                msg = "Доставлено";
                break;
            case VIEWED:
                msg =  "Прочитано";
                break;
            case DELETED_BY_RECIPIENT:
                msg =  "Прочитано";
                break;
            case REJECTED:
                msg =  "Отклонено";
                break;
            case ACCEPTED:
                msg =  "Одобрено";
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
