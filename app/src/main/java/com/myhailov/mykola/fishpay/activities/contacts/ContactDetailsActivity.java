package com.myhailov.mykola.fishpay.activities.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.ContactsActivity;
import com.myhailov.mykola.fishpay.activities.pay_requests.CreatePayRequestActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.ContactDetailResult;
import com.myhailov.mykola.fishpay.api.results.ContactsResult;
import com.myhailov.mykola.fishpay.api.results.SearchedContactsResult;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class ContactDetailsActivity extends BaseActivity {

    private long userId;
    private  String phone, photo, name, surname;
    private boolean isAdded = false;
    private TextView tvIsAdded;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        initToolBar("просмотр профайла");

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        tvIsAdded = findViewById(R.id.tvInContactsList);
        if (extras.containsKey(Keys.CONTACT)) {
            contact = extras.getParcelable(Keys.CONTACT);
            if (contact == null) return;
            phone = contact.getPhone();
            userId = contact.getUserId();
            photo = contact.getPhoto();
            name = contact.getName();
            surname = contact.getSurname();

            tvIsAdded.setText("B списке контактов");
            ((TextView) findViewById(R.id.tvPhone)).setText(phone);
            ((TextView) findViewById(R.id.tvName)).setText(String.format("%s %s", name, surname));
            String initials = Utils.extractInitials(name, surname);
            Utils.displayAvatar(context, ((ImageView) findViewById(R.id.ivAvatar)), photo, initials);

            cardNumberRequest();
        } else {
            SearchedContactsResult.SearchedContact contact =
                    extras.getParcelable(Keys.SEARCHED_CONTACT);
            if (contact == null) return;
            phone = contact.getPhone();
            userId = contact.getId();
            photo = contact.getPhoto();
            name = contact.getName();
            surname = contact.getSuname();
            userId = contact.getId();
            tvIsAdded.setText("Добавить контакт");
            tvIsAdded.setOnClickListener(this);
            String initials = Utils.extractInitials(name, surname);
            Utils.displayAvatar(context, ((ImageView) findViewById(R.id.ivAvatar)), photo, initials);

            ((TextView) findViewById(R.id.tvPhone)).setText(phone);
            ((TextView) findViewById(R.id.tvName)).setText(String.format("%s %s", name, surname));

            String importedName = contact.getName();
            ((TextView) findViewById(R.id.tvName2)).setText(importedName);
            ((TextView) findViewById(R.id.tvCardNumber)).setVisibility(View.GONE);
        }
        (findViewById(R.id.tvGet)).setOnClickListener(this);


    }

    private void cardNumberRequest() {
        if (Utils.isOnline(context)){
            ApiClient.getApiClient()
                    .getContactDetails(TokenStorage.getToken(context), userId)
                    .enqueue(new BaseCallback<ContactDetailResult>(context, false) {
                        @Override
                        protected void onResult(int code, ContactDetailResult result) {
                            if (result == null) return;
                            String publicCard = result.getPublicCard();
                            String name = result.getName();
                            String surname = result.getSuname();
                            ((TextView) findViewById(R.id.tvName2)).setText(String.format("%s %s", name, surname));
                            if (publicCard != null) ((TextView) findViewById(R.id.tvCardNumber)).setText(publicCard);
                        }
                    });

        }

    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.tvInContactsList:
                if (isAdded) break;
                if (Utils.isOnline(context)){
                    ApiClient.getApiClient().addContact(TokenStorage.getToken(context), userId )
                            .enqueue(new BaseCallback<String>(context, true) {
                                @Override
                                protected void onResult(int code, String result) {
                                    getContactsRequest();
                                }
                            });
                } else Utils.noInternetToast(context);
                break;
            case R.id.tvGet:
                context.startActivity((new Intent(context, CreatePayRequestActivity.class))
                        .putExtra(Keys.CONTACT, contact ));
                break;
        }
    }

    private void getContactsRequest(){
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
            return;
        }
        ApiClient.getApiClient()
                .getContacts(TokenStorage.getToken(context), true, true)
                .enqueue(new BaseCallback<ContactsResult>(context, true) {
                    @Override
                    protected void onResult(int code, ContactsResult result) {
                        if (result == null) return;
                        ArrayList<Contact> appContacts = result.getContacts();
                        DBUtils.saveAppContacts(context, appContacts);
                        isAdded = true;
                        tvIsAdded.setText("В списке контактов");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (isAdded) context.startActivity(new Intent(context, ContactsActivity.class));
        else super.onBackPressed();
    }
}
