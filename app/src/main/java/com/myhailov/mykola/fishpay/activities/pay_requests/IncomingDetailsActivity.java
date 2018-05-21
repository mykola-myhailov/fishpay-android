package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.PayRequestActivity;
import com.myhailov.mykola.fishpay.activities.drawer.TransactionActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.InvoiceDetailsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class IncomingDetailsActivity extends BaseActivity {


    private String invoiceId, panMasked, amount, comment,
            requesterPhone, requesterName, requesterPhoto, status;
    private String requesterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_details);
        initToolBar("Запрос на оплату", R.color.black2);

        Bundle extras = getIntent().getExtras();
        long requestId = extras.getLong(Keys.REQUEST_ID);

        if (Utils.isOnline(context)){

            ApiClient.getApiClient().getInvoiceDetails(TokenStorage.getToken(context), requestId)
                    .enqueue(new BaseCallback<InvoiceDetailsResult>(context, true) {
                        @Override
                        protected void onResult(int code, InvoiceDetailsResult result) {
                            if (code == 200) {
                                invoiceId = result.getId();
                                panMasked = result.getPan_masked();
                                amount = Utils.pennyToUah(result.getAmount());
                                comment = result.getComment();
                                status = result.getStatus();
                                InvoiceDetailsResult.Contact requester = result.getRequester();
                                if (requester != null){
                                    requesterName =  requester.getName();
                                    requesterPhoto = requester.getPhoto();
                                    requesterPhone = requester.getPhone();
                                    requesterId = requester.getId();
                                }
                                initViews();
                            }

                        }
                    });
        }

        initViews();
    }

    private void initViews() {

        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        String initials = Utils.extractInitials(requesterName, "");
        Utils.displayAvatar(context, ivAvatar, requesterPhoto, initials);
        ((TextView) findViewById(R.id.tvName)).setText(requesterName);

        ((TextView) findViewById(R.id.tvAmount)).setText(amount);
        ((TextView) findViewById(R.id.tvPhone)).setText(requesterPhone);

        String cardNumber = "";
        if(panMasked != null && panMasked.length() == 10)
        cardNumber = panMasked.substring(0,4) + " "+  panMasked.substring(4, 6)+ "** **** " + panMasked.substring(6);
        ((TextView) findViewById(R.id.tvCard)).setText(cardNumber);


        ((TextView) findViewById(R.id.tvAmount)).setText(amount);
        ((TextView) findViewById(R.id.tvComment)).setText(comment);

            findViewById(R.id.tvAccept).setOnClickListener(this);
            findViewById(R.id.tvReject).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.tvAccept:
                accept();
                break;
            case R.id.tvReject:
                rejectRequest();
                break;
        }
    }

    private void accept() {
        context.startActivity(new Intent(context, TransactionActivity.class)
                .putExtra(Keys.AMOUNT, amount)
                .putExtra(Keys.NAME, requesterName)
                .putExtra(Keys.USER_ID,  requesterId)
        );
    }

    private void rejectRequest() {
        if (Utils.isOnline(context)){
            ApiClient.getApiClient().rejectInvoice(TokenStorage.getToken(context), invoiceId)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 202) {
                                context.startActivity(new Intent(context, PayRequestActivity.class));
                            }
                        }
                    });
        }  else Utils.noInternetToast(context);
    }
}
