package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
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
            postData = "TermUrl=" + URLEncoder.encode(termUrl, "UTF-8") + "&PaReq=" + URLEncoder.encode(paReq, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (postData != null) {
            webview.postUrl(bankUrl, postData.getBytes());
            webview.setWebViewClient(new WebViewClient(){
                @Override
                public void onLoadResource(WebView view, String url) {
                    if (url.equals(bankUrl))super.onLoadResource(view, url);
                    else onBackPressed();
                }
            });
        } else Utils.toast(context, getString(R.string.error));


    }

    private void requetAuditpay() {
        if (Utils.isOnline(context)){
            ApiClient.getApiClient()
                    .auditpay(TokenStorage.getToken(context), fpt, fptId)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            onBackPressed();
                        }
                    });
        }
    }
}
