package com.myhailov.mykola.fishpay.activities.contacts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;

public class ContactDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        Contact contact = extras.getParcelable(Keys.CONTACT);

    }
}
