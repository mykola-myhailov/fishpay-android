package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.drawer.ActivityActivity;
import com.myhailov.mykola.fishpay.activities.drawer.CharityActivity;
import com.myhailov.mykola.fishpay.activities.drawer.ContactsActivity;
import com.myhailov.mykola.fishpay.activities.drawer.GroupSpendsActivity;
import com.myhailov.mykola.fishpay.activities.drawer.JointPurchasesActivity;
import com.myhailov.mykola.fishpay.activities.drawer.MyGoodsActivity;
import com.myhailov.mykola.fishpay.activities.drawer.PayRequestActivity;
import com.myhailov.mykola.fishpay.activities.drawer.ProfileSettingsActivity;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = TokenStorage.getToken(context);
        preferences = context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE);
        extras = getIntent().getExtras();
    }

    protected void createDrawer(){
        if (nameInDrawer == null || phoneInDrawer == null) loadDrawerData();
        navClickListener = createNavClickListener();
        initNavigationViews();
        updateDrawerHeader();
    }

    private View.OnClickListener createNavClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isOnline(context)){
                    Class<?> nextActivityClass = null;
                    switch (view.getId()){
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
    }

    protected void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    protected void initToolbar(String title) {
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

    protected void setToolBarTitle(String title){
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
}
