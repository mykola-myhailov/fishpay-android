package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.PayRequestActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.CreateInvoiceBody;
import com.myhailov.mykola.fishpay.api.requestBodies.Goods;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.MoneyEditText;

import java.util.ArrayList;

import static com.myhailov.mykola.fishpay.activities.pay_requests.SelectContactsActivity.REQUEST_CONTACT;
import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;


public class CreatePayRequestActivity extends BaseActivity {


    private EditText etPhone, etComment, etGoods;
    private MoneyEditText etAmount;
    private TextView tvCard;

    private RecyclerView rvContacts;

    private String receiverPhone = "", receiverName = "",
            receiverCardNumber = "", receiverCardName = "";
    private Contact receiverContact;
    private Card receiverCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pay_request);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            if (extras.containsKey(Keys.CONTACT)){
                receiverContact = extras.getParcelable(Keys.CONTACT);
                if (receiverContact != null){
                    receiverPhone = receiverContact.getPhone();
                    receiverName = receiverContact.getFullName() + receiverContact.getName() + receiverContact.getSurname();
                }
            }
            if (extras.containsKey(Keys.CARD)){
                receiverCard = extras.getParcelable(Keys.CARD);
                if (receiverCard != null){
                    receiverCardNumber = receiverCard.getLastFourNumbers();
                    receiverCardName = receiverCard.getName();
                }
            }
        }
        initCustomToolbar("запрос на оплату");
        initViews();
    }

    private void initViews() {
        findViewById(R.id.iv_choose_contact).setOnClickListener(this);
        findViewById(R.id.tv_send_request).setOnClickListener(this);
        findViewById(R.id.rl_card).setOnClickListener(this);
        rvContacts = findViewById(R.id.rv_contacts);
        etPhone = findViewById(R.id.et_phone);
        tvCard = findViewById(R.id.tv_card_number);
        etComment = findViewById(R.id.et_comment);
        etAmount = findViewById(R.id.met_amount);
        etGoods = findViewById(R.id.et_goods);
        if (receiverName.equals(""))etPhone.setText(receiverPhone);
        else etPhone.setText(String.format("%s | %s", receiverPhone, receiverName));
        if (receiverCardName.equals("")) tvCard.setText(receiverCardNumber);
        else tvCard.setText(String.format("%s | %s", receiverCard, receiverCardName));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.ivBack:
                onBackPressed();
                break;
            case R.id.rl_card:
                startActivityForResult(new Intent(context, CardsActivity.class)
                        .putExtra(REQUEST, true), REQUEST_CARD);
                break;
            case R.id.iv_choose_contact:
                startActivityForResult(new Intent(context, SelectContactsActivity.class)
                .putExtra(REQUEST, true), REQUEST_CONTACT);
                break;
            case R.id.tv_send_request:
                // preparing
                receiverPhone = etPhone.getText().toString();
                if (receiverPhone.substring(0, 1).equals("+")) receiverPhone = receiverPhone.substring(1);
                String amount = etAmount.getText().toString();
                String comment = etComment.getText().toString();

                Goods a = new Goods(123, 30);
                Goods b = new Goods(324, 2);
                ArrayList<Goods> goods = new ArrayList<>();
                goods.add(a);
                goods.add(b);

                String cardFullNumber = receiverCard.getCardNumber();

                //validation
                if (receiverPhone.equals("")) Utils.toast(context, getString(R.string.enter_phone_number));
                else if (receiverPhone.length() < 12) Utils.toast(context, getString(R.string.short_number));
                else if (receiverPhone.length() > 13) Utils.toast(context, getString(R.string.long_number));
                else if (receiverCard == null) Utils.toast(context, getString(R.string.enter_card));
                else if (amount.equals("") || amount.equals("0"))
                    Utils.toast(context, "Введите сумму");

                else if (Utils.isOnline(context)){
                    ApiClient.getApiClient().createInvoice(TokenStorage.getToken(context),
                           new CreateInvoiceBody(receiverPhone, cardFullNumber, amount, comment, null, goods))
                            .enqueue(new BaseCallback<Object>(context, true) {
                                @Override
                                protected void onResult(int code, Object result) {
                                    if (code == 201) context.startActivity(
                                            new Intent(context, PayRequestActivity.class) );
                                }
                            });
                }
                else Utils.noInternetToast(context);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_CARD){
            receiverCard = data.getParcelableExtra(CARD);
            if (receiverCard != null) {
                receiverCardNumber = receiverCard.getLastFourNumbers();
                receiverCardName = receiverCard.getName();
                if (receiverCardName.equals("")) tvCard.setText(receiverCardNumber);
                else tvCard.setText(String.format("%s | %s", receiverCardName, receiverCardNumber));
                Log.d("receiverCard", receiverCardNumber);
            }
        }
        else if (requestCode == REQUEST_CONTACT){
            receiverContact = data.getParcelableExtra(CONTACT);
            if (receiverContact != null) {
                receiverPhone = receiverContact.getPhone();
                receiverName = receiverContact.getName();
                if (receiverPhone != null) etPhone.setText(receiverPhone);
            }
        }
    }
}
