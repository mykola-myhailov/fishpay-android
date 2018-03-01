package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.OWNER;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;

public class MembersPartActivity extends BaseActivity {

    private Member member;
    private boolean isOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_part);
        String title = getIntent().getStringExtra(TITLE);
        member = getIntent().getParcelableExtra(MEMBER);
        isOwner = getIntent().getBooleanExtra(OWNER, false);
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
        ((TextView) findViewById(R.id.tv_status)).setText(member.getMemberStatus());
        ((TextView) findViewById(R.id.tv_amount)).setText(Utils.pennyToUah(Float.valueOf(member.getAmountToPay())));
        if (member.getAmountPaid() != null && !member.getAmountPaid().equals("0"))
            ((TextView) findViewById(R.id.tv_paid)).setText(Utils.pennyToUah(Float.valueOf(member.getAmountPaid())));
        else ((TextView) findViewById(R.id.tv_paid)).setText("0.00");

        if (isOwner) {
            findViewById(R.id.ll_pay).setOnClickListener(this);
            findViewById(R.id.ll_request).setOnClickListener(this);
        } else {
            findViewById(R.id.ll_pay).setVisibility(View.GONE);
            findViewById(R.id.ll_request).setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {

        }
    }

}
