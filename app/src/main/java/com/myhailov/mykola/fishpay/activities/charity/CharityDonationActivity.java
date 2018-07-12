package com.myhailov.mykola.fishpay.activities.charity;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.BankWebActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.CharityResultById;
import com.myhailov.mykola.fishpay.utils.Keys;
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

    private String fpt, fptId, anon;
    private Card card;
    private CharityResultById charity = new CharityResultById();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_donation);
        initCustomToolbar(getString(R.string.charity_donation));

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
            anon = (!anonymousSwitch.isChecked()) + "";
            ApiClient.getApiInterface().attemptPayCharity(
                    TokenStorage.getToken(context),
                    Utils.makeRequestBody(Utils.UAHtoPenny(etTotal.getText().toString()) + ""),
                    Utils.makeRequestBody("true"),
                    Utils.makeRequestBody(anon),
                    charity.getId() + "",
                    Utils.makeRequestBody(card.getId()),
                    Utils.makeRequestBody(etCvv.getText().toString()))
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        public void onFailure(@NonNull Call<BaseResponse<Object>> call, @NonNull Throwable t) {
                            super.onFailure(call, t);
                        }

                        @Override
                        protected void onResult(int code, Object result) {
                            switch (result.toString()) {

                                case "SUCCESS":
                                    Utils.toast(context, "success");
                                    break;
                                case "REJECTED":
                                    showErrorAlert();
                                    Utils.toast(context, getString(R.string.rejected));
                                    break;
                                case "REVERSED":
                                    Utils.toast(context, "reversed");
                                    break;
                                case "ERROR":
                                    showErrorAlert();
                                    Utils.toast(context, "error");
                                    break;
                                default:
                                    LinkedTreeMap resultMap = (LinkedTreeMap) result;
                                    parseResultMap(resultMap);
                                    break;
                            }
                        }
                    });
        }
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
            toast(getString(R.string.input_amount));
            return false;
        }
        if (card == null) {
            toast(getString(R.string.choose_card));
            return false;
        }
        if (TextUtils.isEmpty(etCvv.getText().toString()) || etCvv.getText().length() != 3) {
            toast(getString(R.string.enter_cvv_code));
            return false;
        }

        return true;
    }

    private void parseResultMap(LinkedTreeMap result) {
        String type = (String) result.get("type");
        if (type.equals("3DS")) {
            fpt = (String) result.get("fpt");
            fptId = (String) result.get("id");
            String url = (String) result.get("url");
            LinkedTreeMap form_params = (LinkedTreeMap) result.get("form_params");
            String termUrl = (String) form_params.get("TermUrl");
            String paReq = (String) form_params.get("PaReq");

            context.startActivity(new Intent(context, BankWebActivity.class)
                    .putExtra(Keys.TYPE, type)
                    .putExtra(Keys.URL, url)
                    .putExtra(Keys.TERM_URL, termUrl)
                    .putExtra(Keys.PA_REQ, paReq)
                    .putExtra(Keys.FPT, fpt)
                    .putExtra(Keys.FPT_ID, fptId)

                    .putExtra(Keys.CHARITY_ID, charity.getId() + "")
                    .putExtra(Keys.CHARITY_ANON, anon)
            );
        } else if (type.equals("lookup")) {
            fpt = (String) result.get("fpt");
            fptId = (String) result.get("id");
            requestLookup();
        }
    }

    private void requestLookup() {
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(context)
                .setMessage(getString(R.string.enter_sms_code))
                .setView(input)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String code = input.getText().toString();

                        if (code.equals(""))
                            Utils.toast(context, getString(R.string.enter_sms_code));
                        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                        else ApiClient.getApiInterface()
                                    .sendLookupCharity(TokenStorage.getToken(context), "true", anon,
                                            charity.getId() + "", fpt, fptId, code)
                                    .enqueue(new BaseCallback<String>(context, true) {
                                        @Override
                                        protected void onResult(int code, String result) {
                                            switch (result.toLowerCase()) {
                                                case "error_code":
                                                    Utils.toast(context, getString(R.string.incorrect_code));
                                                    requestLookup();
                                                    break;
                                                case "success":
                                                    Utils.toast(context, getString(R.string.successfully));
                                                    break;
                                                default:
                                                    Utils.toast(context, getString(R.string.error));
                                                    break;
                                            }
                                        }
                                    });
                    }
                })
                .create().show();
    }

    private void showErrorAlert() {
        final AlertDialog infoAlert;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_error, null);
        dialogBuilder.setView(dialogView);

        infoAlert = dialogBuilder.create();
        infoAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoAlert.show();

        dialogView.findViewById(R.id.tv_action_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoAlert.cancel();
            }
        });
    }
}
