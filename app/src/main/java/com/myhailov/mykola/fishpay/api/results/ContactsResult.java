package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.database.Contact;

import java.util.ArrayList;

/** Created by Mykola Myhailov  on 22.01.18.*/

public class ContactsResult {

    @SerializedName("contacts")
    private ArrayList<Contact> contacts;


    public ArrayList<Contact> getContacts() {
        return contacts;
    }
    /*
    "contacts":[
    {
        "id":622,
            "contact_id":94,
            "phone_number":"380123456781",
            "first_name":"Sdfsfsfs",
            "last_name":null,
            "email":null,
            "status":"ACTIVE",
            "isActiveUser ":true,
            "photo_link":"257d8a6d04c7d0b2c3ef94c424c9d2e8.tmp"
    }
      */

}
