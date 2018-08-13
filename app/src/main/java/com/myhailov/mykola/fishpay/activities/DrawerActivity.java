package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.group_spends.SpendDetailActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class DrawerActivity extends BaseActivity {

    protected String token;
    protected SharedPreferences preferences;
    private Bundle extras;
    private View.OnClickListener navClickListener;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private TextView tvNameInDrawer, tvPhoneInDrawer;
    private ImageView ivAvatarInDrawer;

    private String nameInDrawer, surnameInDrawer, phoneInDrawer, avatarInDrawer;
    private GroupSpend groupSpend1, groupSpend2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = TokenStorage.getToken(context);
        preferences = context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
        extras = getIntent().getExtras();
    }

    protected void createDrawer() {
        if (nameInDrawer == null || phoneInDrawer == null) loadDrawerData();
        navClickListener = createNavClickListener();
        initNavigationViews();
        updateDrawerHeader();
        getSpends();
    }

    private View.OnClickListener createNavClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isOnline(context)) {
                    Class<?> nextActivityClass = null;
                    switch (view.getId()) {
                       /* case R.id.tvDrawerName:
                        case R.id.tvDrawerPhone:
                        case R.id.ivAvatar:*/
                        case R.id.navProfile:
                            nextActivityClass = ProfileSettingsActivity.class;
                            break;
                        case R.id.navContacts:
                            nextActivityClass = ContactsActivity.class;
                            break;
                        case R.id.navPayRequest:
                            nextActivityClass = PayRequestActivity.class;
                            break;
                        case R.id.navJointPurchases:
                            nextActivityClass = JointPurchasesActivity.class;
                            break;
                        case R.id.navMyPurchases:
                            nextActivityClass = MyGoodsActivity.class;
                            break;
                        case R.id.navCharity:
                            nextActivityClass = CharityActivity.class;
                            break;
                        case R.id.navActivity:
                            nextActivityClass = ActivityActivity.class;
                            break;
                        case R.id.navGroupSpends:
                            nextActivityClass = GroupSpendsActivity.class;
                            break;
                        case R.id.ll_send_request:
                            nextActivityClass = TransactionActivity.class;
                            break;
//                        case R.id.ll_pay:
//                            showInfoNotSuportedAlert(context);
//                            break;
                        case R.id.ll_about_us:
                            nextActivityClass = AboutUsActivity.class;
                            break;

                        case R.id.navGroupSpendsItem1:
                            startActivity(new Intent(context, SpendDetailActivity.class)
                                    .putExtra(Keys.SPEND, groupSpend1));
                            break;
                        case R.id.navGroupSpendsItem2:
                            startActivity(new Intent(context, SpendDetailActivity.class)
                                    .putExtra(Keys.SPEND, groupSpend2));
                            break;
                    }
                    if (nextActivityClass != null/* && !(nextActivityClass.equals(context.getClass()) )*/)
                        startActivity(new Intent(context, nextActivityClass));
                } else Utils.noInternetToast(context);

            }
        };
    }

    private void initNavigationViews() {
        navigationView = findViewById(R.id.nav_view);

        tvNameInDrawer = navigationView.findViewById(R.id.tvDrawerName);
        tvPhoneInDrawer = navigationView.findViewById(R.id.tvDrawerPhone);
        ivAvatarInDrawer = navigationView.findViewById(R.id.ivDrawerAvatar);

        navigationView.findViewById(R.id.navProfile).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.navContacts).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.navPayRequest).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.navJointPurchases).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.navMyPurchases).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.navCharity).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.navActivity).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.navGroupSpends).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.ll_send_request).setOnClickListener(navClickListener);
//        navigationView.findViewById(R.id.ll_pay).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.ll_about_us).setOnClickListener(navClickListener);


        navigationView.findViewById(R.id.navGroupSpendsItem1).setOnClickListener(navClickListener);
        navigationView.findViewById(R.id.navGroupSpendsItem2).setOnClickListener(navClickListener);
    }

    protected void initDrawerToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ((TextView) findViewById(R.id.tvToolBarTitle)).setText(title.toUpperCase());
    }


    private void loadDrawerData() {
        nameInDrawer = preferences.getString(PrefKeys.NAME, "");
        surnameInDrawer = preferences.getString(PrefKeys.SURNAME, "");
        phoneInDrawer = "+" + preferences.getString(PrefKeys.PHONE, "");
        avatarInDrawer = preferences.getString(PrefKeys.AVATAR, "");
    }


    private void updateDrawerHeader() {
        String visibleName = nameInDrawer + " " + surnameInDrawer;
        String initials = Utils.extractInitials(nameInDrawer, surnameInDrawer);

        tvPhoneInDrawer.setText(phoneInDrawer);
        tvNameInDrawer.setText(visibleName);
        Utils.displayAvatar(context, ivAvatarInDrawer, avatarInDrawer, initials);
    }

    protected void setToolBarTitle(String title) {
        ((TextView) findViewById(R.id.tvToolBarTitle)).setText(title.toUpperCase());
    }


    protected void setNameInDrawer(String nameInDrawer) {
        this.nameInDrawer = nameInDrawer;
    }

    protected void setSurnameInDrawer(String surnameInDrawer) {
        this.surnameInDrawer = surnameInDrawer;
    }

    protected void setPhoneInDrawer(String phoneInDrawer) {
        this.phoneInDrawer = phoneInDrawer;
    }

    protected void setAvatarInDrawer(String avatarInDrawer) {
        this.avatarInDrawer = avatarInDrawer;
    }

    protected void openDrawer() {
        drawer.openDrawer(Gravity.LEFT);
    }

    protected void updateGroupSpends(GroupSpend item1, GroupSpend item2) {
        if (item1 != null) {
            navigationView.findViewById(R.id.navGroupSpendsItem1).setVisibility(View.VISIBLE);
            navigationView.findViewById(R.id.gs_item1).setVisibility(View.VISIBLE);
            ((TextView) navigationView.findViewById(R.id.tv_gs1)).setText(item1.getTitle());
        } else {
            navigationView.findViewById(R.id.navGroupSpendsItem1).setVisibility(View.GONE);
            navigationView.findViewById(R.id.gs_item1).setVisibility(View.GONE);
        }
        if (item2 != null) {
            navigationView.findViewById(R.id.navGroupSpendsItem2).setVisibility(View.VISIBLE);
            navigationView.findViewById(R.id.gs_item2).setVisibility(View.VISIBLE);
            ((TextView) navigationView.findViewById(R.id.tv_gs2)).setText(item2.getTitle());
        } else {
            navigationView.findViewById(R.id.navGroupSpendsItem2).setVisibility(View.GONE);
            navigationView.findViewById(R.id.gs_item2).setVisibility(View.GONE);
        }

    }

    private void getSpends() {
        ApiClient.getApiInterface().getSpending(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GroupSpend>>(context, false) {
                    @Override
                    protected void onResult(int code, ArrayList<GroupSpend> result) {
                        if (code < 204) {
                            Collections.sort(result, new Comparator<GroupSpend>() {
                                @Override
                                public int compare(GroupSpend o1, GroupSpend o2) {
                                    if (o1.getCreatedAt() == null || o2.getCreatedAt() == null)
                                        return 0;
                                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                                }
                            });
                            if (result.size() == 1) {
                                updateGroupSpends(result.get(0), null);
                                groupSpend1 = result.get(0);
                            }
                            else if (result.size() > 1) {
                                groupSpend1 = result.get(0);
                                groupSpend2 = result.get(1);
                                updateGroupSpends(groupSpend1, groupSpend2);
                            }

                        }
                    }
                });
    }
}
