package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.ContactsAdapter;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SelectContactsActivity extends BaseActivity {

    public static final int REQUEST_CONTACT = 42;

    private List<Contact> contacts, appContacts, displayedContacts, filteredContacts;
    private RecyclerView rvContacts;
    private String filterQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        initCustomToolbar(getString(R.string.my_contacts));
        findViewById(R.id.ivBack).setOnClickListener(this);
        contacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        appContacts = new ArrayList<>();
        displayedContacts = new ArrayList<>();
        filteredContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            long userId = contact.getUserId();
            if (userId != 0) appContacts.add(contact);
        }
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(context));
        initSearchView();
        displayedContacts = appContacts;
        filter();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container:   // click on app user's contact,
                Contact contact = (Contact) view.getTag();
                if (contact == null) {
                    Utils.toast(context, getString(R.string.error));
                    return;
                } else {
                    setResult(RESULT_OK, new Intent().putExtra(Keys.CONTACT, contact));
                    finish();
                }
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivInvite:  // click on contact from device to invite
                if (!Utils.isOnline(context)){
                    Utils.noInternetToast(context);
                    return;
                }
                //
                //
                break;
        }
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
        searchView.setQueryHint(getString(R.string.enter_name));
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        //searchEditText.setTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setHintTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    }

    private void filter() {
        filteredContacts.clear();
        if (filterQuery == null || filterQuery.equals("")){
            rvContacts.setAdapter(new ContactsAdapter(context, displayedContacts));
            return;
        }
        String search = filterQuery.toLowerCase();
        for (Contact contact: displayedContacts) {
            String name = contact.getName().toLowerCase();
            if (name.contains(search)){
                filteredContacts.add(contact);
            }
        }
        rvContacts.setAdapter(new ContactsAdapter(context, filteredContacts));
    }

    // title = "выбор контакта"
}
