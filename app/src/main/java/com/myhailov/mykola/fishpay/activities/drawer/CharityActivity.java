package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.adapters.CharityAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.CharityResult;
import com.myhailov.mykola.fishpay.api.results.CharityResult.CharityProgram;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CharityActivity extends DrawerActivity implements TabLayout.OnTabChangedListener {
    private final int TAB_GLOBAL = 0;
    private final int TAB_CONTACT = 1;
    private final int TAB_MY = 2;


    private TabLayout tabLayout;
    private RecyclerView rvCharity;
    private CharityAdapter adapter;
    private TextView tvContributions;
    private TextView tvDescription;

    private List<CharityProgram> charities;
    private List<CharityProgram> selectedCharities = new ArrayList<>();
    private int tabPosition;
    private long myId;
    private String filterQuery = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity);

        initDrawerToolbar(getString(R.string.charity));
        createDrawer();
        assignViews();
        initRecyclerView();
        initTabLayout();
        initSearchView();

        myId = getMyId();

        getCharity();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_report:
                toast("Вы пожаловались");
                break;
            case R.id.charity_item:

                break;
        }

    }

    private void assignViews() {
        tabLayout = findViewById(R.id.tab_layout_activity_сharity);
        rvCharity = findViewById(R.id.rv_charity);
        tvContributions = findViewById(R.id.tv_contributions);
        tvDescription = findViewById(R.id.tv_description);

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
        adapter = new CharityAdapter(context, charities);
        rvCharity.setAdapter(adapter);

    }

    private void initSearchView() {
        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterQuery = query;
                filter();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterQuery = newText;
                filter();
                return true;
            }
        });
        searchView.setQueryHint("Введите имя или название");
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    }

    private void filter() {
        List<CharityProgram> filteredCharity = new ArrayList<>();
        if (filterQuery == null || filterQuery.equals("")) {
            adapter.setList(charities);
            return;
        }
        String search = filterQuery.toLowerCase();
        for (CharityProgram item : selectedCharities) {
            String name = item.getAuthorName().toLowerCase();
            String title = item.getTitle().toLowerCase();
            if (name.contains(search) || title.contains(search)) {
                filteredCharity.add(item);
            }
        }
        adapter.setList(filteredCharity);
    }

    private void getCharity() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().getCharity(TokenStorage.getToken(context))
                    .enqueue(new BaseCallback<CharityResult>(this, true) {
                        @Override
                        protected void onResult(int code, CharityResult result) {
                            if (code == 200) {
                                if (result == null) return;
                                tvContributions.setText(result.getTotalDonation().toString());
                                charities = result.getCharityProgram();
                                adapter.setList(charities);
                            } else if (code == 404) {

                            }
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
                selectedCharities = charities;
                changeState(TAB_GLOBAL, selectedCharities);
                break;
            case TAB_CONTACT:
                selectedCharities = getContactsCharity();
                changeState(TAB_CONTACT, selectedCharities);
                break;
            case TAB_MY:
                selectedCharities = getMyCharity();
                changeState(TAB_MY, selectedCharities);
                break;
        }
    }

    private List<CharityProgram> getContactsCharity() {
        List<CharityProgram> list = new ArrayList<>();

        for (CharityProgram charity : charities) {
            if (charity.isContact()) {
                list.add(charity);
            }
        }
        return list;
    }

    private List<CharityProgram> getMyCharity() {
        List<CharityProgram> list = new ArrayList<>();
        for (CharityProgram charity : charities) {
            if (myId == charity.getUserId()) {
                list.add(charity);
            }
        }

        return list;
    }

    private void changeState(int tab, List<CharityProgram> list){
        if (list.size() > 0){
            rvCharity.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.GONE);
            adapter.setList(list);
        }else {
            rvCharity.setVisibility(View.GONE);
            switch (tab) {
                case TAB_GLOBAL:
                    tvDescription.setText(getString(R.string.charity_empty_global));
                    break;
                case TAB_CONTACT:
                    tvDescription.setText(getString(R.string.charity_empty_contacts));
                    break;
                case TAB_MY:
                    tvDescription.setText(getString(R.string.charity_empty_my));
                    break;
            }
            tvDescription.setVisibility(View.VISIBLE);
        }

    }


}
