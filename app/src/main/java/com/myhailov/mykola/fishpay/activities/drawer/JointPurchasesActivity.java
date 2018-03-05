package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.myhailov.mykola.fishpay.activities.joint_purchases.JointPurchaseDetailsActivity;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
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

import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE;
import static com.myhailov.mykola.fishpay.utils.Utils.pennyToUah;

public class JointPurchasesActivity extends DrawerActivity implements TabLayout.OnTabChangedListener {

    private final static int TAB_ALL = 0;
    private final static int TAB_MY = 1;

    private String id;
    private TabLayout tabLayout;

    private ArrayList<JointPurchase> allPurchases, myPurchases;
    private PurchaseAdapter adapter;
    private View tvPlaceholder;
    private View rvPurchases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchases);
        initDrawerToolbar(getString(R.string.joint_purchase));
        createDrawer();
        id = context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, "");

        tvPlaceholder = findViewById(R.id.tv_placeholder);
        rvPurchases = findViewById(R.id.rv_purchases);
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
        tabLayout = findViewById(R.id.tab_l);
        tabLayout.setTabChangedListener(this);
        tabLayout.addTab(new Tab("Все", TAB_ALL));
        tabLayout.addTab(new Tab("Созданные мною", TAB_MY));
    }

    private void getJointPurchasesRequest(){
        allPurchases = new ArrayList<>();
        myPurchases = new ArrayList<>();
        ApiClient.getApiClient().getJointPurchases(token)
                .enqueue(new BaseCallback<ArrayList<JointPurchase>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<JointPurchase> result) {
                        if (code == 200) {
                            allPurchases = result;
                            for (JointPurchase purchase : allPurchases) {
                                if (id.equals(purchase.getCreatorId())) myPurchases.add(purchase);
                            }
                            setPurchasesList(tabLayout.getCurrentTab() == 0 ? allPurchases : myPurchases);
                        }
                    }

                    @Override
                    protected void onError(int code, String errorDescription) {
                        if (code == 404) {
                            setPurchasesList(new ArrayList<JointPurchase>());
                        } else super.onError(code, errorDescription);
                    }
                });


    }

    private void setPurchasesList(ArrayList<JointPurchase> list) {
        if (adapter != null) {
            adapter.setPurchases(list);
            if (adapter.getItemCount() == 0) {
                rvPurchases.setVisibility(View.GONE);
                tvPlaceholder.setVisibility(View.VISIBLE);
            } else {
                rvPurchases.setVisibility(View.VISIBLE);
                tvPlaceholder.setVisibility(View.GONE);
            }
        }
    }

    private void deletePurchaseRequest(String purchaseId) {
        ApiClient.getApiClient().deleteJointPurchase(token, purchaseId)
                .enqueue(new BaseCallback<Object>(context, false) {
                    @Override
                    protected void onResult(int code, Object result) {
                        getJointPurchasesRequest();
                    }
                });
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
        switch (view.getId()) {
            case R.id.tv_delete:
                String id = ((JointPurchase) view.getTag()).getId();
                showConfirmation(id);
                break;
            case R.id.ll_purchase:
                startActivity(new Intent(context, JointPurchaseDetailsActivity.class)
                        .putExtra(PURCHASE, (JointPurchase) view.getTag())
                );
                break;
        }
    }

    private void showConfirmation(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Вы действительно хотите общую покупку из списка?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePurchaseRequest(id);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }

    @Override
    public void onTabChanged(int position) {
        switch (position) {
            case TAB_ALL:
                setPurchasesList(allPurchases);
                break;
            case TAB_MY:
                setPurchasesList(myPurchases);
                break;
        }
    }

    private class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
        private Context context;
        private ArrayList<JointPurchase> purchases;

        PurchaseAdapter(Context context) {
            this.context = context;
            viewBinderHelper.setOpenOnlyOne(true);
        }

        void setPurchases(ArrayList<JointPurchase> purchases) {
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

            viewBinderHelper.bind(holder.swipeRevealLayout,
                    purchases.get(position).getId());
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

                llPurchase.setOnClickListener((View.OnClickListener) context);
                tvDelete.setOnClickListener((View.OnClickListener) context);
            }

            void bind(JointPurchase purchase) {
                if (purchase.getStatus().equals("NOT_VIEWED")) viewed.setVisibility(View.VISIBLE);
                else viewed.setVisibility(View.INVISIBLE);
                if (purchase.getStatus().equals("CLOSED") || purchase.getStatus().equals("REJECTED"))
                    tvTitle.setTextColor(getResources().getColor(R.color.grey2));
                else tvTitle.setTextColor(getResources().getColor(R.color.black_light));
                tvTitle.setText(purchase.getTitle());
                tvCreator.setText(purchase.getCreatorName());
                tvPayTo.setText(purchase.getTo());
                tvAmount.setText(pennyToUah(((float) purchase.getAmountToPay())));
                llPurchase.setTag(purchase);
                tvDelete.setTag(purchase);
            }
        }
    }
}
