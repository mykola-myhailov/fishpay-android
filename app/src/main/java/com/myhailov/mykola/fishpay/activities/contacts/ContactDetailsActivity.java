package com.myhailov.mykola.fishpay.activities.contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.ContactDetailResult;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class ContactDetailsActivity extends BaseActivity {



    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        initToolBar("просмотр профайла");

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        Contact contact = extras.getParcelable(Keys.CONTACT);
        if (contact == null) return;

        String phone, photo, name, surname;

        phone = contact.getPhone();
        userId = contact.getUserId();
        photo = contact.getPhoto();
        name = contact.getName();
        surname = contact.getSurname();

        ((TextView) findViewById(R.id.tvPhone)).setText(phone);
        ((TextView) findViewById(R.id.tvName)).setText(String.format("%s %s", name, surname));
        String initials = Utils.extractInitials(name, surname);
        Utils.displayAvatar(context, ((ImageView) findViewById(R.id.ivAvatar)), photo, initials);

        cardNumberRequest();
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
    public void onClick(View view) {

    }
}
