package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;

import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;

/**
 * Created by nicholas on 02.03.18.
 */

public class PaymentMemberActivity extends BaseActivity {

    private final int CASH = 0;
    private final int TRANSFER = 1;
    private final int OTHER = 2;
    private int payMethod = CASH;

    private String title;
    private Member member;
    private CharSequence[] items = {"Наличные", "Перевод", "Другое"};
    private TextView tvPayMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_member);

        title = getIntent().getStringExtra(TITLE);
        member = getIntent().getParcelableExtra(MEMBER);

        initCustomToolbar("внести взнос");
        initViews();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    private void initViews() {
        TextView tvInitials = findViewById(R.id.tv_initials);
        ImageView ivAvatar = findViewById(R.id.iv_avatar);
        TextView tvContactName = findViewById(R.id.tv_contact_name);
        TextView tvPhone = findViewById(R.id.tv_phone);
        EditText etUah = findViewById(R.id.et_uah);
        EditText etPenny = findViewById(R.id.et_penny);
        tvPayMethod = findViewById(R.id.tv_pay_method);
        tvPayMethod.setText("Наличные");
        tvPayMethod.setOnClickListener(this);


        TextView tvPurchase = findViewById(R.id.tv_purchase);
        tvPurchase.setText(title);
    }

    private void showChoosePayMethod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Выберите метод оплаты:")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG", "onClick: "+which);
                        tvPayMethod.setText(items[which]);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Отмена", null);
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_pay_method:
                showChoosePayMethod();
                break;
        }
    }
}
