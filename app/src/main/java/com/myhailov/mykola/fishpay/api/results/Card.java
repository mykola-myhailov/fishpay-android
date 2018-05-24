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
    private @SerializedName("pan_masked") String panMasked;
    private @SerializedName("type") String type;


  //  {"timestamp":"26-03-2018 13:06:14:932","errorCode":null,"errorDescription":null,"result":[{"id":50,"user_id":133,"name":"rhg gv g. vv gg v","pan_masked":"5574427370","created_at":"2018-03-26 13:06:14","updated_at":"2018-03-26 13:06:14"}]}


    private Card(Parcel in) {
        id = in.readString();
        name = in.readString();
        panMasked = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(panMasked);
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

    public String getPanMasked() { return panMasked; }

    public String getCardNumber() {
        if (panMasked == null) return "-";
        return panMasked.substring(0, 4)
                + " " + panMasked.substring(4, 6)
                + "** **** " + panMasked.substring(6);
    }

    public String getLastFourNumbers() {
        return "**** " + panMasked.substring(6);
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
