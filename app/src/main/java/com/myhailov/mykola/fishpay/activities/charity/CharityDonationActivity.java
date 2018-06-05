package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
    private ImageView ivCard, ivInfo;
    private EditText etTotal;

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
                if (TextUtils.isEmpty(etTotal.getText().toString())) {
                    toast("Введите суму");
                    break;
                }
                if (card == null) {
                    toast("Выберите карту");
                    break;
                }
                    attemptPayCharity();

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
                tvCardName.setText("| " + card.getName());
                tvCardNumber.setText(card.getLastFourNumbers());
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
        ivInfo = findViewById(R.id.iv_info);
        etTotal = findViewById(R.id.et_total);
        anonymousSwitch = findViewById(R.id.switch_indefinitely);

        ivCard.setOnClickListener(this);
        ivInfo.setOnClickListener(this);
        findViewById(R.id.tv_to_transfer).setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private void setValue() {
        if (charity == null) return;
        tvTitle.setText(charity.getTitle());
        tvAuthor.setText(charity.getAuthorName());
    }

    private void attemptPayCharity() {
        // TODO: 05.06.2018 fix request (CVV)
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().attemptPayCharity(
                    TokenStorage.getToken(context),
                    Utils.makeRequestBody(Utils.UAHtoPenny(etTotal.getText().toString()) + ""),
                    Utils.makeRequestBody("true"),
                    Utils.makeRequestBody((!anonymousSwitch.isChecked()) + ""),
                    charity.getId() + "",
                    Utils.makeRequestBody(card.getId()),
                    Utils.makeRequestBody("123"))
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
}
