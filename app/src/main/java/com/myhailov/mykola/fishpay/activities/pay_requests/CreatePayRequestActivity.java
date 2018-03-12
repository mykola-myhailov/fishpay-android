package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.views.MoneyEditText;

public class CreatePayRequestActivity extends BaseActivity {

    private EditText etPhone;
    private EditText etCard;
    private View ivChooseContact;
    private View ivChooseCard;
    private RecyclerView rvContacts;
    private View rlRequestAmount;
    private EditText etComment;
    private TextView tvProduct;
    private MoneyEditText etAmount;
    private View tvSendRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pay_request);

        initCustomToolbar("запрос на оплату");
        assignViews();
    }

    private void assignViews() {
        etPhone = findViewById(R.id.et_phone);
        etCard = findViewById(R.id.et_card_number);
        ivChooseContact = findViewById(R.id.iv_choose_contact);
        ivChooseCard = findViewById(R.id.iv_choose_card);
        rvContacts = findViewById(R.id.rv_contacts);
        rlRequestAmount = findViewById(R.id.rl_request_amount);
        etComment = findViewById(R.id.et_comment);
        tvProduct = findViewById(R.id.tv_product);
        tvProduct = findViewById(R.id.tv_product);
        etAmount = findViewById(R.id.met_amount);
        tvSendRequest = findViewById(R.id.tv_send_request);
    }
}
