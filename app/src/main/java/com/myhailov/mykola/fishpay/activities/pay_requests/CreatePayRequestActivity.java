package com.myhailov.mykola.fishpay.activities.pay_requests;

import com.google.gson.Gson;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.PayRequestActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.adapters.ContactsAdapter;
import com.myhailov.mykola.fishpay.adapters.GoodsSelectPayRequest;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.CreateInvoiceResult;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.SearchedContactsResult;
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
import static com.myhailov.mykola.fishpay.activities.pay_requests.SelectGoodsActivity.REQUEST_GOODS;
import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.AMOUNT;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.GOODS;
import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_TOTAL_PRICE;
import static com.myhailov.mykola.fishpay.utils.Keys.LOAD_CONTACTS;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;
import static com.myhailov.mykola.fishpay.utils.Keys.SEARCHED_CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND_CREATOR;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;


public class CreatePayRequestActivity extends BaseActivity implements View.OnKeyListener {

    private EditText etPhone, etComment;
    private MoneyEditText etAmount;
    private TextView tvCard, tvName;
    private RecyclerView rvContacts, rvGoods;
    private String receiverPhone = "", receiverName = "",
            receiverCardNumber = "", cardName = "";
    private Contact receiverContact;
    private Member member;
    private Card card;
    private int amount;
    private boolean fromJointPurchase, loadContacts = true, isSpend = false;
    private View rlRequestAmount;
    private ArrayList<Contact> appContacts;
    private ArrayList<GoodsResults> selectedGoods;

    private String title;
    private String beforeChangeAmount;
    private String spendingId, memberFrom, memberTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pay_request);

        // TODO: 06.07.2018 в розробці
