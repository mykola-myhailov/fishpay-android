package com.myhailov.mykola.fishpay.api.requestBodies;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 10.04.18.
 */
public class GroupSpendBody  {

    @SerializedName("title") private String title;
    @SerializedName("description") private String description;
    @SerializedName("start_amount") private int amount;
    @SerializedName("members") private Member[] members;

    public GroupSpendBody(String title, String description, int amount, Member[] members) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.members = members;
    }
}
