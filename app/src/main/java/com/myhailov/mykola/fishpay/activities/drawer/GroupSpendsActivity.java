package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.group_spends.CreateGroupSpendActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;

import java.util.ArrayList;

public class GroupSpendsActivity extends DrawerActivity implements TabLayout.OnTabChangedListener  {

    private ArrayList<GroupSpend> allSpends = new ArrayList<>(), mySpends = new ArrayList<>();

    private final static int TAB_ALL = 0;
    private final static int TAB_MY = 1;
    private ArrayList<GroupSpend> spendsList;
    private RecyclerView rvSpends;
    private SpendsAdapter spendsAdapter;
    private long myId;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_group_spends);
        setContentView(R.layout.activity_group_spends);

        initDrawerToolbar(getString(R.string.joint_costs));
        createDrawer();

        myId = Long.valueOf(context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, ""));

        rvSpends = findViewById(R.id.recyclerView);
        rvSpends.setLayoutManager(new LinearLayoutManager(context));

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
                        if (code == 200){
                            allSpends = result;
                            for (GroupSpend spend : allSpends) {
                                if (spend.getCreatorId() == myId){
                                    mySpends.add(spend);
                                }
                            }
                        }
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
                setSpendsList(mySpends);
                break;
        }
    }

    public void setSpendsList(ArrayList<GroupSpend> spendsList) {

    }


    @Override
    public void onClick(View view) {

    }


    private class SpendsViewHolder extends RecyclerView.ViewHolder {
        public SpendsViewHolder(View itemView) {
            super(itemView);
        }
    }


    private class
    SpendsAdapter extends RecyclerView.Adapter<SpendsViewHolder> {

        @Override
        public SpendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SpendsViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_group_spends, parent, false));
        }

        @Override
        public void onBindViewHolder(SpendsViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
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
