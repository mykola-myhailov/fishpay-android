package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.InvoiceDetailsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class IncomingDetailsActivity extends BaseActivity {


    private String invoiceId, panMasked, amount, comment,
            requesterPhone, requesterName, requesterPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_details);
        initToolBar("Запрос на оплату", R.color.black2);

        Bundle extras = getIntent().getExtras();
        String requsetId = extras.getString(Keys.REQUEST_ID);

        if (Utils.isOnline(context)){

            ApiClient.getApiClient().getInvoiceDetails(TokenStorage.getToken(context), requsetId)
                    .enqueue(new BaseCallback<InvoiceDetailsResult>(context, true) {
                        @Override
                        protected void onResult(int code, InvoiceDetailsResult result) {
                            result.getPan_masked();
                            result.getId();
                            result.getAmount();
                            result.getComment();
                            InvoiceDetailsResult.Requester requester = result.getRequester();
                            if (requester != null){
                                requester.getName();
                                requester.getPhoto();
                                requester.getPhone();
                            }
                        }
                    });
        }

        initViews();
    }

    private void initViews() {
        findViewById(R.id.tvAccept).setOnClickListener(this);
        findViewById(R.id.tvReject).setOnClickListener(this);
        ImageView ivAvatar = findViewById(R.id.ivAvatar);//  Utils.displayAvatar(context, )
        //  findViewById(R.id.);
    }


    @Override
    public void onClick(View view) {

    }
}
