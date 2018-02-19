package com.myhailov.mykola.fishpay.api.results;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nicholas on 19.02.18.
 */

public class Card implements Comparable<Card> {


    private @SerializedName("name") String name;
    private @SerializedName("card_number") String cardNumber;
    private @SerializedName("type") String type;

    public String getName() {
        return name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getType() {
        return type;
    }

    @Override
    public int compareTo(@NonNull Card o) {
        if (name != null) return name.compareTo(o.getName());
        else return 0;
    }
}
