package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.PayRequestActivity;
import com.myhailov.mykola.fishpay.activities.TransactionActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.InvoiceDetailsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class IncomingDetailsActivity extends BaseActivity {

    private AlertDialog alertDialog;
    private TextView tvBlockUser, tvSendComplaint, tvClose;

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

        if (Utils.isOnline(context)) {

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
                                if (requester != null) {
                                    requesterName = requester.getName();
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvAccept:
                accept();
                break;
            case R.id.tvReject:
                rejectRequest();
                break;
            case R.id.tv_block_user:
                alertDialog.cancel();
                break;
            case R.id.tv_send_complaint:
                alertDialog.cancel();
                break;
            case R.id.tv_close:
                alertDialog.cancel();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                showAlert();
                break;
        }
        return true;
    }

    private void showAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.info_request_for_payment, null);
        dialogBuilder.setView(dialogView);
        tvBlockUser = dialogView.findViewById(R.id.tv_block_user);
        tvSendComplaint = dialogView.findViewById(R.id.tv_send_complaint);
        tvClose = dialogView.findViewById(R.id.tv_close);

        tvBlockUser.setOnClickListener(this);
        tvSendComplaint.setOnClickListener(this);
        tvClose.setOnClickListener(this);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }


    private void initViews() {

        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        String initials = Utils.extractInitials(requesterName, "");
        Utils.displayAvatar(context, ivAvatar, requesterPhoto, initials);
        ((TextView) findViewById(R.id.tvName)).setText(requesterName);

        ((TextView) findViewById(R.id.tvAmount)).setText(amount);
        ((TextView) findViewById(R.id.tvPhone)).setText(requesterPhone);

        String cardNumber = "";
        if (panMasked != null && panMasked.length() == 10)
            cardNumber = panMasked.substring(0, 4) + " " + panMasked.substring(4, 6) + "** **** " + panMasked.substring(6);
        ((TextView) findViewById(R.id.tvCard)).setText(cardNumber);


        ((TextView) findViewById(R.id.tvAmount)).setText(amount);
        ((TextView) findViewById(R.id.tvComment)).setText(comment);



        findViewById(R.id.tvAccept).setOnClickListener(this);
        findViewById(R.id.tvReject).setOnClickListener(this);

    }




    private void accept() {
        context.startActivity(new Intent(context, TransactionActivity.class)
                .putExtra(Keys.AMOUNT, amount)
                .putExtra(Keys.NAME, requesterName)
                .putExtra(Keys.USER_ID, requesterId)
        );
    }

    private void rejectRequest() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().rejectInvoice(TokenStorage.getToken(context), invoiceId)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            Log.d("sss", "onResult: " + code);
                            if (code == 202) {
                                context.startActivity(new Intent(context, PayRequestActivity.class));
                            }
                        }
                    });
        } else Utils.noInternetToast(context);
    }
}
