package com.myhailov.mykola.fishpay.activities.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.UapayInfoStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Calendar;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AddCardActivity extends BaseActivity {

    private WebView webview;
    private String token;
    private EditText etCardName;
    private CardData cardData;
    private View llCardData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        llCardData = findViewById(R.id.llCardData);
        llCardData.setVisibility(View.GONE);
        token = generateToken();
        Log.e("token", token);

        initWebWiew();
    }

    public class JsInterface  {
        //This method will be called from the script after receiving a response from Uapay
        @JavascriptInterface
        public void receive(String value) {
            JSdata jSdata = new Gson().fromJson(value, JSdata.class);
            String encodedCardData = jSdata.encodedCardData;
            if (encodedCardData != null){
                cardData = new Gson().fromJson(decode58(encodedCardData), CardData.class);

                AddCardActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llCardData.setVisibility(View.VISIBLE);

                        TextView tvNameError = findViewById(R.id.tv_name_error);
                        etCardName = findViewById(R.id.et_card_name);
                        TextView tvCardNumber = findViewById(R.id.tv_card_number);
                        TextView tvAddCard = findViewById(R.id.tv_add_card);

                        tvCardNumber.setText(cardData.panMasked);
                        tvAddCard.setOnClickListener((View.OnClickListener) context);
                    }
                });
            } else {
                String error = jSdata.error;
            }
        }
    }

    private class JSdata {
        @SerializedName("Success") private String encodedCardData;
        @SerializedName("Error") private String error;
    }

    private class CardData {
        @SerializedName ("id") String  id;
        @SerializedName ("panMasked") String panMasked ;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_add_card:
                String cardName = etCardName.getText().toString();
                if (cardName.length() > 0) sendCardRequest(cardName);
                else Utils.toast(context, context.getString(R.string.enter_card_name));
                break;
        }
    }

    private void sendCardRequest(String cardName) {
        if (!Utils.isOnline(context)){
            Utils.noInternetToast(context);
            return;
        }
        ApiClient.getApiInterface().createCard(TokenStorage.getToken(context), cardName, cardData.panMasked, cardData.id)
                .enqueue(new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, Object result) {
                        if (code == 242) Utils.toast(context, getString(R.string.already_has_card));
                        context.startActivity(new Intent(context, CardsActivity.class));
                    }
                });
    }

    private String generateToken() {
        String key = UapayInfoStorage.getUapayKeyKey(context);
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();

        JSONObject data = new JSONObject();
        JSONObject params = new JSONObject();
        try { params.put("clientId", UapayInfoStorage.getUapayIdKey(context)); }
        catch (JSONException e){ e.printStackTrace();}
        try { params.put("method", "createCard"); } catch (JSONException e) { e.printStackTrace();}
        try { data.put("params", params); } catch (JSONException e) { e.printStackTrace(); }
        try { data.put("iat", time/1000); } catch (JSONException e) { e.printStackTrace(); }
        Log.e("json", data.toString());
        Log.e("key", key);
        return Jwts.builder()
                .setPayload(data.toString())
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebWiew() {
        webview = findViewById(R.id.webWiew);
        if (Build.VERSION.SDK_INT >= 19) { webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); }

        webview.getSettings().setJavaScriptEnabled(true);
        boolean sanbox = UapayInfoStorage.getUapaySandbox(context);
        String html = null;
        if (sanbox)  html ="<body style=\"margin: 0; background: #1d1b1d;\">" +
                "<iframe id=\"uapayFrame\" " +
                "style=\"height: 40%; width: 100%; background: #fff;\"" +
                "src=\"http://api.demo.uapay.ua/api/iframe/"+ token +"\"></iframe>" +
                "<div><button id=\"btnSubmit\" style=\"border: 2px solid #f6db3e; font-size: 18px;" +
                " background: transparent; color: #f6db3e; display: block;" +
                " width: 40%; padding: 10px 15px; margin: 30px auto 0 auto; " +
                "text-transform: capitalize; cursor: pointer;\">Send</button></div>" +
                "<script>" +
                "var iframe = document.getElementById('uapayFrame').contentWindow," +
                "btn = document.getElementById('btnSubmit');" +
                "btn.addEventListener('click', function (e) {iframe.postMessage('Submit', \"*\");});" +
                "function listener(event) {" +
                "if(event.data){" +
                "if(event.data.name === 'Success')" +
                "{ window.AndroidInterface.receive('{\"Success\":\"'+event.data.payload+'\"}');}" +
                "else if(event.data.name === 'Error')" +
                "{ window.AndroidInterface.receive('{\"Error\":\"'+event.data.code+'\"}');" +
                "}}}\n" + "window.addEventListener(\"message\", listener, false);\n" +
                "</script></body>";

        else html ="<body style=\"margin: 0; background: #1d1b1d;\">" +
                "<iframe id=\"uapayFrame\" " +
                "style=\"height: 40%; width: 100%; background: #fff;\"" +
                "src=\"http://api.uapay.ua/api/iframe/"+ token +"\"></iframe>" +
                "<div><button id=\"btnSubmit\" style=\"border: 2px solid #f6db3e; font-size: 18px;" +
                " background: transparent; color: #f6db3e; display: block;" +
                " width: 40%; padding: 10px 15px; margin: 30px auto 0 auto; " +
                "text-transform: capitalize; cursor: pointer;\">Send</button></div>" +
                "<script>" +
                "var iframe = document.getElementById('uapayFrame').contentWindow," +
                "btn = document.getElementById('btnSubmit');" +
                "btn.addEventListener('click', function (e) {iframe.postMessage('Submit', \"*\");});" +
                "function listener(event) {" +
                "if(event.origin !='https://addcard.uapay.ua'){\n" +
                "return;\n" +
                "}"+
                "if(event.data){" +
                "if(event.data.name === 'Success')" +
                "{ window.AndroidInterface.receive('{\"Success\":\"'+event.data.payload+'\"}');}" +
                "else if(event.data.name === 'Error')" +
                "{ window.AndroidInterface.receive('{\"Error\":\"'+event.data.code+'\"}');" +
                "}}}\n" + "window.addEventListener(\"message\", listener, false);\n" +
                "</script></body>";

        webview.addJavascriptInterface(new JsInterface(), "AndroidInterface");
        webview.loadData(html, "text/html", null);
    }

    private String decode58 (String encoded) {
        final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        BigInteger bi = BigInteger.valueOf(0);
        for (int i = encoded.length() - 1; i >= 0; i--) {
            int alphaIndex = ALPHABET.indexOf(encoded.charAt(i));
            bi = bi.add(BigInteger.valueOf(alphaIndex).multiply(BigInteger.valueOf(58).pow(encoded.length() - 1 - i)));
        }
        return new String(bi.toByteArray());
    }
}
