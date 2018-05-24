package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.group_spends.CreateGroupSpendActivity;
import com.myhailov.mykola.fishpay.activities.group_spends.SpendDetailActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;

import java.util.ArrayList;

public class GroupSpendsActivity extends DrawerActivity implements TabLayout.OnTabChangedListener  {

    private final static int TAB_ALL = 0;
    private final static int TAB_MY = 1;
    private ArrayList<GroupSpend> selectedSpends = new ArrayList<>(),
            allSpends = new ArrayList<>(),
            myCreationSpends = new ArrayList<>();
    private RecyclerView rvSpends;
    private SpendsAdapter spendsAdapter = new SpendsAdapter();
    private long myId;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        setContentView(R.layout.activity_group_spends);

        initDrawerToolbar(getString(R.string.joint_costs));
        createDrawer();

        myId = Long.valueOf(context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, ""));

        rvSpends = findViewById(R.id.recyclerView);
        rvSpends.setLayoutManager(new LinearLayoutManager(context));
        initTabLayout();
        groupSpendsRequest();
    }

    private void initTabLayout() {
        tabLayout = findViewById(R.id.tab_l);
        tabLayout.setTabChangedListener(this);
        tabLayout.addTab(new Tab("Все", TAB_ALL));
        tabLayout.addTab(new Tab("Созданные мною", TAB_MY));
    }

    private void groupSpendsRequest() {
        ApiClient.getApiClient().getSpending(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GroupSpend>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GroupSpend> result) {

                        if (code < 204){
                            allSpends = result;
                            for (GroupSpend spend : allSpends)
                                if (spend.getCreatorId() == myId)
                                    myCreationSpends.add(spend);

                            selectedSpends = allSpends;
                            spendsAdapter = new SpendsAdapter();
                            rvSpends.setAdapter(spendsAdapter);
                            Log.e("spends", allSpends.size() + "");
                        } else if (code == 244) allSpends = new ArrayList<>();
                    }
                });
    }

    @Override
    public void onTabChanged(int position) {
        switch (position) {
            case TAB_ALL:
                setSpendsList(allSpends);
                break;
            case TAB_MY:
                setSpendsList(myCreationSpends);
                break;
        }
    }

    public void setSpendsList(ArrayList<GroupSpend> spendsList) {
        selectedSpends = spendsList;
       rvSpends.setAdapter(new SpendsAdapter());
    }


    @Override
    public void onClick(View view) {
        GroupSpend spend = (GroupSpend) view.getTag();
        long spendId = spend.getId();
        switch (view.getId()){
            case R.id.ll_main_item:
               context.startActivity(new Intent(context, SpendDetailActivity.class)
                .putExtra(Keys.SPEND_ID, spendId));
                break;
            case R.id.tv_delete:
                deleteGroupSpendRequest();
                break;
        }
    }

    private void deleteGroupSpendRequest() {
        //TODO:
    }


    private class SpendsViewHolder extends RecyclerView.ViewHolder {

        SwipeRevealLayout swipeRevealLayout;
        View llMainItem;
        View viewed;
        TextView tvTitle, tvCreator, tvPart, tvDelete, tvAmount;

        public SpendsViewHolder(View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            llMainItem = itemView.findViewById(R.id.ll_main_item);
            viewed = itemView.findViewById(R.id.viewed);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCreator = itemView.findViewById(R.id.tv_creator);
            tvPart = itemView.findViewById(R.id.tv_part);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            llMainItem.setOnClickListener((View.OnClickListener) context);
            tvDelete.setOnClickListener((View.OnClickListener) context);
        }
    }


    private class SpendsAdapter extends RecyclerView.Adapter<SpendsViewHolder> {


        @Override
        public SpendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SpendsViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_group_spends, parent, false));
        }

        @Override
        public void onBindViewHolder(SpendsViewHolder holder, int position) {
            GroupSpend spend = selectedSpends.get(position);
            if (spend.getStatus().equals("NOT_VIEWED")) holder.viewed.setVisibility(View.VISIBLE);
            else holder.viewed.setVisibility(View.INVISIBLE);
            if (spend.getStatus().equals("CLOSED") || spend.getStatus().equals("REJECTED"))
                holder.tvTitle.setTextColor(getResources().getColor(R.color.grey2));
            else holder.tvTitle.setTextColor(getResources().getColor(R.color.black_light));
            holder.tvTitle.setText(spend.getTitle());
            holder.tvCreator.setText(spend.getCreatorName());
            holder.tvPart.setText(spend.getMemberPart() + "%");
            holder.tvAmount.setText(Utils.pennyToUah(spend.getStartAmount()) + " | грн");
            holder.llMainItem.setTag(spend);
            holder.tvDelete.setTag(spend);
        }

        @Override
        public int getItemCount() { return selectedSpends.size(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        context.startActivity(new Intent(context, CreateGroupSpendActivity.class));
        return true;
    }
}
