package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.views.MoneyEditText;

public class CreatePayRequestActivity extends BaseActivity {

    private EditText etPhone, etCard, etComment;
    private MoneyEditText etAmount;

    private RecyclerView rvContacts;
    private View rlRequestAmount;

    private String receiverPhone, receiverName, receiverCard, receiverCardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pay_request);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(Keys.PHONE)) receiverPhone = extras.getString(Keys.PHONE);
        if (extras.containsKey(Keys.NAME)) receiverPhone = extras.getString(Keys.NAME);
        if (extras.containsKey(Keys.PHONE)) receiverPhone = extras.getString(Keys.PHONE);
        if (extras.containsKey(Keys.PHONE)) receiverPhone = extras.getString(Keys.PHONE);

        initCustomToolbar("запрос на оплату");
        initViews();
    }

    private void initViews() {

        findViewById(R.id.iv_choose_contact).setOnClickListener(this);
        findViewById(R.id.iv_choose_card).setOnClickListener(this);
        findViewById(R.id.tv_send_request).setOnClickListener(this);
        rvContacts = findViewById(R.id.rv_contacts);
        etPhone = findViewById(R.id.et_phone);
        etCard = findViewById(R.id.et_card_number);
        etComment = findViewById(R.id.et_comment);
        etAmount = findViewById(R.id.met_amount);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.ivBack:
                onBackPressed();
                break;
            case R.id.iv_choose_card:
                context.startActivity(new Intent(context, CardSelectActivity.class));
                break;
            case R.id.iv_choose_contact:
                context.startActivity(new Intent(context, SelectContactsActivity.class));
                break;
            case R.id.tv_send_request:

                break;
        }

    }
}
