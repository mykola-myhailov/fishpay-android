package com.myhailov.mykola.fishpay.activities.charity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
    private Switch swPseudonym;
    private PopupMenu pmCharityVisibility;
    private PopupMenu pmMembersVisibility;

    private boolean newCharity = false;

    private CharityResultById charity = new CharityResultById();
    private CharityRequestBody charityCreate = new CharityRequestBody();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_settings);
        initCustomToolbar("Настройки");
        assignViews();
        if (getIntent() != null) {
            if (getIntent().getBooleanExtra(CHARITY_CREATE, false)) {
                newCharity = true;
                tvClose.setText(getString(R.string.preview_charity));
                charityCreate = (CharityRequestBody) getIntent().getSerializableExtra(CHARITY_RESULT);
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
                        showAlert("Данные о сборе буду сохраняться в публичном доступе и дальше. Возможность взносов будет закрыта");
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
        charity = (CharityResultById) getIntent().getSerializableExtra(CHARITY_RESULT);
        if (!TextUtils.isEmpty(getIntent().getStringExtra(CHARITY_VISIBILITY))) {
            switch (getIntent().getStringExtra(CHARITY_VISIBILITY)) {
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
        if (!TextUtils.isEmpty(getIntent().getStringExtra(CHARITY_MEMBERS_VISIBILITY))) {
            if (getIntent().getStringExtra(CHARITY_MEMBERS_VISIBILITY).equals("true")) {
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

    private void showAlert(String message) {
        new AlertDialog.Builder(context)
                .setTitle("Закрыть взаимопомощь?")
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        closeCharity(charity.getId().toString());
                    }
                })
                .setNegativeButton("no", null)
                .create().show();
    }

    private void closeCharity(String charityId) {
        ApiClient.getApiClient().closeChatiry(TokenStorage.getToken(context), charityId)
                .enqueue(new BaseCallback<Object>(context, false) {
                    @Override
                    protected void onResult(int code, Object result) {
                        toast("Закрито");
                    }
                });
    }

}


