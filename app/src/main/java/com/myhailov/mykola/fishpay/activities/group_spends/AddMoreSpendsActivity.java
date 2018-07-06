package com.myhailov.mykola.fishpay.activities.group_spends;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
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

        if (extras.containsKey(MEMBER)){
            member = extras.getParcelable(MEMBER);
            if (member != null) {
                userName = member.getName();
                userSurname = member.getSurname();
                phoneNumber = member.getPhone();
                photoLink = member.getPhoto();
                if (member.getUserId() != null) {
                    userId = member.getUserId();
                }
            }
        }else {

            //if
            userName = preferences.getString(PrefKeys.NAME, "");
            userSurname = preferences.getString(PrefKeys.SURNAME, "");
            phoneNumber = preferences.getString(PrefKeys.PHONE, "");
            photoLink = preferences.getString(PrefKeys.AVATAR, "");
            userId = preferences.getString(PrefKeys.ID, "");
        }

        initViews();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_add:
                addSpendRequest();
                break;
            case R.id.ivBack:
                onBackPressed();
        }
    }

    private void addSpendRequest(){
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else if (isDataValid())
        ApiClient.getApiInterface().spendTransaction(TokenStorage.getToken(context),
                spendId, true,userId, null, pennyAmount, comment)
                .enqueue(new BaseCallback<JsonElement>(context, true) {

                    @Override
                    protected void onError(int code, String errorDescription) {
                        super.onError(code, errorDescription);
                    }

                    @Override
                    protected void onResult(int code, JsonElement result) {
                        setResult(RESULT_OK);
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
