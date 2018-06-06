package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.PayRequestActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.CreateInvoiceResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class ConfirmPayRequestActivity extends BaseActivity {

    private String requestId, card, name = "", phone = "", photo = "",
            suname = "", contactName = "", amount = "";

    private EditText etPassword;

    private CreateInvoiceResult.ReceiverContact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pay_request);

        Bundle extras = getIntent().getExtras();
        requestId = extras.getString(Keys.REQUEST_ID);
        contact = extras.getParcelable(Keys.CONTACT);
        card = extras.getString(Keys.CARD);
        amount = extras.getString(Keys.AMOUNT);

        initCustomToolbar(getString(R.string.send_request));

        if (contact != null){
            name = contact.getName();
            suname = contact.getSurname();
            contactName = contact.getFullName();
            phone = contact.getPhone();
            photo = contact.getPhoto();
        }
        initViews();
    }

    private void initViews() {
        ((TextView) findViewById(R.id.tv_contact_name)).setText(contactName);
        ((TextView) findViewById(R.id.tv_user_name)).setText(String.format("%s %s", name, suname));
        ((TextView) findViewById(R.id.tv_phone)).setText(phone);
        ((TextView) findViewById(R.id.tv_card)).setText(card);
        ((TextView) findViewById(R.id.met_amount)).setText(amount);
        etPassword = findViewById(R.id.password);
        etPassword.requestFocus();
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                }
                return true;
            }
        });
        findViewById(R.id.tv_apply).setOnClickListener(this);
        ImageView ivAvatar = findViewById(R.id.iv_avatar);
        String initials = Utils.extractInitials(name, suname);
        Utils.displayAvatar(context, ivAvatar, photo, initials );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.tv_apply:
                apply();
                break;
        }
    }

    private void apply() {
        String password = etPassword.getText().toString();
        if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
        else if (password.length() < 8) Utils.toast(context, getString(R.string.password8));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else ApiClient.getApiInterface().confirmInvoice(TokenStorage.getToken(context)
                    , requestId, password).enqueue(new BaseCallback<Object>(context, true) {
                @Override
                protected void onResult(int code, Object result) {
                    context.startActivity(new Intent(context, PayRequestActivity.class));
                }
            });
    }


}
