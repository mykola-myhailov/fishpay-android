package com.myhailov.mykola.fishpay.activities.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.SearchedContactsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

public class SearchContactActivity extends BaseActivity {

    private EditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);

        initToolBar(getString(R.string.enter_number));
        etPhone = findViewById(R.id.etPhone);
        (findViewById(R.id.tvSearch)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        searchPhoneRequest();
    }

    private void searchPhoneRequest() {
        if (!Utils.isOnline(context)){
            Utils.noInternetToast(context);
            return;
        }

        String phone = etPhone.getText().toString();
        if (phone.substring(0, 1).equals("+")) phone = phone.substring(1);
        if (phone.length() < 12) Utils.toast(context, getString(R.string.short_number));
        else if (phone.length() > 13) Utils.toast(context, getString(R.string.long_number));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else ApiClient.getApiInterface().searchContact(TokenStorage.getToken(context), phone)
            .enqueue(new BaseCallback<SearchedContactsResult>(context, true) {
                @Override
                protected void onResult(int code, SearchedContactsResult result) {
                    if (code == 200) {
                        ArrayList<SearchedContactsResult.SearchedContact> contacts = result.getContacts();
                        if (contacts == null || contacts.size() < 1) return;
                        SearchedContactsResult.SearchedContact contact = contacts.get(0);
                        if (contact == null) return;
                        Intent intent = new Intent(context, ContactDetailsActivity.class);
                        intent.putExtra(Keys.SEARCHED_CONTACT, contact);
                        context.startActivity(intent);
                    }
                    else if (code == 400) {
                        Utils.alert(context, "Этот номер уже есть среди ваших активных контактов");
                    }
                }
            });
    }
}
