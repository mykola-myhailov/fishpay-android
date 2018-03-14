package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.JointPurchasesActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.api.results.JointPurchase;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.MoneyEditText;

import org.w3c.dom.Text;

import retrofit2.Call;

import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;
import static com.myhailov.mykola.fishpay.utils.Utils.pennyToUah;
import static com.myhailov.mykola.fishpay.utils.Utils.setPhoto;

/**
 * Created by nicholas on 02.03.18.
 */

public class PaymentMemberActivity extends BaseActivity {

    private final int CASH = 1;
    private final int TRANSFER = 2;
    private final int OTHER = 3;
    private int payMethod = CASH;

    private String title;
    private Member member;
    private final CharSequence[] items = {"Наличные", "Перевод", "Другое"};
    private TextView tvPayMethod;
    private MoneyEditText metAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_member);

        title = getIntent().getStringExtra(TITLE);
        member = getIntent().getParcelableExtra(MEMBER);

        initCustomToolbar("внести взнос");
        initViews();
    }

    private void initViews() {
        TextView tvInitials = findViewById(R.id.tv_initials);
        ImageView ivAvatar = findViewById(R.id.iv_avatar);
        TextView tvContactName = findViewById(R.id.tv_contact_name);
        TextView tvUserName = findViewById(R.id.tv_user_name);
        metAmount = findViewById(R.id.met_amount);
        findViewById(R.id.tv_apply).setOnClickListener(this);

        String initials = Utils.extractInitials(member.getFirstName(), member.getSecondName());
        setPhoto(context, member.getPhoto(), initials, tvInitials, ivAvatar);

        if (member.getFullContactName() != null) tvContactName.setText(member.getFullContactName());
        else tvContactName.setVisibility(View.GONE);
        if (member.getFullUserName() != null) tvUserName.setText(member.getFullUserName());
        else tvUserName.setVisibility(View.GONE);

        tvPayMethod = findViewById(R.id.tv_pay_method);
        tvPayMethod.setText(items[payMethod-1]);
        tvPayMethod.setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_phone)).setText(member.getPhone());
        ((TextView) findViewById(R.id.tv_purchase)).setText(title);

        String toPay = pennyToUah(Float.valueOf(member.getAmountToPay()));
        metAmount.setHint(toPay);
        metAmount.setText(toPay);
    }

    private void showChoosePayMethod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Выберите метод оплаты:")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG", "onClick: "+which);
                        payMethod = which+1;
                        tvPayMethod.setText(items[which]);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Отмена", null);
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_pay_method:
                showChoosePayMethod();
                break;
            case R.id.tv_apply:
                changeJointPurchaseStatus(v);
                break;
        }
    }

    private void changeJointPurchaseStatus(final View view) {
        view.setClickable(false);
        ApiClient.getApiClient().changeJointPurchase(TokenStorage.getToken(context),
                member.getId(),
                metAmount.getCurrency(),
                payMethod).enqueue(new BaseCallback<Object>(context, false) {
            @Override
            protected void onResult(int code, Object result) {
                if (code == 202) {
                    setResult(RESULT_OK);
                    finish();
//                    startActivity(new Intent(context, JointPurchaseDetailsActivity.class)
//                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                else view.setClickable(false);
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<Object>> call, @NonNull Throwable t) {
                super.onFailure(call, t);
                view.setClickable(false);
            }

            @Override
            protected void onError(int code, String errorDescription) {
                super.onError(code, errorDescription);
                view.setClickable(false);
            }
        });
    }


}
