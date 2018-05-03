package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.internal.LinkedTreeMap;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.AuditPayResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BankWebActivity extends AppCompatActivity {

    private String termUrl, paReq, bankUrl, fpt, fptId;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        Bundle extras = getIntent().getExtras();

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
            ApiClient.getApiClient()
                    .auditpay(TokenStorage.getToken(context), fpt, fptId)
                    .enqueue(new BaseCallback<Object>(context, true) {
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
                    });
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
                                                    onBackPressed();
                                                    break;
                                                default:
                                                    Utils.toast(context, "Ошибка");
                                                    onBackPressed();
                                                    break;
                                            }
                                        }
                                    });
                    }
                })
                .create().show();
    }
}
