package com.myhailov.mykola.fishpay.api.results;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by wwaw on 12.02.18.
 */

public class SearchedContactsResult {

    @SerializedName("contacts")
    private ArrayList<SearchedContact> contacts;

    public ArrayList<SearchedContact> getContacts() {
        return contacts;
    }

    public static class SearchedContact implements Parcelable{

        @SerializedName("id")
        private long id;

        @SerializedName("phone")
        private String phone;

        @SerializedName("first_name")
        private String name;

        @SerializedName("second_name")
        private String suname;

        @SerializedName("imported_contacts_first_name")
        private String importedName;

        @SerializedName("photo")
        private String photo;

        protected SearchedContact(Parcel in) {
            id = in.readLong();
            phone = in.readString();
            name = in.readString();
            suname = in.readString();
            importedName = in.readString();
            photo = in.readString();
        }

        public static final Creator<SearchedContact> CREATOR = new Creator<SearchedContact>() {
            @Override
            public SearchedContact createFromParcel(Parcel in) {
                return new SearchedContact(in);
            }

            @Override
            public SearchedContact[] newArray(int size) {
                return new SearchedContact[size];
            }
        };

        public long getId() {
            return id;
        }

        public String getPhone() {
            return phone;
        }

        public String getName() {
            return name;
        }

        public String getSuname() {
            return suname;
        }

        public String getImportedName() {
            return importedName;
        }

        public String getPhoto() {
            return photo;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(id);
            parcel.writeString(phone);
            parcel.writeString(name);
            parcel.writeString(suname);
            parcel.writeString(importedName);
            parcel.writeString(photo);
        }
    }

/*    {"timestamp":"12-02-2018 09:24:08:281",
            "errorCode":null,"errorDescription":null,
            "result":{"contacts":[{
                "id":94,
                "phone":"380123456781",
                "first_name":"\u0435\u0435\u0435",
                "second_name":"\u043f\u0440\u043f\u0432",
                "imported_contacts_first_name":"Sdfsfsfs",
                "imported_contacts_last_name":null,
                "public_card_number":null,"status":
        "ACTIVE","photo":"257d8a6d04c7d0b2c3ef94c424c9d2e8.tmp",
                "is_blocked":false}]}}*/

}