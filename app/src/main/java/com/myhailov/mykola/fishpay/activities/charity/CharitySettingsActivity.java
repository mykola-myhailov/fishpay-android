package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.CharityRequestBody;
import com.myhailov.mykola.fishpay.api.results.CharityResultById;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_CREATE;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_MEMBERS_VISIBILITY;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_RESULT;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_VISIBILITY;

public class CharitySettingsActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener, Switch.OnCheckedChangeListener {
    private TextView tvTitle;
    private TextView tvVisibility;
    private TextView tvListDonation;
    private TextView etPseudonym;
    private TextView tvClose;
    private TextView tvCloseCharityAlert, tvCancelAlert;
    private Switch swPseudonym;
    private PopupMenu pmCharityVisibility;
    private PopupMenu pmMembersVisibility;
    private AlertDialog alertDialog;

    private boolean newCharity = false;

    private CharityResultById charity = new CharityResultById();
    private CharityRequestBody charityCreate = new CharityRequestBody();
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_settings);
        initCustomToolbar("Настройки");
        assignViews();
        extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getBoolean(CHARITY_CREATE, false)) {
                newCharity = true;
                tvClose.setText(getString(R.string.preview_charity));
                charityCreate = (CharityRequestBody) extras.getSerializable(CHARITY_RESULT);
                initPopupMenu(tvVisibility, tvListDonation);
                tvTitle.setText(charityCreate.getTitle());
            } else {
                charitySettings();
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_visible:
                pmCharityVisibility.show();
                break;
            case R.id.tv_list_donation:
                pmMembersVisibility.show();
                break;
            case R.id.tv_cancel_alert:
                alertDialog.cancel();
                break;
            case R.id.tv_close_charity_alert:
                closeCharity(charity.getId().toString());
                alertDialog.cancel();
                break;
            case R.id.tv_close:
                if (newCharity) {
                    if (swPseudonym.isChecked()) {
                        charityCreate.setPseudonym(etPseudonym.getText().toString());
                    }else {
                        charityCreate.setPseudonym("");
                    }
                    Intent intent = new Intent(context, CharityPreviewActivity.class);
                    intent.putExtra(CHARITY_RESULT, charityCreate);
                    startActivity(intent);
                } else {
                    if (charity.getStatus().equals("ACTIVE")) {
                        showAlert();
                    } else {
                        toast("Закрыто");
                    }
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1:
                tvVisibility.setText(getString(R.string.public_charity));
                charityCreate.setItemVisibility("PUBLIC");
                break;
            case R.id.item_2:
                tvVisibility.setText(getString(R.string.contacts_charity));
                charityCreate.setItemVisibility("CONTACTS");
                break;
            case R.id.item_3:
                tvVisibility.setText(getString(R.string.author_charity));
                charityCreate.setItemVisibility("AUTHOR");
                break;
            case R.id.item_4:
                tvListDonation.setText(getString(R.string.charity_open));
                charityCreate.setMembersVisibility("true");
                break;
            case R.id.item_5:
                tvListDonation.setText(getString(R.string.charity_close));
                charityCreate.setMembersVisibility("false");
                break;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        etPseudonym.setText("");
        if (isChecked){
            etPseudonym.setEnabled(true);
        }else {
            etPseudonym.setEnabled(false);
        }
    }

    private void charitySettings() {
        setClickable(false);
        charity = (CharityResultById) extras.getSerializable(CHARITY_RESULT);
        if (!TextUtils.isEmpty(extras.getString(CHARITY_VISIBILITY))) {
            switch (extras.getString(CHARITY_VISIBILITY, "")) {
                case "PUBLIC":
                    tvVisibility.setText(R.string.public_charity);
                    break;
                case "CONTACTS":
                    tvVisibility.setText(R.string.contacts_charity);
                    break;
                case "AUTHOR":
                    tvVisibility.setText(R.string.author_charity);
                    break;
            }

        }
        if (!TextUtils.isEmpty(extras.getString(CHARITY_MEMBERS_VISIBILITY))) {
            if (extras.getString(CHARITY_MEMBERS_VISIBILITY, "").equals("true")) {
                tvListDonation.setText(getString(R.string.charity_open));
            } else {
                tvListDonation.setText(getString(R.string.charity_close));
            }
        }
        setValue();
    }

    private void setClickable(boolean flag) {
        tvVisibility.setClickable(flag);
        tvListDonation.setClickable(flag);
        swPseudonym.setClickable(flag);
    }

    private void setValue() {
        tvTitle.setText(charity.getTitle());
        etPseudonym.setText(charity.getAuthorName());
    }

    private void assignViews() {
        tvClose = findViewById(R.id.tv_close);
        tvTitle = findViewById(R.id.tv_title);
        tvVisibility = findViewById(R.id.tv_visible);
        tvListDonation = findViewById(R.id.tv_list_donation);
        etPseudonym = findViewById(R.id.et_pseudonym);
        swPseudonym = findViewById(R.id.switch_name);

        swPseudonym.setOnCheckedChangeListener(this);
        tvVisibility.setOnClickListener(this);
        tvListDonation.setOnClickListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);

    }

    private void initPopupMenu(View charity, View members) {
        pmCharityVisibility = new PopupMenu(this, charity);
        pmCharityVisibility.inflate(R.menu.charity_visibility_menu);
        pmCharityVisibility.setOnMenuItemClickListener(this);

        pmMembersVisibility = new PopupMenu(this, members);
        pmMembersVisibility.inflate(R.menu.members_visibility_menu);
        pmMembersVisibility.setOnMenuItemClickListener(this);
    }

    private void showAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_charity_close, null);
        dialogBuilder.setView(dialogView);
        tvCloseCharityAlert = dialogView.findViewById(R.id.tv_close_charity_alert);
        tvCancelAlert = dialogView.findViewById(R.id.tv_cancel_alert);

        tvCloseCharityAlert.setOnClickListener(this);
        tvCancelAlert.setOnClickListener(this);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void closeCharity(String charityId) {
        ApiClient.getApiInterface().closeChatiry(TokenStorage.getToken(context), charityId)
                .enqueue(new BaseCallback<Object>(context, false) {
                    @Override
                    protected void onResult(int code, Object result) {
                        toast("Закрито");
                    }
                });
    }

}