//        showInfoAlert();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(AMOUNT)) {
                amount = extras.getInt(AMOUNT);
            }

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
                title = extras.getString(TITLE, "");
            }
            if (extras.containsKey(LOAD_CONTACTS)) {
                loadContacts = extras.getBoolean(LOAD_CONTACTS, true);
            }
            if (extras.containsKey(SEARCHED_CONTACT)) {
                SearchedContactsResult.SearchedContact searchedContact = extras.getParcelable(Keys.SEARCHED_CONTACT);
                if (searchedContact != null) {
                    receiverContact = new Contact();
                    receiverPhone = searchedContact.getPhone();
                    receiverName = searchedContact.getName() + " " + searchedContact.getSurname();
                }
            }

            if (extras.containsKey(Keys.CARD)) {
                card = extras.getParcelable(Keys.CARD);
            }

            if (extras.containsKey(Keys.SPEND)) {
                MemberDetails memberDetails = extras.getParcelable(Keys.MEMBERS);
                if (memberDetails == null) return;
                isSpend = true;
                receiverContact = new Contact();
                receiverName = memberDetails.getName();
                receiverPhone = memberDetails.getPhone();
                spendingId = extras.getString(SPEND_ID, "");
                memberFrom = extras.getString(SPEND_CREATOR, "");
                memberTo = extras.getString(MEMBER_ID, "");
                amount = Math.abs((int) memberDetails.getRelativeBallance());
                findViewById(R.id.iv_payment).setVisibility(View.GONE);
                findViewById(R.id.tv_invoice).setVisibility(View.GONE);
                findViewById(R.id.view_divider3).setVisibility(View.GONE);
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
            receiverCardNumber = card.getCardNumber();
            cardName = card.getName();
        }
        if (loadContacts) {
            loadContacts();
        }
        initCustomToolbar(getString(R.string.payment_request));
        initViews();

        if (extras != null && extras.containsKey(Keys.COMMENT)) {
            etComment.setText(extras.getString(Keys.COMMENT, ""));
        }

        // set information if group purchase
        if (!loadContacts) {
            rvContacts.setVisibility(View.GONE);
            rlRequestAmount.setVisibility(View.VISIBLE);
//            tvName.setText("|" + receiverName);
//            tvName.setVisibility(View.VISIBLE);
            etComment.setText(getString(R.string.contribution_of_joint_purchase, title));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_invoice:
                startActivityForResult(new Intent(context, SelectGoodsActivity.class), REQUEST_GOODS);
                break;
            case R.id.container:   // click on app user's contact, to see details
                Contact contact = (Contact) view.getTag();
                if (contact != null) {
                    receiverContact = contact;
                    rvContacts.setVisibility(View.GONE);
                    rlRequestAmount.setVisibility(View.VISIBLE);
                    receiverPhone = receiverContact.getPhone();
                    receiverName = receiverContact.getName();
                    String sur = receiverContact.getSurname();
                    if (receiverPhone != null) etPhone.setText(receiverPhone);
                    if (!TextUtils.isEmpty(receiverName)) {
                        if (TextUtils.isEmpty(sur)) sur = "";
                        tvName.setText(" | " + receiverName + " " + sur);
                        tvName.setVisibility(View.VISIBLE);
                    }

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
                else if (amount == 0) Utils.toast(context, getString(R.string.input_amount));
                else if (Utils.isOnline(context)) {
                    String cardId = card.getId();
                    String memberId = null;
                    if (member != null) memberId = member.getId();
                    if (!isSpend) {
                        ApiClient.getApiInterface().createInvoice(TokenStorage.getToken(context),
                                receiverPhone, cardId, amount, comment, memberId, prepareGoods(selectedGoods))
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
                    } else {
                        ApiClient.getApiInterface().initInvoice(TokenStorage.getToken(context),
                                receiverPhone, cardId, amount + "", comment, memberFrom, memberTo)
                                .enqueue(new BaseCallback<CreateInvoiceResult>(context, true) {
                                    @Override
                                    protected void onResult(int code, CreateInvoiceResult result) {
                                        if (result == null) return;
                                        startActivity(new Intent(context, ConfirmPayRequestActivity.class)
                                                .putExtra(Keys.REQUEST_ID, result.getRequestId())
                                                .putExtra(Keys.CONTACT, result.getReceiver())
                                                .putExtra(Keys.CARD, receiverCardNumber)
                                                .putExtra(Keys.AMOUNT, amountUAH)
                                        );
                                    }
                                });
                    }
                } else Utils.noInternetToast(context);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        if (keyCode == KeyEvent.KEYCODE_ENTER && v.getId() == R.id.et_phone) {
            String num = "";
            boolean hasContact = false;
            if (etPhone.getText().toString().charAt(0) == '+')
                num = etPhone.getText().toString().substring(1);
            else num = etPhone.getText().toString();

            for (Contact appContact : appContacts) {
                if (appContact.getPhone().equals(num)){
                    hasContact = true;
                    if (rvContacts != null) rvContacts.setVisibility(View.GONE);
                    rlRequestAmount.setVisibility(View.VISIBLE);
                    tvName.setText(" | " + appContact.getName() + " " + appContact.getSurname());
                    tvName.setVisibility(View.VISIBLE);
                    break;
                }
            }
            if (!hasContact){
                tvName.setText("");
            }
            return true;
        }
        return false;
    }

    private void loadContacts() {
        List<Contact> contacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        appContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            long userId = contact.getUserId();
            if (userId != 0) appContacts.add(contact);
        }
    }

 /*   private void showInfoAlert() {
        TextView tvTitle;
        final AlertDialog infoAlert;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_with_one_action, null);
        dialogBuilder.setView(dialogView);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvTitle.setText(context.getString(R.string.info));


        ((TextView) dialogView.findViewById(R.id.tv_description)).setText(context.getString(R.string.info_description));

        infoAlert = dialogBuilder.create();
        infoAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoAlert.show();
        infoAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialogView.findViewById(R.id.tv_action_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoAlert.cancel();
                finish();
            }
        });
    }
*/
    private void initViews() {
        findViewById(R.id.iv_choose_contact).setOnClickListener(this);
        findViewById(R.id.tv_send_request).setOnClickListener(this);
        findViewById(R.id.rl_card).setOnClickListener(this);
        findViewById(R.id.tv_invoice).setOnClickListener(this);
        tvName = findViewById(R.id.tv_name);

        etPhone = findViewById(R.id.et_phone);
        etPhone.setOnKeyListener(this);
        tvCard = findViewById(R.id.tv_card_number);
        etComment = findViewById(R.id.et_comment);
        etAmount = findViewById(R.id.met_amount);
        etAmount.addTextChangedListener(amountTextWatcher);

        if (fromJointPurchase) {
            etAmount.setClickable(false);
            etAmount.setLongClickable(false);
            etAmount.setTextIsSelectable(false);
            etAmount.setCursorVisible(false);
            etAmount.setFocusableInTouchMode(false);
            etAmount.setText(Utils.pennyToUah(amount));
        }
        if (receiverPhone.equals("")) receiverPhone = "+380";
        if (receiverName.equals("")) {
            etPhone.setText(receiverPhone);
        } else {
            etPhone.setText(receiverPhone);
            tvName.setText(" | " + receiverName);
            tvName.setVisibility(View.VISIBLE);
        }
        if (fromJointPurchase) {
            etPhone.setClickable(false);
            etPhone.setLongClickable(false);
            etPhone.setTextIsSelectable(false);
            etPhone.setCursorVisible(false);
            etPhone.setFocusableInTouchMode(false);
        }
        if (isSpend) {
            etAmount.setText(Utils.pennyToUah(amount));
        }

        if (cardName.equals("")) tvCard.setText(receiverCardNumber);
        else {
            Spannable spannable = new SpannableString(String.format("%s | %s", receiverCardNumber, cardName));
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey2)),
                    receiverCardNumber.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvCard.setText(formatCardNumber(spannable));
        }

        rvGoods = findViewById(R.id.rv_goods);
        rvGoods.setLayoutManager(new LinearLayoutManager(context));


        rlRequestAmount = findViewById(R.id.rl_request_amount);

        rvContacts = findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(context));
        rvContacts.setAdapter(new ContactsAdapter(context, appContacts));

        if (receiverContact == null) rlRequestAmount.setVisibility(View.GONE);
        else rvContacts.setVisibility(View.GONE);
    }

    private TextWatcher amountTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            beforeChangeAmount = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String grn = "", cent = "";

            if (s.toString().contains(".")) {
                grn = s.toString().split("\\.", 2)[0];
                cent = s.toString().split("\\.", 2)[1];

                if (grn.length() > 6) {
                    etAmount.setText(beforeChangeAmount);
                    etAmount.setSelection(etAmount.getText().length());
                }

            } else {
                if (s.toString().length() > 6) {
                    etAmount.setText(beforeChangeAmount);
                    etAmount.setSelection(etAmount.getText().length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

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
                receiverCardNumber = card.getCardNumber();
                cardName = card.getName();
                if (cardName.equals("")) tvCard.setText(receiverCardNumber);
                else {
                    Spannable spannable = new SpannableString(String.format("%s | %s", receiverCardNumber, cardName));
                    spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey2)),
                            receiverCardNumber.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvCard.setText(formatCardNumber(spannable));
                }
                Log.d("card", receiverCardNumber);
            }
        } else if (requestCode == REQUEST_CONTACT) {
            rvContacts.setVisibility(View.GONE);
            rlRequestAmount.setVisibility(View.VISIBLE);
            receiverContact = data.getParcelableExtra(CONTACT);
            if (receiverContact != null) {
                receiverPhone = receiverContact.getPhone();
                receiverName = receiverContact.getName();
                String sur = receiverContact.getSurname();
                if (receiverPhone != null) etPhone.setText(receiverPhone);
                if (!TextUtils.isEmpty(receiverName)) {
                    if (TextUtils.isEmpty(sur)) sur = "";
                    tvName.setText(" | " + receiverName + " " + sur);
                    tvName.setVisibility(View.VISIBLE);
                }
            }
        }

        if (requestCode == REQUEST_GOODS) {
            amount = (int) data.getLongExtra(GOODS_TOTAL_PRICE, 0);
            selectedGoods = (ArrayList) data.getSerializableExtra(GOODS);
            etAmount.setText(Utils.pennyToUah(amount));
            if (amount != 0) {
                etAmount.setEnabled(false);
            } else {
                etAmount.setEnabled(true);
            }
            if (selectedGoods.size() != 0) {
                etComment.setText(getString(R.string.invoice_for_goods, selectedGoods.get(0).getTitle()));
                rvGoods.setVisibility(View.VISIBLE);
                rvGoods.setAdapter(new GoodsSelectPayRequest(context, selectedGoods, rvListener));

            } else {
                rvGoods.setVisibility(View.GONE);
            }

        }
    }

    private GoodsSelectPayRequest.OnItemClickListener rvListener = new GoodsSelectPayRequest.OnItemClickListener() {
        @Override
        public void onItemClick(long id) {
            Intent intent = new Intent(context, GoodsDetailPayRequestActivity.class);
            intent.putExtra(GOODS_ID, id);
            startActivity(intent);
        }
    };

    private String prepareGoods(ArrayList<GoodsResults> selectedGoods) {
        if (selectedGoods == null) return null;
        JSONArray goodsArray = new JSONArray();
        try {
            for (GoodsResults goods : selectedGoods) {
                JSONObject goodsJsonObject = new JSONObject();
                goodsJsonObject.put("count", goods.getCount());
                goodsJsonObject.put("goods_id", goods.getId());
                goodsArray.put(goodsJsonObject);
            }
        } catch (Exception ignored) {
        }
        return goodsArray.toString();
    }

    private SpannableString formatCardNumber(Spannable cardNumber) {
        SpannableString spanCardNumber = new SpannableString(cardNumber);
        spanCardNumber.setSpan(new UnderlineSpan(), 0, 4, 0);
        spanCardNumber.setSpan(new UnderlineSpan(), 5, 9, 0);
        spanCardNumber.setSpan(new UnderlineSpan(), 10, 14, 0);
        spanCardNumber.setSpan(new UnderlineSpan(), 15, 19, 0);
        return spanCardNumber;
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
