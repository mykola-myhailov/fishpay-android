package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.OWNER;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;
import static com.myhailov.mykola.fishpay.utils.Keys.USER_ID;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.ID;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.USER_PREFS;

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
        isClosed = member.getMemberStatus().equals("CLOSED");
        paid = member.getMemberStatus().equals("PAID");
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
        ((TextView) findViewById(R.id.tv_name)).setText(member.getFullName());
        ((TextView) findViewById(R.id.tv_phone)).setText(member.getPhone());

        TextView tvStatus = findViewById(R.id.tv_status);
        TextView tvAmount = findViewById(R.id.tv_amount);
        TextView tvPaid = findViewById(R.id.tv_paid);

        tvStatus.setText(member.getMemberStatus());
        tvAmount.setText(Utils.pennyToUah(Float.valueOf(member.getAmountToPay())));
        if (member.getAmountPaid() != null && !member.getAmountPaid().equals("0"))
            tvPaid.setText(Utils.pennyToUah(Float.valueOf(member.getAmountPaid())));
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
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_pay:
                startPaymentMemberActivity();
                break;
            case R.id.tv_request:

                break;
        }
    }

    private void startPaymentMemberActivity() {
        startActivity(new Intent(context, PaymentMemberActivity.class)
                .putExtra(MEMBER, member)
                .putExtra(TITLE, title));
    }

}
