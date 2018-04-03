package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.PayRequestActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.api.requestBodies.SelectedGoods;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.MoneyEditText;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private Member member;
    private Card receiverCard;
    private int amount;

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
                    receiverName = receiverContact.getFullName();
                }
            }
            else if (extras.containsKey(Keys.MEMBER)) {
                member = extras.getParcelable(Keys.MEMBER);
                if (member != null){
                    receiverPhone = member.getPhone();
                    receiverName = member.getFirstName() + member.getLastName();
                    amount = member.getAmountToPay();
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
        if (receiverPhone.equals("")) receiverPhone = "+380";
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
                String amountUAH = etAmount.getText().toString();
                amount = Utils.UAHtoPenny(amountUAH);
                String comment = etComment.getText().toString();

                String cardId = receiverCard.getId();
                String memberId = null;
                if (member != null) memberId = member.getId();

                //validation
                if (receiverPhone.equals("")) Utils.toast(context, getString(R.string.enter_phone_number));
                else if (receiverPhone.length() < 12) Utils.toast(context, getString(R.string.short_number));
                else if (receiverPhone.length() > 13) Utils.toast(context, getString(R.string.long_number));
                else if (receiverCard == null) Utils.toast(context, getString(R.string.enter_card));
                else if (amountUAH.equals("") || amountUAH.equals("0"))
                    Utils.toast(context, "Введите сумму");

                else if (Utils.isOnline(context)){
                    ApiClient.getApiClient().createInvoice(TokenStorage.getToken(context),
                            receiverPhone, cardId, amount, comment, memberId, null)
                            .enqueue(new BaseCallback<CreateInvoiceResult>(context, true) {


                                @Override
                                protected void onResult(int code, CreateInvoiceResult result) {
                                    if (code == 201) showConfirmDialog(result.getRequestId());
                                }
                            });
                }
                else Utils.noInternetToast(context);

                break;
        }
    }

    public class CreateInvoiceResult {
        @SerializedName("request_id")
        private String requestId;

        public String getRequestId() {
            return requestId;
        }
    }
    private void showConfirmDialog(final String requestId) {
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(context)
                .setMessage("Для отправки запроса введите пароль")
                .setView(input)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password = input.getText().toString();
                        if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
                        else if (password.length() < 8) Utils.toast(context, getString(R.string.password8));
                        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                        else ApiClient.getApiClient().confirmInvoice(TokenStorage.getToken(context)
                            , requestId, password).enqueue(new BaseCallback<Object>(context, true) {
                                @Override
                                protected void onResult(int code, Object result) {
                                    context.startActivity(new Intent(context, PayRequestActivity.class));
                                }
                            });
                    }
                })
                .create().show();

        // context.startActivity(new Intent(context, PayRequestActivity.class) );
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


    private  String prepareGoods(ArrayList<SelectedGoods> selectedGoods) {
        JSONArray goodsArray = new JSONArray();
        try {
            for (SelectedGoods goods: selectedGoods) {
                JSONObject goodsJsonObject= new JSONObject();
                goodsJsonObject.put("count",  goods.getCount());
                goodsJsonObject.put("goods_id", goods.getGoods().getId());
                goodsArray.put(goodsJsonObject);
            }
        } catch (Exception ignored){}
        return goodsArray.toString();
    }

}
