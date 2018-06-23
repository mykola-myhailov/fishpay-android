package com.myhailov.mykola.fishpay.activities.group_spends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.joint_purchases.ChooseMembersActivity;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.views.MoneyEditText;

public class CreateGroupSpendActivity extends BaseActivity {

    private MoneyEditText etAmount;
    private EditText etGroupName,
            etDescription;
    private TextView tvPayTo,
            tvChooseCard,
            tvCardName,
            tvCardNumber;
    private View llPublicCard;

    private Card card;
    private String dateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_spend);

        initCustomToolbar(getString(R.string.create_joint_spending));
        initViews();
    }

    private void initViews() {
        etGroupName = findViewById(R.id.et_group_name);
        etAmount = findViewById(R.id.met_amount);
        etDescription = findViewById(R.id.et_description);
        findViewById(R.id.tv_create).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.tv_create:
                if (isDataValid()) {
                    String groupName = etGroupName.getText().toString();
                    int amount = etAmount.getCurrency();
                    String description = etDescription.getText().toString();
                    context.startActivity(new Intent(context, ChooseMembersActivity.class)
                            .putExtra(Keys.AMOUNT, amount)
                            .putExtra(Keys.DESCRIPTION, description)
                            .putExtra(Keys.GROUP, groupName)
                    );
                }
                break;
        }
    }

    private boolean isDataValid() {
        boolean isNameValid = etGroupName.getText().toString().length() > 0;
        boolean isAmountValid = etAmount.getText().toString().length() > 0;
        if (isNameValid && isAmountValid) return true;
        else if (!isNameValid && !isAmountValid)  toast(getString(R.string.enter_name_group_and_amount));
        else if (!isNameValid) toast(getString(R.string.enter_name_group));
        else toast(getString(R.string.enter_amount));
        return false;
    }
}
