package com.myhailov.mykola.fishpay.activities.drawer;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.CreatePayRequestActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.IncomingDetailsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.PayRequest;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
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
    private int tabPosition;

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
        callIncoming.enqueue(createPayRequestCallback());
    }

    private void getOutcomingRequests() {
        if (callIncoming != null) callIncoming.cancel();
        stateIs(State.LOADING);
        callOutcoming = ApiClient.getApiClient().getOutcomingPayRequests(TokenStorage.getToken(context));
        callOutcoming.enqueue(createPayRequestCallback());
    }

    @NonNull
    private Callback<BaseResponse<ArrayList<PayRequest>>> createPayRequestCallback() {
        return new Callback<BaseResponse<ArrayList<PayRequest>>>() {
            @Override
            public void onResponse(Call<BaseResponse<ArrayList<PayRequest>>> call,
                                   Response<BaseResponse<ArrayList<PayRequest>>> response) {
                if (response.code() == 200) {
                    BaseResponse<ArrayList<PayRequest>> body = response.body();
                    if (body == null)return;
                    currentList = body.getResult();
                    rvRequests.getAdapter().notifyDataSetChanged();
                    stateIs(currentList.size() == 0 ? State.EMPTY_LIST : State.LOADED);
                } else if (response.code() == 244) stateIs(State.EMPTY_LIST);
            }

            @Override
            public void onFailure(Call<BaseResponse<ArrayList<PayRequest>>> call, Throwable t) {
                stateIs(State.EMPTY_LIST);
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
        switch (view.getId()) {
            case R.id.tv_delete:
                deletePayRequest((PayRequest) view.getTag());
                break;
            case R.id.rl_pay_request:
                if (tabPosition == TAB_INCOMING){
                    PayRequest payRequest = (PayRequest) view.getTag();
                    long payRequestId = payRequest.getId();
                    context.startActivity((new Intent(context, IncomingDetailsActivity.class))
                            .putExtra(Keys.REQUEST_ID, payRequestId));
                }

        }
    }

    private void deletePayRequest(PayRequest request) {
        String id = preferences.getString(PrefKeys.ID, "");
        if (String.valueOf(request.getRequesterId()).equals(id)) {
            ApiClient.getApiClient().deleteInvoice(TokenStorage.getToken(context),
                    request.getId()).enqueue(new Callback<BaseResponse<Object>>() {
                @Override
                public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                        if (response.code() == 202) getOutcomingRequests();
                }

                @Override
                public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {

                }
            });
        } else {
            ApiClient.getApiClient().removeInvoice(TokenStorage.getToken(context),
                    request.getId()).enqueue(new Callback<BaseResponse<Object>>() {
                @Override
                public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                    if (response.code() == 202) getIncomingRequests();
                }

                @Override
                public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(context, CreatePayRequestActivity.class));
        return true;
    }

    @Override
    public void onTabChanged(int position) {
        tabPosition = position;
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

            View tvDelete;
            View rlPayRequest;
            View viewed;
            TextView tvName;
            TextView tvAmount;
            TextView tvStatus;
            TextView tvTime;

            PayRequestHolder(View itemView) {
                super(itemView);
                tvDelete = itemView.findViewById(R.id.tv_delete);
                rlPayRequest = itemView.findViewById(R.id.rl_pay_request);
                viewed = itemView.findViewById(R.id.viewed);
                tvName = itemView.findViewById(R.id.tv_name);
                tvAmount = itemView.findViewById(R.id.tv_amount);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvStatus = itemView.findViewById(R.id.tv_status);
            }

            void bind(PayRequest request) {
                viewed.setVisibility(request._getStatus().equals("ACTIVE") ? VISIBLE : View.INVISIBLE);
                tvName.setText(request.getFullName());
                tvAmount.setText(pennyToUah(request.getAmount()));
                tvStatus.setText("через: FISHPAY");
                // TODO: 12.03.18 format date
                tvTime.setText(request.getCreatingTime());
                if (request.getStatus().equals("REJECTED")){
                    
                }
                tvDelete.setTag(request);
                rlPayRequest.setTag(request);
                tvDelete.setOnClickListener((View.OnClickListener) context);
                rlPayRequest.setOnClickListener((View.OnClickListener) context);
            }
        }
    }

    private enum State {
        LOADING,
        LOADED,
        EMPTY_LIST
    }
}
