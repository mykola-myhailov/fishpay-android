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
import com.myhailov.mykola.fishpay.utils.TokenStorage;

import java.util.ArrayList;

public class GroupSpendsActivity extends DrawerActivity {

    private ArrayList<GroupSpend> groupSpends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_group_spends);
        setContentView(R.layout.activity_group_spends);

        initDrawerToolbar(getString(R.string.joint_costs));
        createDrawer();

        groupSpendsRequest();
    }

    private void groupSpendsRequest() {
        ApiClient.getApiClient().getSpending(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GroupSpend>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GroupSpend> result) {
                        if (code == 200){
                            groupSpends = result;
                            initRecyclerView();
                        }
                    }
                });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new SpendsAdapter());
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
