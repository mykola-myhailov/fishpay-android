package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.joint_purchases.AddJoinPurchaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.EmptyCallback;
import com.myhailov.mykola.fishpay.api.results.JointPurchasesResult;

import retrofit2.Call;

public class JointPurchasesActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchases);

        setContentView(R.layout.activity_drawer_sample);

        initDrawerToolbar(getString(R.string.joint_purchase));
        createDrawer();
    }


    private void getJointPurchasesRequest(){
        ApiClient.getApiClient().getJointPurchases(token)
                .enqueue(new BaseCallback<JointPurchasesResult>(context, true) {
                    @Override
                    protected void onResult(int code, JointPurchasesResult result) {

                    }

                    @Override
                    public void onFailure(@NonNull Call<BaseResponse<JointPurchasesResult>> call, @NonNull Throwable t) {
                        super.onFailure(call, t);
                    }
                });


    }

    @Override
    public void onClick(View view) {

    }


    private void addNewPurchase(){
        context.startActivity(new Intent(context, AddJoinPurchaseActivity.class));
    }

    private void getJointPurchaseDetailsRequest(){

    }


    private void deletePurchaseRequest(String purchaseId) {
        ApiClient.getApiClient().deleteJointPurchase(token, purchaseId).enqueue(new EmptyCallback());
    }
}
