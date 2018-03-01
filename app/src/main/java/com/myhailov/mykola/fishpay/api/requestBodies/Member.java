package com.myhailov.mykola.fishpay.api.requestBodies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.database.Contact;

/**
 * Created by nicholas on 21.02.18.
 */
public class Member implements Parcelable {
    private String id;
    @SerializedName("member_status") private String memberStatus;
    @SerializedName("type") private String type;
    @SerializedName("amount_to_pay") private String amountToPay;
    @SerializedName("amount_paid") private String amountPaid;

    @SerializedName("user_id") private String userId;

    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    @SerializedName("second_name") private String secondName;
    @SerializedName("phone") private String phone;
    @SerializedName("email") private String email;
    @SerializedName("photo_link") private String photo;

    public Member() {
    }


    protected Member(Parcel in) {
        id = in.readString();
        memberStatus = in.readString();
        type = in.readString();
        amountToPay = in.readString();
        amountPaid = in.readString();
        userId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        secondName = in.readString();
        phone = in.readString();
        email = in.readString();
        photo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(memberStatus);
        dest.writeString(type);
        dest.writeString(amountToPay);
        dest.writeString(amountPaid);
        dest.writeString(userId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(secondName);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(photo);
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
        if (contact.getUserId() == -1 || !contact.isActiveUser()) {
            type = "people";
            firstName = contact.getName();
            lastName = contact.getSurname();
            phone = contact.getPhone();
        } else if (contact.isActiveUser()) {
            type = "user";
            userId = String.valueOf(contact.getContactId());
        }
    }

    public String getId() {
        return id;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public String getType() {
        return type;
    }

    public String getAmountToPay() {
        return amountToPay;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public String getFullName() {
        if (secondName != null) return firstName + " " + secondName;
        return firstName;
    }
}
