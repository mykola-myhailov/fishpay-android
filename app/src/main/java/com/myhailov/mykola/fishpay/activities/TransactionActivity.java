package com.myhailov.mykola.fishpay.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.pay_requests.BankWebActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.SelectContactsActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.ApiInterface;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.JointPurchaseDetailsResult;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import retrofit2.Call;
import retrofit2.Response;

import static com.myhailov.mykola.fishpay.activities.pay_requests.SelectContactsActivity.REQUEST_CONTACT;
import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND_CREATOR;
import static com.myhailov.mykola.fishpay.utils.Keys.SPEND_ID;
import static com.myhailov.mykola.fishpay.utils.Utils.showInfoAlert;

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
    private String cvv;
    private String type;

    public final static String TRANSFER = "transfer";
    public final static String INCOMING_PAY_REQUEST = "incoming_pay_request";
    public static final String JOINT_PURCHASE = "joint_purchase";
    public static final String COMMON_SPENDING = "common_spending";
    public static final String CHARITY_DONATION = "charity_donation";
    private String requestId;
    private String purchaseId;
    private String spendingId, memberFrom, memberTo, spendComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_transaction);

        createDrawer();
        // TODO: 06.07.2018 в розробці
//        showInfoAlert();


        preferences = getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
        String userId = preferences.getString(PrefKeys.ID, "");
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            if (extras.containsKey(Keys.NAME)) receiverName = extras.getString(Keys.NAME);
            if (extras.containsKey(Keys.USER_ID)) receiverId = extras.getString(Keys.USER_ID);
            if (extras.containsKey(Keys.AMOUNT)) amountUAH = extras.getString(Keys.AMOUNT);
            if (extras.containsKey(Keys.REQUEST_ID)) requestId = extras.getString(Keys.REQUEST_ID);
            if (extras.containsKey(Keys.TYPE)) type = extras.getString(Keys.TYPE);
            if (type == null) type = TRANSFER;
            switch (type){
                case JOINT_PURCHASE:
                    JointPurchaseDetailsResult jointPurchase = extras.getParcelable(Keys.PURCHASE);
                    if (jointPurchase == null) return;
                    receiverName = jointPurchase.getCreatorName();
                    receiverId = jointPurchase.getCreatorId();
                    for (Member member : jointPurchase.getMembers()) {
                        if (member.getUserId().equals(userId)){
                            amount = member.getAmountToPay();
                            amountUAH = Utils.pennyToUah(amount);
                        }
                    }
                    purchaseId = jointPurchase.getId();
                    break;
                case COMMON_SPENDING:
                    MemberDetails memberDetails = extras.getParcelable(MEMBER);
                    if (memberDetails == null) break;
                    receiverId = "";
                    receiverName = memberDetails.getName();
                    spendingId = extras.getString(SPEND_ID, "");
                    memberFrom = extras.getString(SPEND_CREATOR, "");
                    memberTo = extras.getString(MEMBER_ID, "");
                    amount = (int) memberDetails.getRelativeBallance();
                    amountUAH = Utils.pennyToUah((int) memberDetails.getRelativeBallance());
                    spendComment = extras.getString(Keys.COMMENT, "");
                    break;
            }
         }
        if (type == null) type = TRANSFER;
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

        if (!TextUtils.isEmpty(spendComment)){
            etComment.setText(spendComment);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
        if (sharedPreferences.contains(PrefKeys.CARD)){
            String cardJson = sharedPreferences.getString(PrefKeys.CARD, null);
            Log.e("cardJson", cardJson);
            card = cardJson  == null ? null : new Gson().fromJson(cardJson, Card.class);
        }
        if (card != null) {
            cardNumber = card.getLastFourNumbers();
            cardName = card.getName();
            if (cardName.equals("")) tvCard.setText(cardNumber);
            else tvCard.setText(String.format("%s | %s", cardName, cardNumber));
        }

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
                if (dataIsValid()) payRequest();
                break;
        }
    }
