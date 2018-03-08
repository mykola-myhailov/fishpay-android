package com.myhailov.mykola.fishpay.activities.drawer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.PayRequest;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.myhailov.mykola.fishpay.utils.Utils.pennyToUah;

public class PayRequestActivity extends DrawerActivity implements TabLayout.OnTabChangedListener {

    private final int TAB_INCOMING = 0;
    private final int TAB_OUTCOMING = 1;

    private TabLayout tabLayout;
    private RecyclerView rvRequests;
    private ProgressBar progressBar;
    private TextView tvPlaceholder;

    private ArrayList<PayRequest> currentList;

    private Call<BaseResponse<ArrayList<PayRequest>>> callIncoming;
    private Call<BaseResponse<ArrayList<PayRequest>>> callOutcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_request);

        initDrawerToolbar(getString(R.string.pay_request));
        createDrawer();
        assignViews();

        currentList = new ArrayList<>();
        initRecyclerView();
        initTabLayout();
        getIncomingRequests();
    }

    private void assignViews() {
        tabLayout = findViewById(R.id.tab_layout_activity_pay_request);
        rvRequests = findViewById(R.id.rv_activity_pay_request_list);
        progressBar = findViewById(R.id.progress_bar);
        tvPlaceholder = findViewById(R.id.tv_placeholder);
    }

    private void initRecyclerView() {
        rvRequests.setLayoutManager(new LinearLayoutManager(context));
        rvRequests.setAdapter(new PayRequestsAdapter());
    }

    private void initTabLayout() {
        tabLayout.addTab(new Tab("Входящие", TAB_INCOMING));
        tabLayout.addTab(new Tab("Исходящие", TAB_OUTCOMING));
        tabLayout.setTabChangedListener(this);
        tabLayout.setTabSelectedAt(TAB_INCOMING);
    }

    private void getIncomingRequests() {
        if (callOutcoming != null) callOutcoming.cancel();
        stateIs(State.LOADING);
        callIncoming = ApiClient.getApiClient().getIncomingPayRequests(TokenStorage.getToken(context));
        callIncoming.enqueue(getCallback());
    }

    private void getOutcomingRequests() {
        if (callIncoming != null) callIncoming.cancel();
        stateIs(State.LOADING);
        callOutcoming = ApiClient.getApiClient().getOutcomingPayRequests(TokenStorage.getToken(context));
        callOutcoming.enqueue(getCallback());
    }

    @NonNull
    private Callback<BaseResponse<ArrayList<PayRequest>>> getCallback() {
        return new Callback<BaseResponse<ArrayList<PayRequest>>>() {
            @Override
            public void onResponse(Call<BaseResponse<ArrayList<PayRequest>>> call,
                                   Response<BaseResponse<ArrayList<PayRequest>>> response) {
                if (response.code() == 200) {
                    currentList = response.body().getResult();
                    rvRequests.getAdapter().notifyDataSetChanged();
                    stateIs(currentList.size() == 0 ? State.EMPTY_LIST : State.LOADED);
                } else if (response.code() == 404) stateIs(State.EMPTY_LIST);
            }

            @Override
            public void onFailure(Call<BaseResponse<ArrayList<PayRequest>>> call, Throwable t) {

            }
        };
    }

    private void stateIs(State state) {
        progressBar.setVisibility(state == State.LOADING ? VISIBLE : GONE);
        tvPlaceholder.setVisibility(state == State.EMPTY_LIST ? VISIBLE : GONE);
        rvRequests.setVisibility(state == State.LOADED ? VISIBLE : GONE);
        if (state == State.EMPTY_LIST) {
            tvPlaceholder.setText(tabLayout.getCurrentTab() == TAB_INCOMING
                    ? R.string.you_have_no_incoming_requests : R.string.you_have_no_outcoming_requests);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    public void onTabChanged(int position) {
        switch (position) {
            case TAB_INCOMING:
                getIncomingRequests();
                break;
            case TAB_OUTCOMING:
                getOutcomingRequests();
                break;
        }
    }

    private class PayRequestsAdapter extends RecyclerView.Adapter<PayRequestsAdapter.PayRequestHolder> {

        @Override
        public PayRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PayRequestHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_pay_request, parent, false));
        }

        @Override
        public void onBindViewHolder(PayRequestHolder holder, int position) {
            holder.bind(currentList.get(position));
        }

        @Override
        public int getItemCount() {
            return currentList.size();
        }

        class PayRequestHolder extends RecyclerView.ViewHolder {

            View rlPayRequest;
            TextView tvName;
            TextView tvAmount;
            TextView tvStatus;
            TextView tvTime;

            PayRequestHolder(View itemView) {
                super(itemView);
                rlPayRequest = itemView.findViewById(R.id.rl_pay_request);
                tvName = itemView.findViewById(R.id.tv_name);
                tvAmount = itemView.findViewById(R.id.tv_amount);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvTime = itemView.findViewById(R.id.tv_time);
                rlPayRequest.setOnClickListener((View.OnClickListener) context);
            }

            void bind(PayRequest request) {
                tvName.setText(request.getFullName());
                tvAmount.setText(pennyToUah(request.getAmount()));
                tvStatus.setText(request.getStatus());
                tvTime.setText(request.getCreatingTime());
                rlPayRequest.setTag(request);
            }
        }
    }

    private enum State {
        LOADING,
        LOADED,
        EMPTY_LIST
    }
}
