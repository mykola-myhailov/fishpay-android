package com.myhailov.mykola.fishpay.api.requestBodies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.database.Contact;

/**
 * Created by nicholas on 21.02.18.
 */
public class Member implements Parcelable {
    @SerializedName("type")
    private String type;
    @SerializedName("amount_to_pay")
    private String amountToPay;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;

    public Member() {
    }

    protected Member(Parcel in) {
        type = in.readString();
        amountToPay = in.readString();
        userId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phone = in.readString();
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(amountToPay);
        dest.writeString(userId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public void setType(String type) {
        this.type = type;
    }

    public void setAmountToPay(String amountToPay) {
        this.amountToPay = amountToPay;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void set(Contact contact) {
        amountToPay = String.valueOf(((int) (contact.getAmountToPay() * 100)));
        if (contact.getUserId() == -1) {
            type = "people";
            firstName = contact.getName();
            lastName = "";
            phone = contact.getPhone();
        } else {
            long id = contact.isActiveUser() ? contact.getContactId() : contact.getUserId();
            type = contact.isActiveUser() ? "user" : "contact";
            userId = String.valueOf(id);
        }
    }
}
