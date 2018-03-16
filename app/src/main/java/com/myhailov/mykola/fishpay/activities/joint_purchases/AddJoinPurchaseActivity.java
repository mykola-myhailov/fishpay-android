package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.api.requestBodies.CommonPurchaseBody;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.views.MoneyEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class AddJoinPurchaseActivity extends BaseActivity {

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
        setContentView(R.layout.activity_create_join_purchase);
        initCustomToolbar(getString(R.string.creating_joint_purchase));

        initViews();
    }

    private void initViews() {
        etGroupName = findViewById(R.id.et_group_name);
        etAmount = findViewById(R.id.met_amount);
        etDescription = findViewById(R.id.et_description);
        tvPayTo = findViewById(R.id.tv_pay_to);
        tvChooseCard = findViewById(R.id.tv_choose_card);
        tvCardName = findViewById(R.id.tv_card_name);
        tvCardNumber = findViewById(R.id.tv_card_number);
        llPublicCard = findViewById(R.id.ll_public_card);

        /*etAmount.setFilters(new InputFilter[]{new MoneyValueFilter(10,2)});
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        findViewById(R.id.rl_card).setOnClickListener(this);
        findViewById(R.id.tv_create).setOnClickListener(this);
        tvPayTo.setOnClickListener(this);
        tvChooseCard.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pay_to:
                showDateDialog();
                break;
            case R.id.rl_card:
            case R.id.tv_choose_card:
                startActivityForResult(
                        new Intent(context, CardsActivity.class).putExtra(REQUEST, true), REQUEST_CARD);
                break;
            case R.id.tv_create:
                if (isDataValid()) {
                    nextActivity();
                }
                break;
        }
    }

    private void nextActivity() {
        CommonPurchaseBody body = new CommonPurchaseBody();
        body.setTitle(etGroupName.getText().toString());
        body.setAmount(String.valueOf(etAmount.getCurrency()));
        if (etDescription.getText().toString().length() > 0)
            body.setDescription(etDescription.getText().toString());
        if (dateTo != null && !dateTo.equals(""))
            body.setDateTo(dateTo);
        if (card != null)
            body.setCreatorCardId(card.getId());
        startActivity(new Intent(context, ChooseMembersActivity.class)
                .putExtra(Keys.PURCHASE, body));
    }

    private boolean isDataValid() {

        boolean isNameValid = etGroupName.getText().toString().length() > 0;
        boolean isAmountValid = etAmount.getText().toString().length() > 0;
        if (isNameValid && isAmountValid) return true;
        else if (!isNameValid && !isAmountValid) {
            toast("Введите название группы и сумму!");
        } else if (!isNameValid) {
            toast("Введите название группы!");
        } else {
            toast("Введите сумму!");
        }
        return false;
    }

    private void showDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final DatePicker datePicker = new DatePicker(context);
        Calendar minDate = Calendar.getInstance(Locale.getDefault());
        minDate.add(DATE, 1);
        datePicker.setMinDate(minDate.getTimeInMillis());
        minDate.add(DATE, 2);
        datePicker.updateDate(minDate.get(YEAR), minDate.get(MONTH), minDate.get(DAY_OF_MONTH));

        builder.setView(datePicker);
        builder.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.set(
                        datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth()
                );
                dateTo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                tvPayTo.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(calendar.getTime()));
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CARD && resultCode == RESULT_OK) {
            card = data.getParcelableExtra(CARD);
            if (card != null) {
                llPublicCard.setVisibility(View.VISIBLE);
                tvChooseCard.setVisibility(View.GONE);
                tvCardName.setText(card.getName());
                tvCardNumber.setText(String.valueOf(card.getLastFourNumbers()));
            } else {
                llPublicCard.setVisibility(View.GONE);
                tvChooseCard.setVisibility(View.VISIBLE);
            }
        }
    }
}
