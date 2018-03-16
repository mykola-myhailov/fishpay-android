package com.myhailov.mykola.fishpay.api.results;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nicholas on 19.02.18.
 */

public class Card implements Comparable<Card>, Parcelable {

    private @SerializedName("id") String id;
    private @SerializedName("name") String name;
    private @SerializedName("card_number") String cardNumber;
    private @SerializedName("type") String type;

    private Card(Parcel in) {
        id = in.readString();
        name = in.readString();
        cardNumber = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(cardNumber);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getLastFourNumbers() {
        return "**** " + cardNumber.substring(12, 16);
    }

    public String getType() {
        return type;
    }

    @Override
    public int compareTo(@NonNull Card o) {
        if (name != null) return name.compareTo(o.getName());
        else return 0;
    }

    public String getId() {
        return id;
    }
}
