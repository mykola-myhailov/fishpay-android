package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.myhailov.mykola.fishpay.adapters.GoodsOutIncomAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.InvoiceDetailsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_ID;

public class IncomingDetailsActivity extends BaseActivity {

    private final String REJECTED = "REJECTED", ACCEPTED = "ACCEPTED", PAID = "PAID";

    private AlertDialog alertDialog, alertBlockUser;


    private String invoiceId, panMasked, amount, comment,
            requesterPhone, requesterName, requesterPhoto, status, createAt;
    private String requesterId;

    private RecyclerView rvGoods;
    private List<InvoiceDetailsResult.GoodsRequest> goods = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_details);
        initToolBar(getString(R.string.pay_request), R.color.black2);


        Bundle extras = getIntent().getExtras();
        long requestId = extras.getLong(Keys.REQUEST_ID);

        if (Utils.isOnline(context)) {

            ApiClient.getApiInterface().getInvoiceDetails(TokenStorage.getToken(context), requestId)
                    .enqueue(new BaseCallback<InvoiceDetailsResult>(context, true) {
                        @Override
                        protected void onResult(int code, InvoiceDetailsResult result) {
                            if (code == 200) {
                                invoiceId = result.getId();
                                panMasked = result.getPan_masked();
                                amount = Utils.pennyToUah(result.getAmount());
                                comment = result.getComment();
                                status = result.getStatus();
                                if (status.equals(REJECTED) || status.equals(ACCEPTED)  || status.equals(PAID)) {
                                    findViewById(R.id.linear2).setVisibility(View.INVISIBLE);
                                }
                                createAt = result.getCreatedAt();
                                InvoiceDetailsResult.Contact requester = result.getRequester();
                                if (requester != null) {
                                    requesterName = requester.getName();
                                    requesterPhoto = requester.getPhoto();
                                    requesterPhone = requester.getPhone();
                                    requesterId = requester.getId();
                                }
                                if (result.getGoods() != null) {
                                    goods = result.getGoods();
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
                showBlockUserAlert();
                break;
            case R.id.tv_first_action:
                blockUser();
                break;
            case R.id.tv_second_action:
                alertBlockUser.cancel();
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
        TextView tvBlockUser, tvSendComplaint, tvClose, tvDescription;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.info_request_for_payment, null);
        dialogBuilder.setView(dialogView);
        tvBlockUser = dialogView.findViewById(R.id.tv_block_user);
        tvSendComplaint = dialogView.findViewById(R.id.tv_send_complaint);
        tvClose = dialogView.findViewById(R.id.tv_close);
        tvDescription = dialogView.findViewById(R.id.tv_description);

        tvDescription.setText(getString(R.string.send_across_fishpay,
                Utils.convertStringToDateWithCustomFormat(context, createAt, "d MMMM"),
                Utils.convertStringToDateWithCustomFormat(context, createAt, "H:mm")));

        tvBlockUser.setOnClickListener(this);
        tvSendComplaint.setOnClickListener(this);
        tvClose.setOnClickListener(this);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void showBlockUserAlert() {
        TextView tvBlockUser, tvClose, tvDescription;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_with_two_action, null);
        dialogBuilder.setView(dialogView);
        tvBlockUser = dialogView.findViewById(R.id.tv_first_action);
        tvClose = dialogView.findViewById(R.id.tv_second_action);
        tvDescription = dialogView.findViewById(R.id.tv_title);
        dialogView.findViewById(R.id.tv_description).setVisibility(View.GONE);

        tvClose.setText(getString(R.string.close));
        tvBlockUser.setText(getString(R.string.block));
        tvDescription.setText(getString(R.string.alert_block_user, requesterName));
        tvBlockUser.setOnClickListener(this);
        tvClose.setOnClickListener(this);

        alertBlockUser = dialogBuilder.create();
        alertBlockUser.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertBlockUser.show();
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

        if (goods != null && goods.size() > 0) {
            findViewById(R.id.constr_goods).setVisibility(View.VISIBLE);
            findViewById(R.id.linear_coment).setVisibility(View.GONE);
            rvGoods = findViewById(R.id.rv_goods);
            rvGoods.setLayoutManager(new LinearLayoutManager(context));
            rvGoods.setAdapter(new GoodsOutIncomAdapter(context, goods, rvListener));
        }

        findViewById(R.id.tvAccept).setOnClickListener(this);
        findViewById(R.id.tvReject).setOnClickListener(this);

    }

    private GoodsOutIncomAdapter.OnItemClickListener rvListener = new GoodsOutIncomAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(long id) {
            Intent intent = new Intent(context, GoodsDetailOutInRequestActivity.class);
            intent.putExtra(GOODS_ID, id);
            startActivity(intent);
        }
    };

    private void blockUser() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().blockUserById(TokenStorage.getToken(context),
                    "BLOCKED", requesterId)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            alertBlockUser.cancel();
                            toast(getString(R.string.user_is_block));
                        }
                    });
        }
    }


    private void accept() {
        context.startActivity(new Intent(context, TransactionActivity.class)
                .putExtra(Keys.TYPE, TransactionActivity.INCOMING_PAY_REQUEST)
                .putExtra(Keys.AMOUNT, amount)
                .putExtra(Keys.NAME, requesterName)
                .putExtra(Keys.USER_ID, requesterId)
                .putExtra(Keys.REQUEST_ID, invoiceId)
        );
    }

    private void rejectRequest() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().rejectInvoice(TokenStorage.getToken(context), invoiceId)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 202) {
                                context.startActivity(new Intent(context, PayRequestActivity.class));
                            }
                        }
                    });
        } else Utils.noInternetToast(context);
    }
}
