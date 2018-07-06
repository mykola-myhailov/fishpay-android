package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.CreatePayRequestActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import static com.myhailov.mykola.fishpay.utils.Keys.LOAD_CONTACTS;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.OWNER;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.ID;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.USER_PREFS;
import static com.myhailov.mykola.fishpay.utils.Utils.showInfoAlert;

public class MembersPartActivity extends BaseActivity {

    private String title;
    private Member member;
    private boolean isOwner, isClosed, paid, isActive, isMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_part);

        String id = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(ID, "");
        title = getIntent().getStringExtra(TITLE);
        member = getIntent().getParcelableExtra(MEMBER);
        isOwner = getIntent().getBooleanExtra(OWNER, false);
        isClosed = member._getMemberStatus().equals("CLOSED");
        paid = member._getMemberStatus().equals("PAID");
        isActive = member.getType().equals("user");
        isMe = isActive && member.getUserId().equals(id);

        initCustomToolbar(title);
        initViews();
    }

    private void initViews() {
        ImageView ivAvatar = findViewById(R.id.iv_avatar);
        TextView tvInitials = findViewById(R.id.tv_initials);
        String initials = Utils.extractInitials(member.getFirstName(), member.getSecondName());
        if (member.getPhoto() != null && !member.getPhoto().equals("") && !member.getPhoto().equals("null")) {
            Uri photoUri = Uri.parse(ApiClient.BASE_API_URL + "api/resources/photo/" + member.getPhoto());
            Picasso.with(context).load(photoUri).into(ivAvatar);
            tvInitials.setText("");
        } else {
            ivAvatar.setImageDrawable(null);
            tvInitials.setText(initials);
        }
        ((TextView) findViewById(R.id.tv_name)).setText(member.getFullUserName());
        ((TextView) findViewById(R.id.tv_phone)).setText(member.getPhone());

        TextView tvStatus = findViewById(R.id.tv_status);
        TextView tvAmount = findViewById(R.id.tv_amount);
        TextView tvPaid = findViewById(R.id.tv_paid);

        tvStatus.setText(member.getMemberStatus());
        tvAmount.setText(Utils.pennyToUah(member.getAmountToPay()));
        if (member.getAmountPaid() != 0)
            tvPaid.setText(Utils.pennyToUah(member.getAmountPaid()));
        else tvPaid.setText("0.00");


        View llPay = findViewById(R.id.ll_pay);
        View tvPay = findViewById(R.id.tv_pay);
        View llRequest = findViewById(R.id.ll_request);
        View tvRequest = findViewById(R.id.tv_request);
        View divider = findViewById(R.id.divider);
        View llButtons = findViewById(R.id.ll_buttons);


        if (isOwner && !isClosed && !paid) {
            llButtons.setVisibility(View.VISIBLE);
            llPay.setVisibility(View.VISIBLE);
            tvPay.setOnClickListener(this);
            if (!isActive || isMe) {
                divider.setVisibility(View.GONE);
                llRequest.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
                llRequest.setVisibility(View.VISIBLE);
                tvRequest.setOnClickListener(this);
            }
        } else {
            llButtons.setVisibility(View.GONE);
        }

        if (isClosed) {
            findViewById(R.id.ll_closed).setVisibility(View.VISIBLE);
            tvStatus.setTextColor(getResources().getColor(R.color.grey2));
            tvAmount.setTextColor(getResources().getColor(R.color.grey2));
            tvPaid.setTextColor(getResources().getColor(R.color.grey2));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_pay:
                showInfoAlert(context);
                // TODO: 06.07.2018 в розробці
//                startPaymentMemberActivity();
                break;
            case R.id.tv_request:
                context.startActivity(new Intent(context, CreatePayRequestActivity.class)
                        .putExtra(LOAD_CONTACTS, false)
                        .putExtra(TITLE, title)
                        .putExtra(Keys.MEMBER, member)
                );
                break;
        }
    }

    private void startPaymentMemberActivity() {
        startActivityForResult(new Intent(context, PaymentMemberActivity.class)
                .putExtra(MEMBER, member)
                .putExtra(TITLE, title), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
