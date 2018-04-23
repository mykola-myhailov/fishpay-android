package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.SelectContactsActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Utils;

import static com.myhailov.mykola.fishpay.activities.pay_requests.SelectContactsActivity.REQUEST_CONTACT;
import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;

public class TransactionActivity extends DrawerActivity {

    private TextView tvCard, tvName;
    private ImageView ivChoseContact;
    private EditText etComment, etAmount;
    private Card receiverCard;
    private String receiverPhone = "", receiverName = "",
            receiverCardNumber = "", receiverCardName = "";

    private Contact receiverContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_transaction);

        createDrawer();
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
        etAmount = findViewById(R.id.met_amount);
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
           /*     receiverPhone = etPhone.getText().toString();
                if (receiverPhone.substring(0, 1).equals("+")) receiverPhone = receiverPhone.substring(1);
                final String amountUAH = etAmount.getText().toString();
                if (!fromJointPurchase) amount = Utils.UAHtoPenny(amountUAH);
                String comment = etComment.getText().toString();



                //validation
                if (receiverPhone.equals("")) Utils.toast(context, getString(R.string.enter_phone_number));
                else if (receiverPhone.length() < 12) Utils.toast(context, getString(R.string.short_number));
                else if (receiverPhone.length() > 13) Utils.toast(context, getString(R.string.long_number));
                else if (receiverCard == null) Utils.toast(context, getString(R.string.enter_card));
                else if (amount == 0) Utils.toast(context, "Введите сумму");
                else if (Utils.isOnline(context)){
                    String cardId = receiverCard.getId();
                    String memberId = null;
                    if (member != null) memberId = member.getId();
                    ApiClient.getApiClient().createInvoice(TokenStorage.getToken(context),
                            receiverPhone, cardId, amount, comment, memberId, null)
                            .enqueue(new BaseCallback<CreateInvoiceResult>(context, true) {
                                @Override
                                protected void onResult(int code, CreateInvoiceResult result) {
                                    if (code == 201) {
                                        if (result == null) return;
                                        startActivity(new Intent(context, ConfirmPayRequestActivity.class)
                                                .putExtra(Keys.REQUEST_ID, result.getRequestId())
                                                .putExtra(Keys.CONTACT, result.getReceiver())
                                                .putExtra(Keys.CARD, receiverCardNumber)
                                                .putExtra(Keys.AMOUNT, amountUAH)
                                        );
                                    }
                                }
                            });
                }
                else Utils.noInternetToast(context);
                break;*/
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
                if (receiverName != null) tvName.setText(receiverName);
            }
        }
    }
}
