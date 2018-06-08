package com.myhailov.mykola.fishpay.activities.charity;

import com.google.gson.Gson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.CharityActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.CharityResultById;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import retrofit2.Call;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_RESULT;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;

public class CharityDonationActivity extends BaseActivity {
    private TextView tvTitle, tvAuthor, tvCardNumber, tvCardName;
    private Switch anonymousSwitch;
    private ImageView ivCard;
    private EditText etTotal, etCvv;

    private Card card;
    private CharityResultById charity = new CharityResultById();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_donation);
        initCustomToolbar("Взаимопомощь взнос");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            charity = (CharityResultById) extras.getSerializable(CHARITY_RESULT);
        }
        initViews();
        setValue();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_transfer:
                if (checkValue()) {
                    attemptPayCharity();
                }
                break;
            case R.id.iv_card:
                startActivityForResult(new Intent(context, CardsActivity.class)
                        .putExtra(REQUEST, true), REQUEST_CARD);
                break;
            case R.id.ivBack:
                onBackPressed();
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

            } else {

            }
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvAuthor = findViewById(R.id.tv_author);
        tvCardNumber = findViewById(R.id.tv_card_number);
        tvCardName = findViewById(R.id.tv_card_name);
        ivCard = findViewById(R.id.iv_card);
        etTotal = findViewById(R.id.et_total);
        anonymousSwitch = findViewById(R.id.switch_indefinitely);
        etCvv = findViewById(R.id.et_cvv);

        ivCard.setOnClickListener(this);
        findViewById(R.id.tv_to_transfer).setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private void setValue() {
        if (charity == null) return;
        tvTitle.setText(charity.getTitle());
        if (TextUtils.isEmpty(charity.getName()) || charity.getName().equals("null")) {
            tvAuthor.setText(charity.getAuthorName());
        } else {
            tvAuthor.setText(charity.getName());
        }
        getCard();
    }

    private void attemptPayCharity() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().attemptPayCharity(
                    TokenStorage.getToken(context),
                    Utils.makeRequestBody(Utils.UAHtoPenny(etTotal.getText().toString()) + ""),
                    Utils.makeRequestBody("true"),
                    Utils.makeRequestBody((!anonymousSwitch.isChecked()) + ""),
                    charity.getId() + "",
                    Utils.makeRequestBody(card.getId()),
                    Utils.makeRequestBody(etCvv.getText().toString()))
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        public void onFailure(@NonNull Call<BaseResponse<Object>> call, @NonNull Throwable t) {
                            super.onFailure(call, t);
                            Log.d("sss", "onFailure: " + t);
                        }

                        @Override
                        protected void onResult(int code, Object result) {
                            startActivity(new Intent(context, CharityActivity.class));
                            Log.d("sss", "onResult: code " + code);
                            Log.d("sss", "onResult: result " + result);

                        }
                    });
        }
    }

    private void getCard() {
        SharedPreferences sharedPreferences = getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
        if (sharedPreferences.contains(PrefKeys.CARD)) {
            String cardJson = sharedPreferences.getString(PrefKeys.CARD, null);
            Log.e("cardJson", cardJson);
            card = cardJson == null ? null : new Gson().fromJson(cardJson, Card.class);
            if (card != null) {
                setCardValue();
            }
        }
    }

    private void setCardValue() {
        tvCardName.setText("| " + card.getName());
        SpannableString spanCardNumber = new SpannableString(card.getCardNumber());
        spanCardNumber.setSpan(new UnderlineSpan(), 0, 4, 0);
        spanCardNumber.setSpan(new UnderlineSpan(), 5, 9, 0);
        spanCardNumber.setSpan(new UnderlineSpan(), 10, 14, 0);
        spanCardNumber.setSpan(new UnderlineSpan(), 15, 19, 0);
        tvCardNumber.setText(spanCardNumber);
    }

    private boolean checkValue() {
        if (TextUtils.isEmpty(etTotal.getText().toString()) || etTotal.getText().toString().equals("0")) {
            toast("Введите суму");
            return false;
        }
        if (card == null) {
            toast("Выберите карту");
            return false;
        }
        if (TextUtils.isEmpty(etCvv.getText().toString()) || etCvv.getText().length() != 3) {
            toast("Введите CVV");
            return false;
        }

        return true;
    }
}
