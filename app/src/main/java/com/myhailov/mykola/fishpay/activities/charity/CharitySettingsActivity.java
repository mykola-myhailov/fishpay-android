package com.myhailov.mykola.fishpay.activities.charity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.results.CharityResultById;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_CREATE;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_MEMBERS_VISIBILITY;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_RESULT;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_VISIBILITY;

public class CharitySettingsActivity extends BaseActivity {
    private TextView tvTitle;
    private TextView tvCategory;
    private TextView tvVisibility;
    private TextView tvListDonation;
    private TextView tvAuthor;
    private TextView tvClose;
    private Switch swPseudonym;

    private boolean flag = false;

    private CharityResultById charity = new CharityResultById();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_settings);
        initCustomToolbar("Настройки");
        assignViews();
        if (getIntent() != null) {
            if (getIntent().getBooleanExtra(CHARITY_CREATE, false)) {
                flag = true;
                tvClose.setText(getString(R.string.preview_charity));

            } else {
                setClickable(false);
                charity = (CharityResultById) getIntent().getSerializableExtra(CHARITY_RESULT);
                if (!TextUtils.isEmpty(getIntent().getStringExtra(CHARITY_VISIBILITY))) {
                    tvVisibility.setText(getIntent().getStringExtra(CHARITY_VISIBILITY));
                }
                if (!TextUtils.isEmpty(getIntent().getStringExtra(CHARITY_MEMBERS_VISIBILITY))) {
                    tvListDonation.setText(getIntent().getStringExtra(CHARITY_MEMBERS_VISIBILITY));
                }
            }
        }
        setValue();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_close:
                if (flag) {

                } else {
                    showAlert("Данные о сборе буду сохраняться в публичном доступе и дальше. Возможность взносов будет закрыта");
                }
                break;
        }
    }

    private void setClickable(boolean flag) {
        tvCategory.setClickable(flag);
        tvVisibility.setClickable(flag);
        tvListDonation.setClickable(flag);
        swPseudonym.setClickable(flag);
    }

    private void setValue() {
        tvTitle.setText(charity.getTitle());
        tvAuthor.setText(charity.getAuthorName());
    }

    private void assignViews() {
        tvClose = findViewById(R.id.tv_close);
        tvTitle = findViewById(R.id.tv_title);
        tvCategory = findViewById(R.id.tv_category);
        tvVisibility = findViewById(R.id.tv_visible);
        tvListDonation = findViewById(R.id.tv_list_donation);
        tvAuthor = findViewById(R.id.tv_author);
        swPseudonym = findViewById(R.id.switch_name);
        findViewById(R.id.tv_close).setOnClickListener(this);

    }

    private void showAlert(String message) {
        new AlertDialog.Builder(context)
                .setTitle("Закрыть благотворительный сбор?")
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 15.05.2018 add request to close charity
                    }
                })
                .setNegativeButton("no", null)
                .create().show();
    }
}
