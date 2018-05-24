package com.myhailov.mykola.fishpay.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.BankWebActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.SelectContactsActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import static com.myhailov.mykola.fishpay.activities.pay_requests.SelectContactsActivity.REQUEST_CONTACT;
import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;

public class TransactionActivity extends DrawerActivity {

    private TextView tvCard, tvName;
    private ImageView ivChoseContact;
    private EditText etComment, etAmount, etCvv;
    private Card card;
    private String receiverPhone = "", receiverName = "", receiverId = null,
            cardNumber = "", cardName = "";

    private Contact receiverContact;
    private int amount;
    private String fpt, fptId;
    private String amountUAH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_transaction);

        createDrawer();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            if (extras.containsKey(Keys.NAME)) receiverName = extras.getString(Keys.NAME);
            if (extras.containsKey(Keys.USER_ID)) receiverId = extras.getString(Keys.NAME);
            if (extras.containsKey(Keys.AMOUNT)) amountUAH = (extras.getString(Keys.AMOUNT));
        }
        initDrawerToolbar(getString(R.string.outgoing_transaction));
        initViews();
    }

    private void initViews() {
        findViewById(R.id.rl_choose_contact).setOnClickListener(this);
        findViewById(R.id.tv_send_request).setOnClickListener(this);
        findViewById(R.id.rl_card).setOnClickListener(this);
        tvName = findViewById(R.id.tv_name);
        ivChoseContact = findViewById(R.id.iv_choose_contact);
        tvCard = findViewById(R.id.tv_card_number);
        etComment = findViewById(R.id.et_comment);
        etAmount = findViewById(R.id.et_sum);
        etCvv = findViewById(R.id.et_cvv);
        if (receiverName != null && !receiverName.equals("")) tvName.setText(receiverName);
        etAmount.setText(amountUAH);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_card:
                startActivityForResult(new Intent(context, CardsActivity.class)
                        .putExtra(REQUEST, true), REQUEST_CARD);
                break;
            case R.id.rl_choose_contact:
            case R.id.iv_choose_contact:
                startActivityForResult(new Intent(context, SelectContactsActivity.class)
                        .putExtra(REQUEST, true), REQUEST_CONTACT);
                break;
            case R.id.tv_send_request:
                // preparing


                amountUAH = etAmount.getText().toString();
                amount = Utils.UAHtoPenny(amountUAH);
             //   String comment = etComment.getText().toString();
                final String cvv = etCvv.getText().toString();
                if (receiverContact != null) receiverContact.getUserId();

                //validation
                if (card == null) Utils.toast(context, getString(R.string.enter_card));
                else if (receiverId == null) Utils.toast(context, "выберите контакт");
                else if (amount == 0) Utils.toast(context, "Введите сумму");
                else if (cvv.equals("")) Utils.toast(context,"Введите CVV");
                else if (Utils.isOnline(context)){
                   // String cardId = card.getId();
                  //  String memberId = null;
                  //  if (member != null) memberId = member.getId();
                    ApiClient.getApiClient().transfer(
                            token,
                            receiverId + "",
                            card.getId(),
                            cvv,
                            amount)
                            .enqueue(new BaseCallback<Object>(context, true) {
                                @Override
                                protected void onResult(int code, Object result) {

                                    switch (result.toString()) {

                                        case "SUCCESS":
                                            Utils.toast(context, "success");
                                            break;
                                        case "REJECTED":
                                            Utils.toast(context, "rejected");
                                            break;
                                        case "REVERSED":
                                            Utils.toast(context, "reversed");
                                            break;
                                        case "ERROR":
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
                else Utils.noInternetToast(context);
                break;
        }
    }

    private void parseResultMap(LinkedTreeMap result) {
        String type = (String) result.get("type");
        if (type.equals("3DS")){
            fpt = (String) result.get("fpt");
            fptId = (String) result.get("id");
            String url = (String) result.get("url");
            LinkedTreeMap form_params = (LinkedTreeMap) result.get("form_params");
            String termUrl = (String) form_params.get("TermUrl");
            String paReq = (String) form_params.get("PaReq");

            context.startActivity(new Intent(context, BankWebActivity.class)
                    .putExtra(Keys.URL, url)
                    .putExtra(Keys.TERM_URL, termUrl)
                    .putExtra(Keys.PA_REQ, paReq)
                    .putExtra(Keys.FPT, fpt)
                    .putExtra(Keys.FPT_ID, fptId));
        } else if (type.equals("lookup")){
            fpt = (String) result.get("fpt");
            fptId  = (String) result.get("id");
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
                    .setMessage("Введите SMS-код")
                    .setView(input)
                    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String code = input.getText().toString();

                            if (code.equals("")) Utils.toast(context, getString(R.string.enter_sms_code));
                            else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                            else ApiClient.getApiClient()
                                        .sendLookup(TokenStorage.getToken(context),fpt, fptId, code)
                                        .enqueue(new BaseCallback<String>(context, true) {
                                            @Override
                                            protected void onResult(int code, String result) {
                                                switch (result.toLowerCase()){
                                                    case "error_code":
                                                        Utils.toast(context, "Неверный код");
                                                        requestLookup();
                                                        break;
                                                    case "success":
                                                        Utils.toast(context, "Успешно");
                                                        break;
                                                    default:
                                                        Utils.toast(context, "Ошибка");
                                                        break;
                                                }
                                            }
                                        });
                        }
                    })
                    .create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_CARD){
            card = data.getParcelableExtra(CARD);
            if (card != null) {
                cardNumber = card.getLastFourNumbers();
                cardName = card.getName();
                if (cardName.equals("")) tvCard.setText(cardNumber);
                else tvCard.setText(String.format("%s | %s", cardName, cardNumber));
                Log.d("card", cardNumber);
            }
        }

        else if (requestCode == REQUEST_CONTACT){
            receiverContact = data.getParcelableExtra(CONTACT);
            if (receiverContact != null) {
                receiverPhone = receiverContact.getPhone();
                receiverName = receiverContact.getName();
                if (receiverName != null) tvName.setText(receiverName);
            }
        }
    }
}