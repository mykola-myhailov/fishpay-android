package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.contacts.ContactDetailsActivity;
import com.myhailov.mykola.fishpay.activities.contacts.SearchContactActivity;
import com.myhailov.mykola.fishpay.adapters.ContactsAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.ContactsResult;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class ContactsActivity extends DrawerActivity {

    private TextView tvCancel;
    private SearchView searchView;

    private List<Contact> allContacts, appContacts, displayedContacts, filteredContacts;
    private RecyclerView rvContacts;
    private String filterQuery = "";
    private AlertDialog alertDeleteContact;
    private long contactIdDelete;
    private String phoneDelete;
    private int tabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        createDrawer();
        initDrawerToolbar(getString(R.string.my_contacts));
//        getContactsRequest();
        allContacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        Log.d("sss", "onCreate: " + allContacts.size());
        appContacts = new ArrayList<>();
        displayedContacts = new ArrayList<>();
        filteredContacts = new ArrayList<>();
        for (Contact contact : allContacts) {
            long userId = contact.getUserId();
            if (userId != 0) {
                appContacts.add(contact);
            }
        }

        initToggleButtons();
        findViewById(R.id.ivPlus).setOnClickListener(this);
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(context));
        initSearchView();
        displayedContacts = appContacts;
        filter();

    }

    private void initToggleButtons() {
        final ToggleSwitch toggleSwitch = findViewById(R.id.toggleSwitch);
        ArrayList<String> labels = new ArrayList<>();
        labels.add(getString(R.string.active));
        labels.add(getString(R.string.all));
        toggleSwitch.setLabels(labels);
        toggleSwitch.setCheckedTogglePosition(0);
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener() {

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position == 0) {
                    tabPosition = 0;
                    displayedContacts = appContacts;
                }
                else {
                    tabPosition = 1;
                    displayedContacts = allContacts;
                }
                filter();
            }
        });
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                toggleSwitch.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivPlus:
                context.startActivity(new Intent(context, SearchContactActivity.class));
                break;
            case R.id.container:   // click on app user's contact, to see details
                Contact contact = (Contact) view.getTag();
                if (contact == null) {
                    Utils.toast(context, getString(R.string.error));
                    return;
                }
                Intent contactDetailsIntent = new Intent(context, ContactDetailsActivity.class);
                contactDetailsIntent.putExtra(Keys.CONTACT, contact);
                context.startActivity(contactDetailsIntent);
                break;

            case R.id.tv_delete:
                contactIdDelete = ((Contact) view.getTag()).getContactId();
                phoneDelete = ((Contact) view.getTag()).getPhone();
                showDeleteAlert();
                break;
            case R.id.tv_first_action:
                alertDeleteContact.cancel();
                break;
            case R.id.tv_second_action:
                if (contactIdDelete == 0) {
                    toast(getString(R.string.deleted));
                    Contact con = new Contact();
                    for (Contact appContact : allContacts) {
                        if (appContact.getPhone().equals(phoneDelete)) {
                            con = appContact;
                            break;
                        }
                    }
                    DBUtils.getDaoSession(context).delete(con);
                    allContacts.remove(con);
                    displayedContacts = allContacts;
                    filter();
                } else {
                    deleteContactRequest(contactIdDelete);
                }
                alertDeleteContact.cancel();
                break;

            case R.id.ivInvite:  // click on contact from device to invite
                if (!Utils.isOnline(context)) {
                    Utils.noInternetToast(context);
                    return;
                }
                break;
            case R.id.tv_cancel:
                tvCancel.setVisibility(View.GONE);
                searchView.clearFocus();
                searchView.setQuery("", false);
                break;
        }
    }

    private void deleteContactRequest(final long id) {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface()
                    .blockUserById(TokenStorage.getToken(context), "DELETED", id + "")
                    .enqueue(new BaseCallback<Object>(context, false) {
                        @Override
                        protected void onResult(int code, Object result) {
                            toast(getString(R.string.deleted));
                            Contact con = new Contact();
                            for (Contact appContact : allContacts) {
                                if (appContact.getContactId() == id) {
                                    con = appContact;
                                    break;
                                }
                            }
                            DBUtils.getDaoSession(context).delete(con);
                            allContacts.remove(con);
                            appContacts.remove(con);
                            if (tabPosition == 0){
                                displayedContacts = appContacts;
                            }else {
                                displayedContacts = allContacts;
                            }
                            filter();

                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private void showDeleteAlert() {
        TextView tvDelete, tvClose, tvDescription, tvTitle;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_with_two_action, null);
        dialogBuilder.setView(dialogView);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvClose = dialogView.findViewById(R.id.tv_first_action);
        tvDelete = dialogView.findViewById(R.id.tv_second_action);
        tvDescription = dialogView.findViewById(R.id.tv_description);

        tvClose.setText(getString(R.string.cancel));
        tvDelete.setText(getString(R.string.ok));
        tvTitle.setText(getString(R.string.alert_delete_contact));
        tvDescription.setText(getString(R.string.alert_delete_contact_description));
        tvDelete.setOnClickListener(this);
        tvClose.setOnClickListener(this);

        alertDeleteContact = dialogBuilder.create();
        alertDeleteContact.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDeleteContact.show();
    }

    private void initSearchView() {
        searchView = findViewById(R.id.search);
        tvCancel = findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
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
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.abl_contacts).setVisibility(View.GONE);
                    tvCancel.setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.abl_contacts).setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(searchView.getQuery().toString())) {
                        tvCancel.setVisibility(View.GONE);
                    }
                }
            }
        });


        searchView.setQueryHint(getString(R.string.contacts_search));
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        //searchEditText.setTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setHintTextColor(getResources().getColor(R.color.grey_material_500));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
    }

    private void filter() {
        filteredContacts.clear();
        if (filterQuery == null || filterQuery.equals("")) {
            rvContacts.setAdapter(new ContactsAdapter(context, displayedContacts));
            return;
        }
        String search = filterQuery.toLowerCase();
        for (Contact contact : displayedContacts) {
            String name = contact.getName().toLowerCase() + contact.getSurname().toLowerCase();
            if (name.contains(search)) {
                filteredContacts.add(contact);
            }
        }
        rvContacts.setAdapter(new ContactsAdapter(context, filteredContacts));
    }

    private void getContactsRequest(){
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
            return;
        }

        ApiClient.getApiInterface()
                .getContacts(TokenStorage.getToken(context), true, true)
                .enqueue(new BaseCallback<ContactsResult>(context, true) {
                    @Override
                    protected void onResult(int code, ContactsResult result) {
                        if (result == null) return;
                        allContacts = result.getContacts();
                        for (Contact contact : allContacts) {
                            if (contact.getContactId() != 0) {
                                appContacts.add(contact);
                            }
                        }
                        displayedContacts = appContacts;
                        filter();
                    }
                });
    }

}
