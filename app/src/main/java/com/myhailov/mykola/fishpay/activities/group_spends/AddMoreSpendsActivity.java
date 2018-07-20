package com.myhailov.mykola.fishpay.activities.group_spends;

import com.google.gson.JsonElement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import retrofit2.Call;

import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;

public class AddMoreSpendsActivity extends BaseActivity {
    public final int MENU_CODE = 58;

    private GroupSpend spend;
    private MemberDetails member;

    private String comment, userId, userName, userSurname, phoneNumber, photoLink, spendTitle;
    private int pennyAmount;
    private long spendId;

    private EditText etAmount, etComment;
    private TextView tvSpendTitle, tvUserName, tvPhone;
    private ImageView ivPhoto;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spend);

        initCustomToolbar(getString(R.string.add_expenses));
        preferences = getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        spend = extras.getParcelable(Keys.SPEND);
        spendId = spend.getId();
        spendTitle = spend.getTitle();

        if (extras.containsKey(MEMBER)) {
            member = extras.getParcelable(MEMBER);
            if (member != null) {
                userName = member.getName();
                userSurname = member.getSurname();
                phoneNumber = member.getPhone();
                photoLink = member.getPhoto();
                userId = member.getId() + "";
            }
        } else {
            //if
            findViewById(R.id.iv_menu).setOnClickListener(this);
            userName = preferences.getString(PrefKeys.NAME, "");
            userSurname = preferences.getString(PrefKeys.SURNAME, "");
            phoneNumber = preferences.getString(PrefKeys.PHONE, "");
            photoLink = preferences.getString(PrefKeys.AVATAR, "");
            userId = extras.getLong(Keys.MEMBER_ID, 0) + "";

        }
        initViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add:
                addSpendRequest();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.iv_menu:
                startActivityForResult(new Intent(context, GroupSpendMenuActivity.class)
                        .putExtra(Keys.TITLE, spend.getTitle()), MENU_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MENU_CODE){
            GroupSpend item;
            item = data.getParcelableExtra(Keys.SPEND);
            userId = data.getStringExtra(Keys.MEMBER_ID);
            if (item != null){
                spendId = item.getId();
                spendTitle = item.getTitle();
                tvSpendTitle.setText(spendTitle);
            }

        }
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        etComment = findViewById(R.id.etComment);

        tvUserName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        ivPhoto = findViewById(R.id.ivAvatar);

        tvUserName.setText(userName + " " + userSurname);
        tvPhone.setText(phoneNumber);
        String initials = Utils.extractInitials(userName, userSurname);
        Utils.displayAvatar(context, ivPhoto, photoLink, initials);


        tvSpendTitle = findViewById(R.id.tvSpendName);
        tvSpendTitle.setText(spendTitle);

        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.tv_add).setOnClickListener(this);
    }

    private void addSpendRequest() {
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else if (isDataValid())
            ApiClient.getApiInterface().spendTransactionWithoutMemberTo(TokenStorage.getToken(context),
                    spendId + "", true, userId, pennyAmount, comment)
                    .enqueue(new BaseCallback<JsonElement>(context, true) {

                        @Override
                        protected void onError(int code, String errorDescription) {
                            super.onError(code, errorDescription);
                        }

                        @Override
                        protected void onResult(int code, JsonElement result) {
                            startActivity(new Intent(context, GroupSpendsAddActivity.class)
                                .putExtra(Keys.JSON_ELEMENT, result.toString()));
                            finish();
                        }

                        @Override
                        public void onFailure(@NonNull Call<BaseResponse<JsonElement>> call, @NonNull Throwable t) {
                            super.onFailure(call, t);
                            Log.d("sss", "onFailure: " + t);
                            onBackPressed();
                        }
                    });
    }

    private boolean isDataValid() {
        comment = etComment.getText().toString();
        String amount = etAmount.getText().toString();
        if (comment.length() < 1) Utils.toast(context, getString(R.string.enter_comment));
        else if (amount.length() < 1) Utils.toast(context, getString(R.string.enter_amount));
        else if ((pennyAmount = Utils.UAHtoPenny(amount)) <= 0)
            Utils.toast(context, getString(R.string.enter_amount));
        else return true;

        return false;
    }

}
