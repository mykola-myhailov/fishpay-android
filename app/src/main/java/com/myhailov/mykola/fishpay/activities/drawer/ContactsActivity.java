package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.contacts.ContactDetailsActivity;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

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
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(context));
        initSearchView();
        displayedContacts = appContacts;
        rvContacts.setAdapter(new ContactsAdapter(displayedContacts));
    }

    private void initToggleButtons() {
        ToggleSwitch toggleSwitch = findViewById(R.id.toggleSwitch);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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

    private class ContactsViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivAvatar, ivInvite;
        private TextView tvName;
        private View container;

        ContactsViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivInvite = itemView.findViewById(R.id.ivInvite);
            container = itemView.findViewById(R.id.container);
        }
    }

    private class ContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder> {


        ContactsAdapter(List<Contact> contacts) {
            this.contacts = contacts;
        }

        private List<Contact> contacts;
        @Override
        public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_item, parent, false);
            return new ContactsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ContactsViewHolder holder, int position) {
            Contact contact = contacts.get(position);
            if (contact == null) return;
            String name = contact.getName();
            long userId = contact.getUserId();

            if (name != null) holder.tvName.setText(name.toUpperCase());
            String photo = contact.getPhoto();
            String initials = "";
            String phone = contact.getPhone();
            if (name != null) initials = Utils.extractInitials(name, "");

            final String finalInitials = initials;


            if (userId == 0) {  //this contact is not app user
                Picasso picasso = new Picasso.Builder(context)
                        .listener(new Picasso.Listener() {
                            @Override
                            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                                Utils.setInitialsImage(context, finalInitials, holder.ivAvatar);
                            }
                        })
                        .build();
                if (photo != null && !photo.equals("")) {
                    Uri photoUri = Uri.parse(contact.getPhoto());
                    picasso.load(photoUri).into(holder.ivAvatar);
                } else Utils.setInitialsImage(context, finalInitials, holder.ivAvatar);
            }

            else {  //this contact is app user
                Utils.displayAvatar(context, holder.ivAvatar, photo, initials);
                holder.container.setTag(contact);
                holder.container.setOnClickListener((View.OnClickListener) context);
            }


        }

        @Override
        public int getItemCount() {
            if (contacts == null) return 0;
            return contacts.size();
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
        searchView.setQueryHint("введите имя");
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        //searchEditText.setTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setHintTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    }

    private void filter() {
        filteredContacts.clear();
        if (filterQuery == null || filterQuery.equals("")){
            rvContacts.setAdapter(new ContactsAdapter(displayedContacts));
            return;
        }
        String search = filterQuery.toLowerCase();
        for (Contact contact: displayedContacts) {
            String name = contact.getName().toLowerCase();
            if (name.contains(search)){
                filteredContacts.add(contact);
            }
        }
        rvContacts.setAdapter(new ContactsAdapter(filteredContacts));
    }


}
