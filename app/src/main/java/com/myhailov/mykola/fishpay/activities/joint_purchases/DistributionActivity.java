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
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;

import static com.myhailov.mykola.fishpay.utils.Keys.CONTACTS;
import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE;
import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE2;

public class DistributionActivity extends BaseActivity {

    private CommonPurchaseBody commonPurchaseBody;
    private ArrayList<Contact> contacts;


    private DistributionContactsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution);

        commonPurchaseBody = getIntent().getParcelableExtra(PURCHASE2);
        contacts = getIntent().getParcelableArrayListExtra(CONTACTS);

        initCustomToolbar("распределение взносов");
        initViews();
    }

    private void initViews() {
        if (commonPurchaseBody != null)
            ((TextView) findViewById(R.id.tv_amount)).setText(commonPurchaseBody.getAmount());

        initTabLayout();

        if (contacts != null) {
            defineAmounts(contacts, Float.valueOf(commonPurchaseBody.getAmount()));
            adapter = new DistributionContactsAdapter(contacts);
            RecyclerView rvContacts = findViewById(R.id.rv_contacts);
            rvContacts.setLayoutManager(new LinearLayoutManager(context));
            rvContacts.setAdapter(adapter);
        }

        findViewById(R.id.tv_finish).setOnClickListener(this);
    }

    private void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(new Tab("Пропорционально", 0));
        tabLayout.addTab(new Tab("Вручную", 1));
    }


    @Override
    public void onClick(final View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_finish:
                v.setClickable(false);
                Member[] members = new Member[contacts.size()];
                int i = 0;
                for (Contact contact : contacts) {
                    members[i] = new Member();
                    members[i].set(contact);
                    i++;
                }
                commonPurchaseBody.setMembers(members);
                ApiClient.getApiClient().createJointPurchase(TokenStorage.getToken(context), commonPurchaseBody)
                        .enqueue(new BaseCallback<Object>(context, false) {
                            @Override
                            protected void onResult(int code, Object result) {
                                if (code == 201) {
                                    startActivity(new Intent(context, JointPurchasesActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                } else {
                                    v.setClickable(true);
                                }
                            }

                            @Override
                            protected void onError(int code, String errorDescription) {
                                super.onError(code, errorDescription);
                                v.setClickable(true);
                            }

                            @Override
                            public void onFailure(@NonNull Call<BaseResponse<Object>> call, @NonNull Throwable t) {
                                super.onFailure(call, t);
                                v.setClickable(true);
                            }
                        });
                break;
        }
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

            public ViewHolder(View itemView) {
                super(itemView);
                ivAvatar = itemView.findViewById(R.id.iv_avatar);
                tvInitials = itemView.findViewById(R.id.tv_initials);
                tvName = itemView.findViewById(R.id.tv_name);
                tvAmount = itemView.findViewById(R.id.tv_amount);
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
                if (contact.getAmountToPay() != 0)
                    tvAmount.setText(String.valueOf(contact.getAmountToPay()));
            }
        }
    }

}