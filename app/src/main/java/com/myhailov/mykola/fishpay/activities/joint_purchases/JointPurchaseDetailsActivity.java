package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.JointPurchase;
import com.myhailov.mykola.fishpay.api.results.JointPurchaseDetailsResult;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE;
import static com.myhailov.mykola.fishpay.utils.Utils.pennyToUah;

public class JointPurchaseDetailsActivity extends BaseActivity {

    private JointPurchaseDetailsResult purchase;
    private View llDescription;
    private TextView tvDescription;

    private View llCard;
    private TextView tvCardNumber;

    private TextView tvAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchase_details);
        JointPurchase purchase = getIntent().getParcelableExtra(PURCHASE);

        initCustomToolbar(purchase.getTitle());
        initViews();
        getJointPurchase(purchase.getId());

    }

    private void initViews() {
        llDescription = findViewById(R.id.ll_description);
        tvDescription = findViewById(R.id.tv_description);
        llCard = findViewById(R.id.ll_card);
        tvCardNumber = findViewById(R.id.tv_card_number);
        tvAmount = findViewById(R.id.tv_amount);
    }

    private void getJointPurchase(String id) {
        ApiClient.getApiClient().getJointPurchaseDetails(TokenStorage.getToken(context), id)
                .enqueue(new BaseCallback<JointPurchaseDetailsResult>(context, false) {
                    @Override
                    protected void onResult(int code, JointPurchaseDetailsResult result) {
                        hasResponse(result);
                    }
                });
    }

    private void hasResponse(JointPurchaseDetailsResult response) {
        purchase = response;
        if (purchase.getDescription() != null) {
            llDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(purchase.getDescription());
        } else llDescription.setVisibility(View.GONE);
        if (purchase.getCardNumber() != null) {
            llCard.setVisibility(View.VISIBLE);
            tvCardNumber.setText(purchase.getLastFourNumbers());
        } else llCard.setVisibility(View.GONE);

        tvAmount.setText(pennyToUah(((float) purchase.getAmount())));

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

    }
}
