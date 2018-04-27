package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.contacts.ContactDetailsActivity;
import com.myhailov.mykola.fishpay.activities.contacts.SearchContactActivity;
import com.myhailov.mykola.fishpay.adapters.ContactsAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends DrawerActivity {

    private List<Contact> contacts, appContacts, displayedContacts, filteredContacts;
    private RecyclerView rvContacts;
    private String filterQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        createDrawer();
        initDrawerToolbar(getString(R.string.my_contacts));
        contacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        appContacts = new ArrayList<>();
        displayedContacts = new ArrayList<>();
        filteredContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            long userId = contact.getUserId();
            if (userId != 0) appContacts.add(contact);
        }

        initToggleButtons();
        findViewById(R.id.ivPlus).setOnClickListener(this);
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(context));
        initSearchView();
        displayedContacts = appContacts;
        filter();
        Log.d("log", contacts.size() + " " + appContacts.size() + " " + displayedContacts.size() + " "+
        filteredContacts.size());

    }

    private void initToggleButtons() {
        final ToggleSwitch toggleSwitch = findViewById(R.id.toggleSwitch);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Активные");
        labels.add("Все");;
        toggleSwitch.setLabels(labels);
        toggleSwitch.setCheckedTogglePosition(0);
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position == 0) displayedContacts = appContacts;
                else displayedContacts = contacts;
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
                long id = ((Contact) view.getTag()).getUserId();
                showDeleteConfirmation(id);
                break;

            case R.id.ivInvite:  // click on contact from device to invite
                if (!Utils.isOnline(context)){
                    Utils.noInternetToast(context);
                    return;
                }
                break;
        }
    }

    private void showDeleteConfirmation(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Вы действительно хотите удалить контакт  из списка?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteContactRequest(id);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }

    private void deleteContactRequest(long id) {
        if (Utils.isOnline(context)){
            ApiClient.getApiClient()
                    .changeContactStatus(TokenStorage.getToken(context), id, "DELETED")
                    .enqueue(new Callback<BaseResponse<Object>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                           // TODO:
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {

                        }
                    });
        } else Utils.noInternetToast(context);
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
        searchView.setQueryHint("введите имя");
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


}
