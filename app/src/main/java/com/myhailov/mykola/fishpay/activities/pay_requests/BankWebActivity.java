package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.internal.LinkedTreeMap;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.ApiInterface;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;

import static com.myhailov.mykola.fishpay.activities.TransactionActivity.CHARITY_DONATION;
import static com.myhailov.mykola.fishpay.activities.TransactionActivity.INCOMING_PAY_REQUEST;
import static com.myhailov.mykola.fishpay.activities.TransactionActivity.JOINT_PURCHASE;
import static com.myhailov.mykola.fishpay.activities.TransactionActivity.TRANSFER;

public class BankWebActivity extends AppCompatActivity {

    private String termUrl, paReq, bankUrl, fpt, fptId, type;
    private Context context;
    private Bundle extras;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        extras = getIntent().getExtras();

        type = extras.getString(Keys.TYPE);
        bankUrl = extras.getString(Keys.URL);
        termUrl =  extras.getString(Keys.TERM_URL);
        paReq = extras.getString(Keys.PA_REQ);
        fpt = extras.getString(Keys.FPT);
        fptId = extras.getString(Keys.FPT_ID);

        final WebView webview = new WebView(this);
        setContentView(webview);
        String postData = null;
        try {
            postData = "TermUrl=" + URLEncoder.encode(termUrl, "UTF-8")
                    + "&PaReq=" + URLEncoder.encode(paReq, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (postData != null) {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient(){
                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                 if (url.contains(ApiClient.BANK_REDIRECT)){
                        webview.destroy();
                        requestAuditpay();
                    }
                }
            });
            webview.postUrl(bankUrl, postData.getBytes());
        }

        else Utils.toast(context, getString(R.string.error));

    }

    private void requestAuditpay() {
        if (Utils.isOnline(context)){
            ApiInterface apiInterface = ApiClient.getApiInterface();
            Call<BaseResponse<Object>> call;
            Log.d("sss", "requestAuditpay: " + type);
            switch (type){
                case TRANSFER:
                    call = apiInterface.auditpay(TokenStorage.getToken(context), fpt, fptId);
                    break;
                case INCOMING_PAY_REQUEST:
                    String requestId = extras.getString(Keys.REQUEST_ID);
                    call = apiInterface.auditpayIncomging(TokenStorage.getToken(context), fpt, fptId, requestId);
                    break;
                case JOINT_PURCHASE:
                    String purchaseId = extras.getString(Keys.PURCHASE_ID);
                    call = apiInterface.auditpayPurchase(TokenStorage.getToken(context), fpt, fptId, purchaseId);
                    break;
                case CHARITY_DONATION:
                    String charityId = extras.getString(Keys.CHARITY_ID);
                    String isAnon = extras.getString(Keys.CHARITY_ANON);
                    call = apiInterface.auditPayCharity(TokenStorage.getToken(context), "true", isAnon,
                            charityId, fpt, fptId);
                    break;
                default: return;
            }
            call.enqueue(new AuditpayCallback(context, true));
        }
    }


    private class AuditpayCallback extends BaseCallback<Object> {

        protected AuditpayCallback(@NonNull Context context, boolean showProgress) {
            super(context, showProgress);
        }

        @Override
        protected void onResult(int code, Object result) {
            switch (result.toString()) {

                case "SUCCESS":
                    Utils.toast(context, "success");
                    onBackPressed();
                    break;
                case "REJECTED":
                    Utils.toast(context, "rejected");
                    onBackPressed();
                    break;
                case "REVERSED":
                    Utils.toast(context, "reversed");
                    onBackPressed();
                    break;
                case "ERROR":
                    Utils.toast(context, "error");
                    onBackPressed();
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
         if (type.equals("lookup")){
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
                        code = input.getText().toString();

                        if (code.equals("")) Utils.toast(context, getString(R.string.enter_sms_code));
                        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
                        else lookupRequest();
                    }
                })
                .create().show();
    }

    private void lookupRequest() {
        ApiInterface apiInterface = ApiClient.getApiInterface();
        Call<BaseResponse<String>> call;
        switch (type){
            case TRANSFER:
                call = apiInterface.sendLookup(TokenStorage.getToken(context),fpt, fptId, code);
                break;
            case INCOMING_PAY_REQUEST:
                String requestId = extras.getString(Keys.REQUEST_ID);
                call = apiInterface.sendLookupIncoming(TokenStorage.getToken(context), fpt, fptId, requestId, code);
                break;
            case JOINT_PURCHASE:
                String purchaseId = extras.getString(Keys.PURCHASE_ID);
                call = apiInterface.sendLookupIncoming(TokenStorage.getToken(context), fpt, fptId, purchaseId, code);
                break;
            case CHARITY_DONATION:
                String charityId = extras.getString(Keys.CHARITY_ID);
                String isAnon = extras.getString(Keys.CHARITY_ANON);
                call = apiInterface.sendLookupCharity(TokenStorage.getToken(context), "true", isAnon,
                        charityId, fpt, fptId, code);
                break;
            default: return;
        }
        call.enqueue(new LookupCallback(context, true));


    }

    private class LookupCallback extends BaseCallback<String> {

        LookupCallback(@NonNull Context context, boolean showProgress) {
            super(context, showProgress);
        }

        @Override
        protected void onResult(int code, String result) {
            switch (result.toLowerCase()){
                case "error_code":
                    Utils.toast(context, getString(R.string.incorrect_code));
                    requestLookup();
                    break;
                case "success":
                    Utils.toast(context, getString(R.string.successfully));
                    onBackPressed();
                    break;
                default:
                    Utils.toast(context, getString(R.string.error));
                    onBackPressed();
                    break;
            }
        }
    }
}
