package com.myhailov.mykola.fishpay.api.requestBodies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nicholas on 21.02.18.
 */

public class CommonPurchaseBody implements Parcelable {
    @SerializedName("title") private String title;
    @SerializedName("description") private String description;
    @SerializedName("amount") private String amount;
    @SerializedName("date_to") private String dateTo;
    @SerializedName("creator_card_id") private String creatorCardId;
    @SerializedName("members") private Member[] members;

    public CommonPurchaseBody() {
    }


    protected CommonPurchaseBody(Parcel in) {
        amount = in.readString();
        creatorCardId = in.readString();
        dateTo = in.readString();
        description = in.readString();
        title = in.readString();
//        members = in.createTypedArray(Member.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(creatorCardId);
        dest.writeString(dateTo);
        dest.writeString(description);
        dest.writeString(title);
//        dest.writeTypedArray(members, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommonPurchaseBody> CREATOR = new Creator<CommonPurchaseBody>() {
        @Override
        public CommonPurchaseBody createFromParcel(Parcel in) {
            return new CommonPurchaseBody(in);
        }

        @Override
        public CommonPurchaseBody[] newArray(int size) {
            return new CommonPurchaseBody[size];
        }
    };

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public void setCreatorCardId(String creatorCardId) {
        this.creatorCardId = creatorCardId;
    }

    public void setMembers(Member[] members) {
        this.members = members;
    }

    public String getAmount() {
        return amount;
    }

}