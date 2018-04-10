package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.JointPurchasesActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.requestBodies.CommonPurchaseBody;
import com.myhailov.mykola.fishpay.api.requestBodies.GroupSpendBody;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.myhailov.mykola.fishpay.utils.Keys.CONTACTS;
import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE;
import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE2;

public class DistributionActivity extends BaseActivity {

    private CommonPurchaseBody commonPurchaseBody;
    private ArrayList<Contact> contacts;


    private DistributionContactsAdapter adapter;

    private String from;

    private int groupSpendAmount;
    private String groupName;
    private String groupSpendDescription;
    private String  proportion;
    private Member[] members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution);

        Bundle extras = getIntent().getExtras();
        contacts = extras.getParcelableArrayList(CONTACTS);
        from = extras.getString(Keys.FROM);
        members = new Member[]{};

        switch (from){
            case ChooseMembersActivity.FROM_GROUP_SPENDS:
                groupSpendAmount =  extras.getInt(Keys.AMOUNT);
                groupSpendDescription = extras.getString(Keys.DESCRIPTION);
                groupName = extras.getString(Keys.GROUP);
                initCustomToolbar("распределение долей");
                break;
            case ChooseMembersActivity.FROM_JOINT_PURCHASES:
                commonPurchaseBody = extras.getParcelable(PURCHASE2);
                initCustomToolbar("распределение взносов");
                break;
        }


        initViews();
    }

    private void initViews() {
        int amount = 0;
        switch (from){

            case ChooseMembersActivity.FROM_GROUP_SPENDS:
                amount = groupSpendAmount;
                (findViewById(R.id.llAmount)).setVisibility(View.GONE);
                proportion = calculateProportion();
                break;
            case ChooseMembersActivity.FROM_JOINT_PURCHASES:
                amount = Integer.parseInt(commonPurchaseBody.getAmount());
                ((TextView) findViewById(R.id.tv_amount)).setText(
                        String.valueOf(((float) amount) / 100)
                );
                initTabLayout();
                break;
        }
            if (contacts != null) {
                defineAmounts(contacts, ((float) amount) / 100);
                adapter = new DistributionContactsAdapter(contacts);
                RecyclerView rvContacts = findViewById(R.id.rv_contacts);
                rvContacts.setLayoutManager(new LinearLayoutManager(context));
                rvContacts.setAdapter(adapter);
            }

            findViewById(R.id.tv_finish).setOnClickListener(this);
    }

    private String calculateProportion() {
         int quantity = contacts.size();
         float proportion = 100.0f/quantity;
         return String.format(Locale.ENGLISH,"%.1f", (proportion));
    }

    private void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(new Tab("Пропорционально", 0));
        tabLayout.addTab(new Tab("Вручную", 1));
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case  R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_finish:
                members = new Member[contacts.size()];
                int i = 0;

                switch (from) {
                    case ChooseMembersActivity.FROM_JOINT_PURCHASES:
                        for (Contact contact : contacts) {
                            members[i] = new Member();
                            members[i].set(contact);
                            i++;
                        }
                        createJointPurchaseRequest();
                        break;
                    case ChooseMembersActivity.FROM_GROUP_SPENDS:
                        for (Contact contact : contacts) {
                            members[i] = new Member();
                            members[i].set(contact, proportion );
                            i++;
                        }
                        createGroupSpendRequest();
                        break;

                }
                break;
        }
    }

    private void createGroupSpendRequest() {
        if (Utils.isOnline(context)){
            GroupSpendBody groupSpendBody
                    = new GroupSpendBody(groupName, groupSpendDescription, groupSpendAmount, members);
            ApiClient.getApiClient()
                    .createSpending(TokenStorage.getToken(context), groupSpendBody)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 201) {
                                startActivity(new Intent(context, JointPurchasesActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        }
                    });

        } else Utils.noInternetToast(context);
    }

    private void createJointPurchaseRequest() {
        if (Utils.isOnline(context)) {
            commonPurchaseBody.setMembers(members);
            ApiClient.getApiClient().createJointPurchase(TokenStorage.getToken(context), commonPurchaseBody)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 201) {
                                startActivity(new Intent(context, JointPurchasesActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        }
                    });
        } else Utils.noInternetToast(context);
    }


    private void defineAmounts(ArrayList<Contact> contacts, float amount) {
        int count = contacts.size();
        float oneAmount = amount / count;
        oneAmount = Float.valueOf(String.format(Locale.ENGLISH,"%.2f", oneAmount));
        for (Contact contact : contacts)
            contact.setAmountToPay(oneAmount);
        if (oneAmount * count != amount) {
            float remainder = Float.valueOf(String.format(Locale.ENGLISH,"%.2f",amount - oneAmount * count));
            contacts.get(0).setAmountToPay(oneAmount + remainder);
        }
    }


    private class DistributionContactsAdapter extends RecyclerView.Adapter<DistributionContactsAdapter.ViewHolder> {

        private ArrayList<Contact> contacts;

        public DistributionContactsAdapter(ArrayList<Contact> contacts) {
            this.contacts = contacts;
        }

        public ArrayList<Contact> getContacts() {
            return contacts;
        }

        public void setContacts(ArrayList<Contact> contacts) {
            this.contacts = contacts;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_contact_distribution, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(contacts.get(position));
        }

        @Override
        public int getItemCount() {
            if (contacts != null) return contacts.size();
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivAvatar;
            TextView tvInitials;
            TextView tvName;
            TextView tvAmount;
            TextView tvUnits;

            public ViewHolder(View itemView) {
                super(itemView);
                ivAvatar = itemView.findViewById(R.id.iv_avatar);
                tvInitials = itemView.findViewById(R.id.tv_initials);
                tvName = itemView.findViewById(R.id.tv_name);
                tvAmount = itemView.findViewById(R.id.tv_amount);
                tvUnits = itemView.findViewById(R.id.tvUnits);
            }

            void bind(Contact contact) {
                String initials = Utils.extractInitials(contact.getName(), contact.getSurname());
                if (contact.getPhoto() != null && !contact.getPhoto().equals("") && !contact.getPhoto().equals("null")) {
                    Uri photoUri = Uri.parse(ApiClient.BASE_API_URL + "api/resources/photo/" + contact.getPhoto());
                    Picasso.with(context).load(photoUri).into(ivAvatar);
                    tvInitials.setText("");
                } else {
                    ivAvatar.setImageDrawable(null);
                    tvInitials.setText(initials);
                }
                tvName.setText(contact.getFullName());
                switch (from){
                    case ChooseMembersActivity.FROM_GROUP_SPENDS:
                        tvAmount.setText(proportion);
                        tvUnits.setText("%");
                        break;
                    case ChooseMembersActivity.FROM_JOINT_PURCHASES:
                        if (contact.getAmountToPay() != 0)
                            tvAmount.setText(String.valueOf(contact.getAmountToPay()));
                        break;
                }

            }
        }
    }

}