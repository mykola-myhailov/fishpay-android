package com.myhailov.mykola.fishpay.activities.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.ContactsActivity;
import com.myhailov.mykola.fishpay.activities.TransactionActivity;
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
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.NAME;
import static com.myhailov.mykola.fishpay.utils.Keys.USER_ID;

public class ContactDetailsActivity extends BaseActivity {

    private long userId;
    private  String phone, photo, name, surname;
    private boolean isAdded = false;
    private TextView tvIsAdded;
    private ContactDetailResult contactDetails;
    private Contact contact;
    private boolean isContact;
    private SearchedContactsResult.SearchedContact searchedContact;
    private List<Contact> allContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        initCustomToolbar(getString(R.string.profile_view));
        allContacts = DBUtils.getDaoSession(context).getContactDao().loadAll();

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        tvIsAdded = findViewById(R.id.tvInContactsList);
        if (extras.containsKey(Keys.CONTACT)) {
            isContact = true;
            contact = extras.getParcelable(Keys.CONTACT);
            userId = contact.getUserId();
            if (Utils.isOnline(context)){
                ApiClient.getApiInterface()
                        .getContactDetails(TokenStorage.getToken(context), userId)
                        .enqueue(new BaseCallback<ContactDetailResult>(context, false) {
                            @Override
                            protected void onResult(int code, ContactDetailResult result) {
                                if (result == null) return;
                                contactDetails = result;
                                String publicCard = result.getPublicCard();
                                String name = result.getName();
                                String surname = result.getSuname();
                                String photo = result.getPhoto();
                                String phone = result.getPhone();
                                tvIsAdded.setText(getString(R.string.in_contacts));
                                if (phone != null) ((TextView) findViewById(R.id.tvPhone)).setText("+"+phone);
                                ((TextView) findViewById(R.id.tvName)).setText(String.format("%s %s", name, surname));
                                String initials = Utils.extractInitials(name, surname);
                                Utils.displayAvatar(context, ((ImageView) findViewById(R.id.ivAvatar)), photo, initials);
                            //    ((TextView) findViewById(R.id.tvName2)).setText(String.format("%s %s", name, surname));
                                if (publicCard != null) ((TextView) findViewById(R.id.tvCardNumber)).setText(publicCard);
                            }
                        });
            }
        } else {
            isContact = false;
            searchedContact = extras.getParcelable(Keys.SEARCHED_CONTACT);
            if (searchedContact == null) return;
            phone = searchedContact.getPhone();
            userId = searchedContact.getId();
            photo = searchedContact.getPhoto();
            name = searchedContact.getName();
            surname = searchedContact.getSurname();
            userId = searchedContact.getId();
            tvIsAdded.setText(getString(R.string.add_contact));
            for (Contact allContact : allContacts) {
                if(allContact.getUserId() == userId){
                    tvIsAdded.setText(getString(R.string.in_contacts));
                    break;
                }
            }
            tvIsAdded.setOnClickListener(this);
            String initials = Utils.extractInitials(name, surname);
            Utils.displayAvatar(context, ((ImageView) findViewById(R.id.ivAvatar)), photo, initials);

            ((TextView) findViewById(R.id.tvPhone)).setText(phone);
            ((TextView) findViewById(R.id.tvName)).setText(String.format("%s %s", name, surname));

            String importedName = searchedContact.getName();
            ((TextView) findViewById(R.id.tvName2)).setText(importedName);
            if (!TextUtils.isEmpty(searchedContact.getPanMaskedCard())){
                ((TextView) findViewById(R.id.tvCardNumber)).setText(formatCard(searchedContact.getPanMaskedCard()));
            }else {
                findViewById(R.id.tvCardNumber).setVisibility(View.GONE);
            }
        }
        (findViewById(R.id.tvGet)).setOnClickListener(this);
        (findViewById(R.id.tvGive)).setOnClickListener(this);
    }


    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvInContactsList:
                if (isAdded) break;
                if (Utils.isOnline(context)){
                    ApiClient.getApiInterface().addContact(TokenStorage.getToken(context), userId )
                            .enqueue(new BaseCallback<String>(context, true) {
                                @Override
                                protected void onResult(int code, String result) {
                                    getContactsRequest();
                                }
                            });
                } else Utils.noInternetToast(context);
                break;
            case R.id.tvGet:
                if (contactDetails != null) {
                    context.startActivity((new Intent(context, CreatePayRequestActivity.class))
                            .putExtra(Keys.CONTACT, contact));
                }else {
                    if (!isContact) {
                        context.startActivity((new Intent(context, CreatePayRequestActivity.class))
                                .putExtra(Keys.SEARCHED_CONTACT, searchedContact));
                    }
                }

                break;

            case R.id.tvGive:
                String contactName = "";
                String idUser = "";
                if (isContact) {
                    if (!TextUtils.isEmpty(contactDetails.getName())) {
                        contactName = contactDetails.getName() + " ";
                    }
                    if (!TextUtils.isEmpty(contactDetails.getSuname())) {
                        contactName = contactName + contactDetails.getSuname();
                    }
                    idUser = contactDetails.getUserId();
                }else {
                    contactName = name + " " + surname;
                    idUser = userId + "";
                }
                context.startActivity((new Intent(context, TransactionActivity.class))
                        .putExtra(NAME, contactName)
                        .putExtra(USER_ID, idUser)
                        .putExtra(Keys.CONTACT, contact));


        }
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
                        ArrayList<Contact> appContacts = result.getContacts();
                        DBUtils.saveAppContacts(context, appContacts);
                        isAdded = true;
                        tvIsAdded.setText(getString(R.string.in_contacts));
                    }
                });
    }

    private String formatCard(String card){
        return card.substring(0, 4) + " " +
                card.substring(4, 6) + "** **** " +
                card.substring(6, card.length());
    }

    @Override
    public void onBackPressed() {
        if (isAdded) context.startActivity(new Intent(context, ContactsActivity.class));
        else super.onBackPressed();
    }
}
