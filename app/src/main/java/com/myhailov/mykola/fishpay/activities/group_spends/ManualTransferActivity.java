package com.myhailov.mykola.fishpay.activities.group_spends;

import com.google.gson.JsonElement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

import java.util.ArrayList;

import retrofit2.Call;

import static com.myhailov.mykola.fishpay.utils.Keys.JSON_ELEMENT;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBERS;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;

public class ManualTransferActivity extends BaseActivity {
    public static final int CHOOSE_RECUEST = 51;

    private TextView tvNameFrom, tvPhoneFrom, tvNameTo, tvPhoneTo;
    private EditText etComment, etAmount;
    private ImageView ivAvatarFrom, ivAvatarTo;

    private boolean isFrom = false;
    private long myUserId;
    private ArrayList<MemberDetails> members;
    private MemberDetails member;
    private GroupSpend spend;
    private String comment, fromId, toId;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_transfer);
        initCustomToolbar(getString(R.string.manual_transaction));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            spend = getIntent().getExtras().getParcelable(Keys.SPEND);
            members = getIntent().getExtras().getParcelableArrayList(Keys.MEMBERS);
            member = getIntent().getExtras().getParcelable(MEMBER);

        }

        myUserId = Long.valueOf(getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, "0"));
        initViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_create:
                createTransaction();
                break;
            case R.id.iv_menu_to:
                isFrom = false;
                startActivityForResult(new Intent(context, ChooseMemberActivity.class)
                        .putExtra(TITLE, context.getString(R.string.to_members))
                        .putExtra(MEMBERS, members), CHOOSE_RECUEST);
                break;
            case R.id.iv_menu:
                isFrom = true;
                startActivityForResult(new Intent(context, ChooseMemberActivity.class)
                        .putExtra(TITLE, context.getString(R.string.from_members))
                        .putExtra(MEMBERS, members), CHOOSE_RECUEST);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_RECUEST && resultCode == RESULT_OK) {
            MemberDetails memberDetails = data.getParcelableExtra(MEMBER);
            if (isFrom) {
                fromId = memberDetails.getId() + "";
                String initials = Utils.extractInitials(memberDetails.getName(), memberDetails.getSurname());
                String photo = memberDetails.getPhoto();
                Utils.displayAvatar(context, ivAvatarFrom, photo, initials);
                tvNameFrom.setText(memberDetails.getName() + " " + memberDetails.getSurname());
                tvPhoneFrom.setText("+" + memberDetails.getPhone());
            } else {
                toId = memberDetails.getId() + "";
                String initials = Utils.extractInitials(memberDetails.getName(), member.getSurname());
                String photo = memberDetails.getPhoto();
                Utils.displayAvatar(context, ivAvatarTo, photo, initials);
                tvNameTo.setText(memberDetails.getName() + " " + memberDetails.getSurname());
                tvPhoneTo.setText("+" + memberDetails.getPhone());
            }

        }
    }

    private void initViews() {
        tvNameFrom = findViewById(R.id.tv_name);
        tvPhoneFrom = findViewById(R.id.tv_phone);
        tvNameTo = findViewById(R.id.tv_name_to);
        tvPhoneTo = findViewById(R.id.tv_phone_to);
        ivAvatarFrom = findViewById(R.id.ivAvatar);
        ivAvatarTo = findViewById(R.id.ivAvatar_to);

        etAmount = findViewById(R.id.et_amount);
        etComment = findViewById(R.id.et_comment);
        setValue();

        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.iv_menu_to).setOnClickListener(this);
        findViewById(R.id.iv_menu).setOnClickListener(this);
        findViewById(R.id.tv_create).setOnClickListener(this);
    }

    private void createTransaction() {
        if (isValid()) {
            addSpendRequest();
        }
    }

    private void addSpendRequest() {
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
            return;
        }
        ApiClient.getApiInterface().spendTransaction(TokenStorage.getToken(context),
                spend.getId(), true, fromId, toId, amount, comment)
                .enqueue(new BaseCallback<JsonElement>(context, true) {

                    @Override
                    protected void onError(int code, String errorDescription) {
                        super.onError(code, errorDescription);
                        Log.d("sss", "onError: " + errorDescription);
                    }

                    @Override
                    protected void onResult(int code, JsonElement result) {
                        context.startActivity(new Intent(context, GroupSpendsTransitionSuccessActivity.class)
                                .putExtra(JSON_ELEMENT, result.toString()));
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<BaseResponse<JsonElement>> call, @NonNull Throwable t) {
                        super.onFailure(call, t);
                        Log.d("sss", "onFailure: " + t);
                    }
                });
    }

    private boolean isValid() {
        if (tvPhoneTo.getText().toString().equals(tvPhoneFrom.getText().toString())) {
            toast(getString(R.string.fields_must_filled_not_equals));
            return false;
        }
        if (TextUtils.isEmpty(etComment.getText().toString())) {
            toast(getString(R.string.enter_comment));
            return false;
        }
        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            toast(getString(R.string.enter_amount));
            return false;
        }

        comment = etComment.getText().toString();
        amount = Utils.UAHtoPenny(etAmount.getText().toString());

        return true;
    }

    private void setValue() {
        for (MemberDetails member : members) {
            if (member.getUserId() != null && member.getUserId().equals(myUserId + "")) {
                fromId = member.getId() + "";
                String initials = Utils.extractInitials(member.getName(), member.getSurname());
                String photo = member.getPhoto();
                Utils.displayAvatar(context, ivAvatarFrom, photo, initials);
                tvNameFrom.setText(member.getName() + " " + member.getSurname());
                tvPhoneFrom.setText("+" + member.getPhone());
                break;
            }
        }
        toId = member.getId() + "";
        String initials = Utils.extractInitials(member.getName(), member.getSurname());
        String photo = member.getPhoto();
        Utils.displayAvatar(context, ivAvatarTo, photo, initials);
        tvNameTo.setText(member.getName() + " " + member.getSurname());
        tvPhoneTo.setText("+" + member.getPhone());
    }
}
