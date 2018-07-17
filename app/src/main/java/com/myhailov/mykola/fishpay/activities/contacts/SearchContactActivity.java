package com.myhailov.mykola.fishpay.activities.contacts;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.SearchedContactsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;

public class SearchContactActivity extends BaseActivity {

    private EditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);

//        initToolBar(getString(R.string.enter_number));
        initCustomToolbar(getString(R.string.enter_number));
        etPhone = findViewById(R.id.etPhone);
        (findViewById(R.id.tvSearch)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvSearch:
                searchPhoneRequest();
                break;
        }
    }

    private void searchPhoneRequest() {
        if (!Utils.isOnline(context)) {
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
                        public void onFailure(@NonNull Call<BaseResponse<SearchedContactsResult>> call, @NonNull Throwable t) {
                            super.onFailure(call, t);
                            showInfoAlert();
                        }

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
                            } else if (code == 400) {
                                Utils.alert(context, "Этот номер уже есть среди ваших активных контактов");
                            }
                        }
                    });
    }

    private void showInfoAlert() {
        TextView tvTitle;
        final AlertDialog infoAlert;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_with_one_action, null);
        dialogBuilder.setView(dialogView);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvTitle.setText(context.getString(R.string.not_found_phone_number));
        dialogView.findViewById(R.id.tv_description).setVisibility(View.GONE);

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