/*
    private void showInfoAlert() {
        TextView tvTitle;
        final AlertDialog infoAlert;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_with_one_action, null);
        dialogBuilder.setView(dialogView);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvTitle.setText(context.getString(R.string.info));


        ((TextView)dialogView.findViewById(R.id.tv_description)).setText(context.getString(R.string.info_description));

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
    }*/

    private boolean dataIsValid() {
        amountUAH = etAmount.getText().toString();
        amount = Utils.UAHtoPenny(amountUAH);
        //   String comment = etComment.getText().toString();
        cvv = etCvv.getText().toString();
        if (receiverContact != null) receiverContact.getUserId();

        //validation
        if (card == null) Utils.toast(context, getString(R.string.enter_card));
        else if (receiverId == null) Utils.toast(context, getString(R.string.select_contact));
        else if (amount == 0) Utils.toast(context, getString(R.string.enter_amount));
        else if (amount >1499900) Utils.toast(context, getString(R.string.max_amount));
        else if (cvv.equals("")) Utils.toast(context,getString(R.string.enter_cvv_code));
        else return true;
        return false;
    }

    private void payRequest() {
        ApiInterface anInterface = ApiClient.getApiInterface();
        Call<BaseResponse<Object>> call;
        switch (type){
            case TRANSFER:
                call = anInterface.transfer(token, receiverId, card.getId(), cvv, amount);
                break;
            case INCOMING_PAY_REQUEST:
                Log.d("sss", "payRequest: "+ token + "\n"+ requestId+ "\n"+ card.getId() + "\n" + cvv+ "\n"+amount);
                call = anInterface.paymentIncoming(token, requestId, card.getId(), cvv);
                break;
            case JOINT_PURCHASE:
                call = anInterface.paymentPurchase(token, purchaseId, card.getId(), cvv);
                break;
            case COMMON_SPENDING:
                // TODO: 11.07.2018 fix request
                if (TextUtils.isEmpty(etComment.getText().toString())){
                    Utils.toast(context, getString(R.string.enter_comment));
                    return;
                }
                call = anInterface.paymentSpend(token, spendingId, true,
                        memberFrom, memberTo, amount + "",etComment.getText().toString(),
                        card.getId(), cvv);
//                call = anInterface.paymentSpend(token, spendingId,
//                        Utils.makeRequestBody("true"),
//                        Utils.makeRequestBody(memberFrom),
//                        Utils.makeRequestBody(memberTo),
//                        Utils.makeRequestBody(amount + ""),
//                        Utils.makeRequestBody(etComment.getText().toString()),
//                        Utils.makeRequestBody(card.getId()),
//                        Utils.makeRequestBody(cvv));
                break;

            default: return;
        }
        call.enqueue(new PayCallback(context, true));
    }

    private class PayCallback extends BaseCallback<Object> {

        PayCallback(@NonNull Context context, boolean showProgress) {
            super(context, showProgress);
        }

        @Override
        public void onResponse(@NonNull Call<BaseResponse<Object>> call, @NonNull Response<BaseResponse<Object>> response) {
            super.onResponse(call, response);
            Log.d("sss", "onResponse: " + response);
        }

        @Override
        protected void onError(int code, String errorDescription) {
            super.onError(code, errorDescription);
            Log.d("sss", "onError: " + errorDescription);

            showErrorAlert();
        }

        @Override
        public void onFailure(@NonNull Call<BaseResponse<Object>> call, @NonNull Throwable t) {
            super.onFailure(call, t);
            Log.d("sss", "onFailure: " + t);

            showErrorAlert();
        }

        @Override
        protected void onResult(int code, Object result) {
            Log.d("sss", "onResult: " + result.toString());
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
                    .putExtra(Keys.TYPE, type)
                    .putExtra(Keys.URL, url)
                    .putExtra(Keys.TERM_URL, termUrl)
                    .putExtra(Keys.PA_REQ, paReq)
                    .putExtra(Keys.FPT, fpt)
                    .putExtra(Keys.FPT_ID, fptId)

                    .putExtra(Keys.REQUEST_ID, requestId)
                    .putExtra(Keys.PURCHASE_ID, purchaseId)

                    .putExtra(Keys.SPEND_ID, spendingId)
                    .putExtra(Keys.MEMBER_FROM, memberFrom)
                    .putExtra(Keys.MEMBER_TO, memberTo)
                    .putExtra(Keys.AMOUNT, amount + "")
                    .putExtra(Keys.COMMENT, etComment.getText().toString())
            );
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
                    .setMessage(getString(R.string.enter_sms_code))
                    .setView(input)
                    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String code = input.getText().toString();

                            if (code.equals("")) Utils.toast(context, getString(R.string.enter_sms_code));
                            else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                            else ApiClient.getApiInterface()
                                        .sendLookup(TokenStorage.getToken(context),fpt, fptId, code)
                                        .enqueue(new BaseCallback<String>(context, true) {
                                            @Override
                                            protected void onResult(int code, String result) {
                                                switch (result.toLowerCase()){
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
            }
        }

        else if (requestCode == REQUEST_CONTACT){
            receiverContact = data.getParcelableExtra(CONTACT);
            if (receiverContact != null) {
                receiverId = receiverContact.getContactId() + "";
                receiverPhone = receiverContact.getPhone();
                receiverName = receiverContact.getName();
                if (receiverName != null) tvName.setText(receiverName);
            }
        }
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
