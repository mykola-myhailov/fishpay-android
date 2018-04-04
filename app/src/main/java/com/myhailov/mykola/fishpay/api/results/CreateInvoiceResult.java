package com.myhailov.mykola.fishpay.api.results;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 04.04.18.
 */
public class CreateInvoiceResult {

    @SerializedName("request_id")
    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    @SerializedName("addressee")
    private ReceiverContact receiver;

    public ReceiverContact getReceiver() { return receiver; }

    public static class ReceiverContact implements Parcelable{

        @SerializedName("first_name")
        private String name;

        @SerializedName("last_name")
        private String surname;

        @SerializedName("contact_full_name")
        private String fullName;

        @SerializedName("phone")
        private String phone;

        @SerializedName("photo_url")
        private String photo;


        // Getters
        @NonNull
        public String getName() {
            if (name == null) return "";
            return name;
        }

        @NonNull
        public String getSurname() {
            if (surname == null) return "";
            return surname;
        }

        @NonNull
        public String getFullName() {
            if (fullName == null) return "";
            return fullName;
        }

        @NonNull
        public String getPhone() {
            if (phone == null) return "";
            return phone;
        }

        public String getPhoto() {
            return photo;
        }




        // Parcelable implementation
        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(name);
            parcel.writeString(surname);
            parcel.writeString(fullName);
            parcel.writeString(phone);
            parcel.writeString(photo);
        }

        protected ReceiverContact(Parcel in) {
            name = in.readString();
            surname = in.readString();
            fullName = in.readString();
            phone = in.readString();
            photo = in.readString();
        }

        public static final Creator<ReceiverContact> CREATOR = new Creator<ReceiverContact>() {
            @Override
            public ReceiverContact createFromParcel(Parcel in) {
                return new ReceiverContact(in);
            }

            @Override
            public ReceiverContact[] newArray(int size) {
                return new ReceiverContact[size];
            }
        };


        @Override
        public int describeContents() {
            return 0;
        }
    }
}


