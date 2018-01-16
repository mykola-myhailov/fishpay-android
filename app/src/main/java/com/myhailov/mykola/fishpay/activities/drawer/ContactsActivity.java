package com.myhailov.mykola.fishpay.activities.drawer;

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
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends DrawerActivity {

    private List<Contact> contacts, filteredContacts;
    private RecyclerView rvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        createDrawer();
        initToolbar(getString(R.string.my_contacts));
        contacts =  DBUtils.getDaoSession(context).getContactDao().loadAll();
        filteredContacts = new ArrayList<>();
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setAdapter(new ContactsAdapter(contacts));
        rvContacts.setLayoutManager(new LinearLayoutManager(context));
        initSearchView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    private class ContactsViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivAvatar, ivInvite;
        private TextView tvName;

        ContactsViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivInvite = itemView.findViewById(R.id.ivInvite);
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
            if (name != null) holder.tvName.setText(name);
            String photo = contact.getPhoto();
            String initials = "";
            if (name != null) initials = Utils.extractInitials(name, "");
         //   Utils.displayAvatar(context, holder.ivAvatar, photo, initials);
            final String finalInitials = initials;
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

        @Override
        public int getItemCount() {
            return contacts.size();
        }
    }

    private void initSearchView() {
        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        searchView.setQueryHint("введите имя");
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        //searchEditText.setTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setHintTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    }

    private void filter(String newText) {
        filteredContacts.clear();
        if (newText == null || newText.equals("")){
            rvContacts.setAdapter(new ContactsAdapter(contacts));
            return;
        }
        String search = newText.toLowerCase();
        for (Contact contact: contacts) {
            String name = contact.getName().toLowerCase();
            if (name.contains(search)){
                filteredContacts.add(contact);
            }
        }
        rvContacts.setAdapter(new ContactsAdapter(filteredContacts));
    }
}
