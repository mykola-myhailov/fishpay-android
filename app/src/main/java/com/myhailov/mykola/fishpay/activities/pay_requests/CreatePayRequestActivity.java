package com.myhailov.mykola.fishpay.activities.pay_requests;

import com.google.gson.Gson;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.PayRequestActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.adapters.ContactsAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.api.requestBodies.SelectedGoods;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.CreateInvoiceResult;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.MoneyEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.activities.pay_requests.SelectContactsActivity.REQUEST_CONTACT;
import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.LOAD_CONTACTS;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;


public class CreatePayRequestActivity extends BaseActivity {

    private EditText etPhone, etComment, etGoods;
    private MoneyEditText etAmount;
    private TextView tvCard;
    private RecyclerView rvContacts;
    private String receiverPhone = "", receiverName = "",
            receiverCardNumber = "", cardName = "";
    private Contact receiverContact;
    private Member member;
    private Card card;
    private int amount;
    private boolean fromJointPurchase, loadContacts;
    private View rlRequestAmount;
    private ArrayList<Contact> appContacts;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pay_request);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(Keys.CONTACT)) {
                receiverContact = extras.getParcelable(Keys.CONTACT);
                if (receiverContact != null) {
                    receiverPhone = receiverContact.getPhone();
                    receiverName = receiverContact.getFullName();
                }
            } else if (extras.containsKey(Keys.MEMBER)) {
                member = extras.getParcelable(Keys.MEMBER);
                if (member != null) {
                    receiverPhone = member.getPhone();
                    receiverName = member.getFirstName() + " " + member.getSecondName();
                    amount = member.getAmountToPay();
                    fromJointPurchase = true;
                }
                loadContacts = extras.getBoolean(LOAD_CONTACTS, true);
                title = extras.getString(TITLE, "");
            }

            if (extras.containsKey(Keys.CARD)) {
                card = extras.getParcelable(Keys.CARD);
            }

        }
        if (card == null) {
            SharedPreferences sharedPreferences = getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
            if (sharedPreferences.contains(PrefKeys.CARD)) {
                String cardJson = sharedPreferences.getString(PrefKeys.CARD, null);
                Log.e("cardJson", cardJson);
                card = cardJson == null ? null : new Gson().fromJson(cardJson, Card.class);
            }
        }
        if (card != null) {
            receiverCardNumber = card.getLastFourNumbers();
            cardName = card.getName();
        }
        if (loadContacts) {
            loadContacts();
        }
        initCustomToolbar("запрос на оплату");
        initViews();
        // set information if group purchase
        if (!loadContacts) {
            rvContacts.setVisibility(View.GONE);
            rlRequestAmount.setVisibility(View.VISIBLE);
            etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            Spannable spannable = new SpannableString(String.format("%s | %s", receiverPhone, receiverName));
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey2)), receiverPhone.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            etPhone.setText(spannable);
            etComment.setText("Взнос в рамках общей покупки " + title);
        }
    }

    private void loadContacts() {
        List<Contact> contacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        appContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            long userId = contact.getUserId();
            if (userId != 0) appContacts.add(contact);
        }
    }

    private void initViews() {
        findViewById(R.id.iv_choose_contact).setOnClickListener(this);
        findViewById(R.id.tv_send_request).setOnClickListener(this);
        findViewById(R.id.rl_card).setOnClickListener(this);

        etPhone = findViewById(R.id.et_phone);
        tvCard = findViewById(R.id.tv_card_number);
        etComment = findViewById(R.id.et_comment);
        etAmount = findViewById(R.id.met_amount);
        if (fromJointPurchase) {
            etAmount.setClickable(false);
            etAmount.setLongClickable(false);
            etAmount.setTextIsSelectable(false);
            etAmount.setCursorVisible(false);
            etAmount.setFocusableInTouchMode(false);
            etAmount.setText(Utils.pennyToUah(amount));
        }
        etGoods = findViewById(R.id.et_goods);
        if (receiverPhone.equals("")) receiverPhone = "+380";
        if (receiverName.equals("")) {
            etPhone.setText(receiverPhone);
        } else {
            etPhone.setText(String.format("%s | %s", receiverPhone, receiverName));
        }
        if (fromJointPurchase) {
            etPhone.setClickable(false);
            etPhone.setLongClickable(false);
            etPhone.setTextIsSelectable(false);
            etPhone.setCursorVisible(false);
            etPhone.setFocusableInTouchMode(false);
        }
        if (cardName.equals("")) tvCard.setText(receiverCardNumber);
        else tvCard.setText(String.format("%s | %s", receiverCardNumber, cardName));

        rlRequestAmount = findViewById(R.id.rl_request_amount);

        rvContacts = findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(context));
        rvContacts.setAdapter(new ContactsAdapter(context, appContacts));

        if (receiverContact == null) rlRequestAmount.setVisibility(View.GONE);
        else rvContacts.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.container:   // click on app user's contact, to see details
                Contact contact = (Contact) view.getTag();
                if (contact != null) {
                    receiverContact = contact;
                    rvContacts.setVisibility(View.GONE);
                    rlRequestAmount.setVisibility(View.VISIBLE);
                    receiverPhone = receiverContact.getPhone();
                    receiverName = receiverContact.getName();
                    if (receiverPhone != null) etPhone.setText(receiverPhone);

                } else Utils.toast(context, getString(R.string.error));


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
                if (receiverPhone.substring(0, 1).equals("+"))
                    receiverPhone = receiverPhone.substring(1);
                final String amountUAH = etAmount.getText().toString();
                if (!fromJointPurchase) amount = Utils.UAHtoPenny(amountUAH);
                String comment = etComment.getText().toString();


                //validation
                if (receiverPhone.equals(""))
                    Utils.toast(context, getString(R.string.enter_phone_number));
                else if (receiverPhone.length() < 12)
                    Utils.toast(context, getString(R.string.short_number));
                else if (receiverPhone.length() > 13)
                    Utils.toast(context, getString(R.string.long_number));
                else if (card == null) Utils.toast(context, getString(R.string.enter_card));
                else if (amount == 0) Utils.toast(context, "Введите сумму");
                else if (Utils.isOnline(context)) {
                    String cardId = card.getId();
                    String memberId = null;
                    if (member != null) memberId = member.getId();
                    ApiClient.getApiInterface().createInvoice(TokenStorage.getToken(context),
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
                } else Utils.noInternetToast(context);
                break;
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
                        if (password.equals(""))
                            Utils.toast(context, getString(R.string.enter_password));
                        else if (password.length() < 8)
                            Utils.toast(context, getString(R.string.password8));
                        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                        else
                            ApiClient.getApiInterface().confirmInvoice(TokenStorage.getToken(context)
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
        if (requestCode == REQUEST_CARD) {
            card = data.getParcelableExtra(CARD);
            if (card != null) {
                receiverCardNumber = card.getLastFourNumbers();
                cardName = card.getName();
                if (cardName.equals("")) tvCard.setText(receiverCardNumber);
                else tvCard.setText(String.format("%s | %s", cardName, receiverCardNumber));
                Log.d("card", receiverCardNumber);
            }
        } else if (requestCode == REQUEST_CONTACT) {
            rvContacts.setVisibility(View.GONE);
            rlRequestAmount.setVisibility(View.VISIBLE);
            receiverContact = data.getParcelableExtra(CONTACT);
            if (receiverContact != null) {
                receiverPhone = receiverContact.getPhone();
                receiverName = receiverContact.getName();
                if (receiverPhone != null) etPhone.setText(receiverPhone);
            }
        }
    }

    private String prepareGoods(ArrayList<SelectedGoods> selectedGoods) {
        JSONArray goodsArray = new JSONArray();
        try {
            for (SelectedGoods goods : selectedGoods) {
                JSONObject goodsJsonObject = new JSONObject();
                goodsJsonObject.put("count", goods.getCount());
                goodsJsonObject.put("goods_id", goods.getGoods().getId());
                goodsArray.put(goodsJsonObject);
            }
        } catch (Exception ignored) {
        }
        return goodsArray.toString();
    }

/*    private class MemberHolder extends RecyclerView.ViewHolder{

        public MemberHolder(View itemView) {
            super(itemView);
        }
    }

    private class SelectContactAdapter extends RecyclerView.Adapter<MemberHolder> {
        @Override
        public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(MemberHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return
        }
    }*/
}
