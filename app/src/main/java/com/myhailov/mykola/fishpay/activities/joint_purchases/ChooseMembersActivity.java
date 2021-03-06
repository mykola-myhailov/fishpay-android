package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.CommonPurchaseBody;
import com.myhailov.mykola.fishpay.api.results.ContactsResult;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.myhailov.mykola.fishpay.views.Tab;
import com.myhailov.mykola.fishpay.views.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.CONTACTS;
import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE2;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST_MEMBER;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.PHONE;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.USER_PREFS;

public class ChooseMembersActivity extends BaseActivity {

    private CommonPurchaseBody commonPurchaseBody;

    private RecyclerView rvContacts;
    private ArrayList<Contact> allContacts, activeContacts, selectedUsers;

    private RecyclerView rvSelectedUsers;
    private View tvInfo;
    private View flPlaceholder;
    private SelectableContactsAdapter selectableContactsAdapter;
    private SelectedClientsAdapter selectedClientsAdapter;

    public final static String FROM_JOINT_PURCHASES = "joint_purchases", FROM_GROUP_SPENDS = "group_spends";
    private String from = FROM_JOINT_PURCHASES;

    private int groupSpendAmount;
    private String groupName;
    private String groupSpendDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_members);
        initCustomToolbar(getString(R.string.select_participants));

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(Keys.PURCHASE))
            commonPurchaseBody = getIntent().getParcelableExtra(Keys.PURCHASE);
        else {
            from = FROM_GROUP_SPENDS;
            groupSpendAmount = extras.getInt(Keys.AMOUNT);
            groupSpendDescription = extras.getString(Keys.DESCRIPTION);
            groupName = extras.getString(Keys.GROUP);
        }
        initViews();
        getContacts();
    }

    private void getContacts() {
        ApiClient.getApiInterface().getContacts(TokenStorage.getToken(context), true, true)
                .enqueue(new BaseCallback<ContactsResult>(context, false) {
                    @Override
                    protected void onResult(int code, ContactsResult result) {
                        if (code == 200) {
                            allContacts = result.getContacts();
                            activeContacts = new ArrayList<>();
                            selectedUsers = new ArrayList<>();
                            SharedPreferences preferences = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                            String id = preferences.getString(PrefKeys.ID, "");
                            String phone = preferences.getString(PHONE, "");
                            String name = preferences.getString(PrefKeys.NAME, "");
                            String surname = preferences.getString(PrefKeys.SURNAME, "");
                            String avatar = preferences.getString(PrefKeys.AVATAR, "");

                            long myId = -1;
                            for (Contact contact : allContacts) {
                                if (contact.isActiveUser()) activeContacts.add(contact);
                                if (contact.getPhone().equals(phone)) {
                                    myId = contact.getUserId();
                                    int index = allContacts.indexOf(contact);
                                    allContacts.remove(index);
                                    allContacts.add(0, contact);
                                    break;
                                }
                            }
                            if (myId == -1) {
                                myId = Long.parseLong(id);
                                Contact contactMe = new Contact();
                                contactMe.setUserId(myId);
                                contactMe.setContactId(myId);
                                contactMe.setPhone(phone);
                                contactMe.setPhoto(avatar);
                                contactMe.setName(name);
                                contactMe.setSurname(surname);
                                contactMe.setIsActiveUser(true);
                                allContacts.add(0, contactMe);
                                activeContacts.add(0, contactMe);
                            }

                            for (Contact activeContact : activeContacts) {
                                if (myId == activeContact.getUserId()) {
                                    int index = activeContacts.indexOf(activeContact);
                                    activeContacts.remove(index);
                                    activeContacts.add(0, activeContact);
                                    break;
                                }
                            }

                            selectableContactsAdapter = new SelectableContactsAdapter(context, activeContacts);
                            selectedClientsAdapter = new SelectedClientsAdapter(context, selectedUsers);
                            rvContacts.setAdapter(selectableContactsAdapter);
                            rvSelectedUsers.setAdapter(selectedClientsAdapter);
                            if (myId != -1) {
                                selectableContactsAdapter.setSelectedId(myId);
                                selectedUsers.add(findUserById(myId));
                            }
                            initTabs();
                        }
                    }
                });
    }

    private Contact findUserById(long id) {
        if (allContacts != null && allContacts.size() > 0) {
            for (Contact contact : allContacts) {
                if (contact.getUserId() == id) return contact;
            }
            return null;
        } else return null;
    }

    private void initViews() {
        rvContacts = findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(context));


        rvSelectedUsers = findViewById(R.id.rv_selected_users);
        rvSelectedUsers.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        flPlaceholder = findViewById(R.id.fl_placeholder);
        tvInfo = findViewById(R.id.tv_info);

        findViewById(R.id.ivPlus).setOnClickListener(this);
        findViewById(R.id.tv_go).setOnClickListener(this);
    }

    private void initTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(new Tab(getString(R.string.active), 0));
        tabLayout.addTab(new Tab(getString(R.string.all), 1));
        tabLayout.setTabChangedListener(getTabChangedListener());
    }

    @NonNull
    private TabLayout.OnTabChangedListener getTabChangedListener() {
        return new TabLayout.OnTabChangedListener() {
            @Override
            public void onTabChanged(int position) {
                switch (position) {
                    case 0:
                        selectableContactsAdapter.setList(activeContacts);
                        break;
                    case 1:
                        selectableContactsAdapter.setList(allContacts);
                        break;
                }
            }
        };
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ll_contact:
                long id = ((Contact) view.getTag()).getUserId();
                if (selectableContactsAdapter.setSelectedId(id)) {
                    selectedUsers.add(findUserById(id));
                } else selectedUsers.remove(findUserById(id));
                selectedClientsAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_remove:
                Contact contact = ((Contact) view.getTag());
                selectedUsers.remove(contact);
                selectedClientsAdapter.notifyDataSetChanged();
                if (contact.getUserId() != -1)
                    selectableContactsAdapter.setSelectedId(contact.getUserId());
                break;
            case R.id.ivPlus:
                startActivityForResult(new Intent(context, CreateMemberActivity.class), REQUEST_MEMBER);
                break;
            case R.id.tv_go:
                nextActivity();
                break;
        }
    }


    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.select_min_2))
                .setPositiveButton(getString(R.string.ok), null)
                .create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEMBER && resultCode == RESULT_OK) {
            Contact contact = data.getParcelableExtra(CONTACT);
            selectedUsers.add(contact);
            selectedClientsAdapter.notifyDataSetChanged();
        }
    }

    private void nextActivity() {
        if (selectedUsers != null && selectedUsers.size() >= 2) {

            Intent intent = new Intent(context, DistributionActivity.class);
            intent.putExtra(CONTACTS, selectedUsers);
            switch (from) {
                case FROM_JOINT_PURCHASES:
                    intent.putExtra(PURCHASE2, commonPurchaseBody);
                    break;
                case FROM_GROUP_SPENDS:
                    intent.putExtra(Keys.AMOUNT, groupSpendAmount);
                    intent.putExtra(Keys.DESCRIPTION, groupSpendDescription);
                    intent.putExtra(Keys.GROUP, groupName);
                    break;
            }
            intent.putExtra(Keys.FROM, from);
            context.startActivity(intent);
        } else showErrorDialog();
    }

    private class SelectableContactsAdapter extends RecyclerView.Adapter<SelectableContactsAdapter.ViewHolder> {

        private Context context;
        private ArrayList<Contact> list;
        private ArrayList<Long> selectedIds;

        public SelectableContactsAdapter(Context context, ArrayList<Contact> list) {
            this.context = context;
            this.list = list;
            selectedIds = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_contact_selectable, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.bind(list.get(position));

        }

        public void setList(ArrayList<Contact> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        /**
         * @param id contacts userId
         * @return true if add item, false - remove item
         */
        public boolean setSelectedId(long id) {
            boolean result;
            if (!selectedIds.contains(id)) {
                selectedIds.add(id);
                result = true;
            } else {
                selectedIds.remove(selectedIds.indexOf(id));
                result = false;
            }
            notifyDataSetChanged();
            return result;
        }

        @Override
        public int getItemCount() {
            if (list == null) return 0;
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View llContact;
            ImageView ivAvatar;
            TextView tvInitials;
            TextView tvName;
            View ivCheck;

            public ViewHolder(View itemView) {
                super(itemView);
                llContact = itemView.findViewById(R.id.ll_contact);
                ivAvatar = itemView.findViewById(R.id.iv_avatar);
                tvInitials = itemView.findViewById(R.id.tv_initials);
                tvName = itemView.findViewById(R.id.tv_name);
                ivCheck = itemView.findViewById(R.id.iv_check);
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
                ivCheck.setVisibility(selectedIds.contains(contact.getUserId()) ? View.VISIBLE : View.GONE);
                llContact.setTag(contact);
                llContact.setOnClickListener((View.OnClickListener) context);
            }
        }
    }

    private class SelectedClientsAdapter extends RecyclerView.Adapter<SelectedClientsAdapter.ViewHolder> {

        private Context context;
        private ArrayList<Contact> list;

        public SelectedClientsAdapter(Context context, ArrayList<Contact> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_contact_selected, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(list.get(position));
        }

        @Override
        public int getItemCount() {
            int result = 0;
            if (list != null) {
                result = list.size();
                if (result == 0) {
                    tvInfo.setVisibility(View.VISIBLE);
                    flPlaceholder.setVisibility(View.VISIBLE);
                } else if (result >= 2) {
                    tvInfo.setVisibility(View.GONE);
                    flPlaceholder.setVisibility(View.INVISIBLE);
                } else {
                    tvInfo.setVisibility(View.VISIBLE);
                    flPlaceholder.setVisibility(View.INVISIBLE);
                }
            }
            return result;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvInitials;
            ImageView ivAvatar;
            View ivRemove;

            public ViewHolder(View itemView) {
                super(itemView);
                tvInitials = itemView.findViewById(R.id.tv_initials);
                ivAvatar = itemView.findViewById(R.id.iv_avatar);
                ivRemove = itemView.findViewById(R.id.iv_remove);
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

                ivRemove.setTag(contact);
                ivRemove.setOnClickListener((View.OnClickListener) context);
            }
        }
    }
}


