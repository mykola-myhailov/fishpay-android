package com.myhailov.mykola.fishpay.activities.drawer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.adapters.CharityAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.CharityResult;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharityActivity extends DrawerActivity implements TabLayout.OnTabChangedListener {
    private final int TAB_GLOBAL = 0;
    private final int TAB_CONTACT = 1;
    private final int TAB_MY = 2;

    private TabLayout tabLayout;
    private RecyclerView rvCharity;
    private CharityAdapter adapter;
    private TextView tvContributions;

    private List<CharityResult.CharityProgram> charities;
    private int tabPosition;
    private long myId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity);

        initDrawerToolbar(getString(R.string.charity));
        createDrawer();
        assignViews();
        initRecyclerView();
        initTabLayout();

        myId = getMyId();

        getCharity();

    }

    @Override
    public void onClick(View view) {

    }

    private void assignViews() {
        tabLayout = findViewById(R.id.tab_layout_activity_сharity);
        rvCharity = findViewById(R.id.rv_charity);
        tvContributions = findViewById(R.id.tv_contributions);

    }

    private void initTabLayout() {
        tabLayout.addTab(new Tab("Глобальный", TAB_GLOBAL));
        tabLayout.addTab(new Tab("Контакты", TAB_CONTACT));
        tabLayout.addTab(new Tab("Мои", TAB_MY));
        tabLayout.setTabChangedListener(this);
        tabLayout.setTabSelectedAt(TAB_GLOBAL);
    }

    private void initRecyclerView() {
        rvCharity.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CharityAdapter(CharityActivity.this, charities);
        rvCharity.setAdapter(adapter);
    }

    private void getCharity() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().getCharity(TokenStorage.getToken(context))
                    .enqueue(new Callback<BaseResponse<CharityResult>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<CharityResult>> call,
                                               Response<BaseResponse<CharityResult>> response) {
                            if (response.code() == 200) {
                                BaseResponse<CharityResult> body = response.body();
                                if (body == null) return;
                                tvContributions.setText(body.getResult().getTotalDonation().toString());
                                charities = body.getResult().getCharityProgram();
                                adapter.setList(charities);
                                adapter.notifyDataSetChanged();
                            } else if (response.code() == 404) {

                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<CharityResult>> call, Throwable t) {
                            Log.d("sss", "onFailure: " + t);
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private long getMyId() {
        return Long.valueOf(context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, ""));
    }


    @Override
    public void onTabChanged(int position) {
        tabPosition = position;
        switch (position) {
            case TAB_GLOBAL:
                adapter.setList(charities);
                break;
            case TAB_CONTACT:
                adapter.setList(getContactsCharity());
                break;
            case TAB_MY:
                adapter.setList(getMyCharity());
                break;
        }
    }

    private  List<CharityResult.CharityProgram> getContactsCharity(){
        List<CharityResult.CharityProgram> list = new ArrayList<>();

        for (CharityResult.CharityProgram charity : charities) {
            if (charity.isContact()){
                list.add(charity);
            }
        }

        return list;
    }

    private List<CharityResult.CharityProgram>  getMyCharity(){
        List<CharityResult.CharityProgram> list = new ArrayList<>();
        for (CharityResult.CharityProgram charity : charities) {
            if (myId == charity.getUserId()) {
                list.add(charity);
            }
        }

        return list;
    }


}
