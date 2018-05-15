package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.adapters.CharityAdapter;
import com.myhailov.mykola.fishpay.adapters.CharityDetailContactsAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.CharityResultById;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_MEMBERS_VISIBILITY;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_RESULT;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_USER_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_VISIBILITY;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;

public class CharityDetailsActivity extends BaseActivity implements TabLayout.OnTabChangedListener {
    private final int TAB_DESCRIPTION = 0;
    private final int TAB_CONTACT = 1;

    private ConstraintLayout container;
    private TabLayout tabLayout;

    private CharityAdapter adapter;
    private TextView tvTitle;
    private TextView tvAuthor;
    private ImageView ivSettings;

    private String charityId = "-1";
    private long userId = -1;
    private String charityVisibility = new String();
    private String membersVisibility;
    private CharityResultById charity = new CharityResultById();
    private Card card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_details);
        initCustomToolbar("Стихийное бедствие");
        if (getIntent() != null) {
            charityId = getIntent().getStringExtra(CHARITY_ID);
            userId = getIntent().getLongExtra(CHARITY_USER_ID, -1);
            charityVisibility = getIntent().getStringExtra(CHARITY_VISIBILITY);
            membersVisibility = getIntent().getStringExtra(CHARITY_MEMBERS_VISIBILITY);
        }

        assignViews();
        initTabLayout();
        getCharityById();
    }

    private void assignViews() {
        tabLayout = findViewById(R.id.tab_layout_activity_сharity);
        tvTitle = findViewById(R.id.tv_title);
        tvAuthor = findViewById(R.id.tv_author);
        container = findViewById(R.id.container_charity);
        ivSettings = findViewById(R.id.iv_settings);
        findViewById(R.id.tv_contribution).setOnClickListener(this);
        findViewById(R.id.iv_settings).setOnClickListener(this);

    }

    private void initTabLayout() {
        tabLayout.addTab(new Tab("Описание", TAB_DESCRIPTION));
        tabLayout.addTab(new Tab("Контакты", TAB_CONTACT));
        tabLayout.setTabChangedListener(this);
        tabLayout.setTabSelectedAt(TAB_DESCRIPTION);

    }

    private void initView() {
        tvAuthor.setText(charity.getAuthorName());
        tvTitle.setText(charity.getTitle());
        String[] name = charity.getAuthorName().split(" ");
        String firstName = "";
        String lastName = "";
        if (name.length > 0 && !TextUtils.isEmpty(name[0])) {
            firstName = name[0];
        }
        if (name.length > 1 && !TextUtils.isEmpty(name[1])) {
            lastName = name[1];
        }
        String initials = Utils.extractInitials(firstName, lastName);

        Utils.displayAvatar(context, ((ImageView) findViewById(R.id.iv_charity_avatar)), charity.getMainPhoto(), initials);
        container.removeAllViews();
        initDescriptionView();
    }

    private void getCharityById() {
        if (Utils.isOnline(context)) {
            if (charityId.equals("-1") || TextUtils.isEmpty(charityId)) return;
            ApiClient.getApiClient().getCharityDetails(TokenStorage.getToken(context), charityId)
                    .enqueue(new BaseCallback<CharityResultById>(this, true) {
                        @Override
                        protected void onResult(int code, CharityResultById result) {
                            if (code == 200) {
                                if (result == null) return;
                                charity = result;
                                if (userId != -1 && userId == charity.getAuthorId()) {
                                    ivSettings.setVisibility(View.VISIBLE);
                                }
                                initView();
                            } else if (code == 404) {

                            }
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.iv_settings:
                Intent intent = new Intent(this, CharitySettingsActivity.class);
                intent.putExtra(CHARITY_MEMBERS_VISIBILITY, membersVisibility);
                intent.putExtra(CHARITY_VISIBILITY, charityVisibility);
                intent.putExtra(CHARITY_RESULT, charity);
                startActivity(intent);
                break;
            case R.id.tv_contribution:
                startActivityForResult(new Intent(context, CardsActivity.class)
                        .putExtra(REQUEST, true), REQUEST_CARD);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CARD && resultCode == RESULT_OK) {
            card = data.getParcelableExtra(CARD);
            if (card != null) {
                // TODO: 15.05.2018 create request
            } else {

            }
        }
    }

    @Override
    public void onTabChanged(int position) {
        container.removeAllViews();
        switch (position) {
            case TAB_DESCRIPTION:
                initDescriptionView();
                break;
            case TAB_CONTACT:
                Log.d("sss", "onTabChanged: ");
                initContactView();
                break;
        }
    }

    private void initDescriptionView() {
        View v = View.inflate(context, R.layout.item_charity_details_desciption, container);
        TextView tvDescription;
        tvDescription = v.findViewById(R.id.tv_description);
        tvDescription.setText(charity.getDescription());
        tvDescription.setMovementMethod(new ScrollingMovementMethod());
        if (charity.getExecution() > 100) {
            v.findViewById(R.id.tv_collected).setVisibility(View.VISIBLE);
            v.findViewById(R.id.tv_progress).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.tv_collected).setVisibility(View.GONE);
            v.findViewById(R.id.tv_progress).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tv_progress)).setText(charity.getExecution() + "%");
        }

        ((TextView) v.findViewById(R.id.tv_goal)).setText(charity.getRequiredAmount().toString());

    }

    private void initContactView() {
        View v = View.inflate(context, R.layout.item_charity_details_contact, container);
        RecyclerView rvCharityContact = v.findViewById(R.id.rv_charity_contact);
        rvCharityContact.setLayoutManager(new LinearLayoutManager(context));
        rvCharityContact.setAdapter(new CharityDetailContactsAdapter(context, charity.getDonation()));

        ((TextView) v.findViewById(R.id.tv_amount)).setText(charity.getTotalAmount().toString());

    }

}
