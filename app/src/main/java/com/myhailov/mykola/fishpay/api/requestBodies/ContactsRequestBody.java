package com.myhailov.mykola.fishpay.api.requestBodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Mykola Myhailov  on 19.01.18.
 */

public class ContactsRequestBody {


@SerializedName("contacts")
    String contacts;

    public ContactsRequestBody(String contacts) {
        this.contacts = contacts;
    }

/*

        @SerializedName("contacts_data")
        private ArrayList<Contact> contacts;

    public ContactsRequestBody(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public static class Contact {

            public Contact(String phone, String name) {
         a
            }

            @SerializedName("phone_number")
            private String phone;

            @SerializedName("first_name")
            private String name;*/







}

