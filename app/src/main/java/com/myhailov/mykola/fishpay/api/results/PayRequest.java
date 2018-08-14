package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

import android.content.Context;

import com.myhailov.mykola.fishpay.R;

/**
 * Created by nicholas on 07.03.18.
 *
 * "id":448,
 * "payer_id":176,
 * "requester_id":127,
 * "card_number":"7537537537537573",
 * "amount":20000,
 * "comment":null,
 * "status":"ACTIVE",
 * "created_at":"2018-03-07 15:12:04",
 * "updated_at":"2018-03-07 15:12:12",
 * "contact_first_name":"Kiev",
 * "contact_last_name":null
 */

public class PayRequest {
    private @SerializedName("id")
    long id;
    private @SerializedName("payer_id")
    long payerId;
    private @SerializedName("requester_id")
    long requesterId;
    private @SerializedName("card_number")
    String cardNumber;
    private @SerializedName("amount")
    int amount;
    private @SerializedName("comment")
    String comment;
    private @SerializedName("status")
    String status;
    private @SerializedName("created_at")
    String creatingTime;
    private @SerializedName("updated_at")
    String changedAt;
    private @SerializedName("contact_first_name")
    String firstName;
    private @SerializedName("contact_last_name")
    String lastName;


    public PayRequest() {
    }

    public long getId() {
        return id;
    }

    public long getPayerId() {
        return payerId;
    }

    public long getRequesterId() {
        return requesterId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public String _getStatus() {
        return status;
    }

    public void setCreatingTime(String creatingTime) {
        this.creatingTime = creatingTime;
    }

    public String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(String changedAt) {
        this.changedAt = changedAt;
    }



    public String getStatus(Context context) {
        switch (status) {
            case "ACTIVE":
                return context.getString(R.string.delivered);
            case "VIEWED":
            case "DELETED_BY_RECIPIENT":
                return context.getString(R.string.read);
            case "ACCEPTED":
                return context.getString(R.string.approved);
            case "REJECTED":
                return context.getString(R.string.rejected);
            case "PAID":
                return context.getString(R.string.paid);
            default:
                return status;
        }
    }

    public String getCreatingTime() {
        return creatingTime;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (lastName != null) return firstName + " " + lastName;
        return firstName;
    }
}
