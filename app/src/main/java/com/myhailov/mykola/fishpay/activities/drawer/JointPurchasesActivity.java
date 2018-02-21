package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.joint_purchases.AddJoinPurchaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.EmptyCallback;
import com.myhailov.mykola.fishpay.api.results.JointPurchasesResult;

import retrofit2.Call;

public class JointPurchasesActivity extends DrawerActivity implements TabLayout.OnTabChangedListener {

    private final static int TAB_ALL = 0;
    private final static int TAB_MY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchases);
        initDrawerToolbar(getString(R.string.joint_purchase));
        createDrawer();


        initTabLayout();

    }

    private void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tab_l);
        tabLayout.setTabChangedListener(this);
        tabLayout.addTab(new Tab("Все", TAB_ALL));
        tabLayout.addTab(new Tab("Созданные мною", TAB_MY));
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

    private void getJointPurchaseDetailsRequest(){

    }

    private void deletePurchaseRequest(String purchaseId) {
        ApiClient.getApiClient().deleteJointPurchase(token, purchaseId).enqueue(new EmptyCallback());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        context.startActivity(new Intent(context, AddJoinPurchaseActivity.class));
        return true;
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTabChanged(int position) {
        switch (position) {
            case TAB_ALL:

                break;
            case TAB_MY:

                break;
        }
    }
}
