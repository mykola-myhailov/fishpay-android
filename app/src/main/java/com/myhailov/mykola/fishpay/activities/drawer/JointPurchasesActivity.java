package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.joint_purchases.AddJoinPurchaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.EmptyCallback;
import com.myhailov.mykola.fishpay.api.results.JointPurchase;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;

public class JointPurchasesActivity extends DrawerActivity implements TabLayout.OnTabChangedListener {

    private final static int TAB_ALL = 0;
    private final static int TAB_MY = 1;

    private PurchaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchases);
        initDrawerToolbar(getString(R.string.joint_purchase));
        createDrawer();


        initTabLayout();
        initRecyclerView();
        getJointPurchasesRequest();
    }

    private void initRecyclerView() {
        adapter = new PurchaseAdapter(context);
        RecyclerView rvPurchases = findViewById(R.id.rv_purchases);
        rvPurchases.setLayoutManager(new LinearLayoutManager(context));
        rvPurchases.setAdapter(adapter);
    }

    private void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tab_l);
        tabLayout.setTabChangedListener(this);
        tabLayout.addTab(new Tab("Все", TAB_ALL));
        tabLayout.addTab(new Tab("Созданные мною", TAB_MY));
    }

    private void getJointPurchasesRequest(){
        ApiClient.getApiClient().getJointPurchases(token)
                .enqueue(new BaseCallback<ArrayList<JointPurchase>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<JointPurchase> result) {
                        if (code == 200) {
//                            purchases = result;
                            if (adapter != null) adapter.setPurchases(result);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BaseResponse<ArrayList<JointPurchase>>> call, @NonNull Throwable t) {
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

    private class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
        private Context context;
        private ArrayList<JointPurchase> purchases;


        public PurchaseAdapter(Context context) {
            this.context = context;
            viewBinderHelper.setOpenOnlyOne(true);
        }

        public void setPurchases(ArrayList<JointPurchase> purchases) {
            this.purchases = purchases;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_purchase, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(purchases.get(position));

            viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            if (purchases == null) return 0;
            return purchases.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            SwipeRevealLayout swipeRevealLayout;
            View llPurchase;
            View viewed;
            TextView tvTitle,
                    tvCreator,
                    tvPayTo,
                    tvAmount,
                    tvDelete;



            ViewHolder(View itemView) {
                super(itemView);
                swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
                llPurchase = itemView.findViewById(R.id.ll_purchase);
                viewed = itemView.findViewById(R.id.viewed);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvCreator = itemView.findViewById(R.id.tv_creator);
                tvPayTo = itemView.findViewById(R.id.tv_pay_to);
                tvAmount = itemView.findViewById(R.id.tv_amount);
                tvDelete = itemView.findViewById(R.id.tv_delete);

//                llPurchase.setOnClickListener((View.OnClickListener) context);
//                swipeRevealLayout.setOnClickListener((View.OnClickListener) context);
            }

            public void bind(JointPurchase purchase) {
                tvTitle.setText(purchase.getTitle());
                tvCreator.setText(purchase.getCreatorName());
                tvPayTo.setText(purchase.getTo());
                tvAmount.setText(String.format(Locale.ENGLISH,"%.2f", (((float) ((float) purchase.getAmountToPay() / 100f)))));
//                llPurchase.setTag(purchase);
//                tvDelete.setTag(purchase);
            }
        }
    }
}
