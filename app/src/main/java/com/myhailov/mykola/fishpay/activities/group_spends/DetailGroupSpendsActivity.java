package com.myhailov.mykola.fishpay.activities.group_spends;

import com.google.gson.Gson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.Utils;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND;

public class DetailGroupSpendsActivity extends BaseActivity {
    private TextView tvTitle, tvAmount, tvDescription, tvCardName, tvCardNumber;
    private ImageView ivCard;

    private SpendDetailResult spendDetail;
    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_group_spends);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            spendDetail = (SpendDetailResult) extras.getSerializable(SPEND);
        }
        if (!TextUtils.isEmpty(spendDetail.getTitle())) {
            initCustomToolbar(spendDetail.getTitle());
        } else initCustomToolbar("");

        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.iv_card:
                startActivityForResult(new Intent(context, CardsActivity.class)
                        .putExtra(REQUEST, true), REQUEST_CARD);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CARD && resultCode == RESULT_OK) {
            card = data.getParcelableExtra(CARD);
            if (card != null) {
                setCardValue();

            }
        }
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvAmount = findViewById(R.id.tv_amount);
        tvDescription = findViewById(R.id.tv_description);
        ivCard = findViewById(R.id.iv_card);
        tvCardName = findViewById(R.id.tv_card_name);
        tvCardNumber = findViewById(R.id.tv_card_number);

        tvTitle.setText(spendDetail.getTitle());
        tvAmount.setText(Utils.pennyToUah(spendDetail.getStartAmount()));
        tvDescription.setText(spendDetail.getDescription());

        ivCard.setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
        getCard();
    }

    private void setCardValue() {
        tvCardName.setText("| " + card.getName());
        tvCardNumber.setText(card.getLastFourNumbers());
    }

    private void getCard() {
        SharedPreferences sharedPreferences = getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
        if (sharedPreferences.contains(PrefKeys.CARD)) {
            String cardJson = sharedPreferences.getString(PrefKeys.CARD, null);
            Log.d("cardJson", cardJson);
            card = cardJson == null ? null : new Gson().fromJson(cardJson, Card.class);
            if (card != null) {
                setCardValue();
            }
        }
    }
}
