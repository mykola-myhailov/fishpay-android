package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.charity.CharityDetailsActivity;
import com.myhailov.mykola.fishpay.activities.charity.CharityListActivity;
import com.myhailov.mykola.fishpay.activities.charity.CreateCharityActivity;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_AMOUNT;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_LIST;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_MEMBERS_VISIBILITY;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_USER_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_VISIBILITY;

public class CharityActivity extends DrawerActivity implements TabLayout.OnTabChangedListener {
    private final int TAB_GLOBAL = 0;
    private final int TAB_CONTACT = 1;
    private final int TAB_MY = 2;


    private TabLayout tabLayout;
    private RecyclerView rvCharity;
    private TextView tvContributions;
    private TextView tvDescription;

    private List<CharityResult.Donation> donations = new ArrayList<>();
    private List<CharityProgram> charities;
    private List<CharityProgram> selectedCharities = new ArrayList<>();
    private long myId;
    private String filterQuery = "";
    private int amount;


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
            case R.id.iv_menu:
                Intent intent = new Intent(this, CharityListActivity.class);
                intent.putExtra(CHARITY_LIST, (ArrayList) donations);
                intent.putExtra(CHARITY_AMOUNT, amount);
                startActivity(intent);
                break;
            case R.id.ivPlus:
                startActivity(new Intent(context, CreateCharityActivity.class));
                break;
        }

    }

    @Override
    public void onTabChanged(int position) {
        switch (position) {
            case TAB_GLOBAL:
                selectedCharities = getGlobalCharity();
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

    private void assignViews() {
        tabLayout = findViewById(R.id.tab_layout_activity_сharity);
        rvCharity = findViewById(R.id.rv_charity);
        tvContributions = findViewById(R.id.tv_contributions);
        tvDescription = findViewById(R.id.tv_description);
        findViewById(R.id.iv_menu).setOnClickListener(this);
        findViewById(R.id.ivPlus).setOnClickListener(this);
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
        rvCharity.setAdapter(new CharityAdapter(context, charities, rvListener));

    }

    private CharityAdapter.OnItemClickListener rvListener = new CharityAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(String id, CharityProgram item) {
            Intent intent = new Intent(context, CharityDetailsActivity.class);
            intent.putExtra(CHARITY_ID, id);
            intent.putExtra(CHARITY_USER_ID, myId);
            intent.putExtra(CHARITY_MEMBERS_VISIBILITY, item.getMembersVisibility());
            intent.putExtra(CHARITY_VISIBILITY, item.getItemVisibility());
            context.startActivity(intent);
        }
    };

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
            rvCharity.setAdapter(new CharityAdapter(context, charities, rvListener));
            return;
        }
        String search = filterQuery.toLowerCase();
        for (CharityProgram item : selectedCharities) {
            String name = item.getAuthorName().toLowerCase();
            String title = item.getTitle().toLowerCase();
            String pseudonym = "";
            if (!TextUtils.isEmpty(item.getPseudonym())){
                pseudonym  = item.getPseudonym();
            }
            if (name.contains(search) || title.contains(search) || pseudonym.contains(search)) {
                filteredCharity.add(item);
            }
        }
        rvCharity.setAdapter(new CharityAdapter(context, filteredCharity, rvListener));
    }

    private void getCharity() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().getCharity(TokenStorage.getToken(context))
                    .enqueue(new BaseCallback<CharityResult>(this, true) {
                        @Override
                        protected void onResult(int code, CharityResult result) {
                                if (result == null) return;
                                setValue(result);
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private void setValue(CharityResult result) {
        donations = result.getDonation();
        amount = result.getTotalDonation();
        String contributions = Utils.pennyToUah(result.getTotalDonation());
        if (contributions.equals("0")) contributions = "0.00";
        tvContributions.setText(contributions);
        charities = result.getCharityProgram();
        Collections.sort(charities, new Comparator<CharityProgram>() {
            @Override
            public int compare(CharityProgram o1, CharityProgram o2) {
                if (o1.getCreatedAt() == null || o2.getCreatedAt() == null) return 0;
                return o2.getCreatedAt().compareTo(o1.getCreatedAt());

            }
        });
        selectedCharities = charities;
        rvCharity.setAdapter(new CharityAdapter(context, charities, rvListener));
    }

    private long getMyId() {
        return Long.valueOf(context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, ""));
    }

    private List<CharityProgram> getGlobalCharity() {
        List<CharityProgram> list = new ArrayList<>();
        for (CharityProgram charity : charities) {
            if (charity.getItemVisibility().equals("PUBLIC")) {
                list.add(charity);
            }
        }
        return list;
    }


    private List<CharityProgram> getContactsCharity() {
        List<CharityProgram> list = new ArrayList<>();

        for (CharityProgram charity : charities) {
            if (charity.isContact() && !charity.getItemVisibility().equals("AUTHOR")) {
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

    private void changeState(int tab, List<CharityProgram> list) {
        if (list.size() > 0) {
            rvCharity.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.GONE);
            rvCharity.setAdapter(new CharityAdapter(context, list, rvListener));
        } else {
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
