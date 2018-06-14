package com.myhailov.mykola.fishpay.api.results;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 13.06.18.
 */
public class Transaction implements Parcelable {

    @SerializedName("id")
    private long id;

    @SerializedName("member_from")
    private long memberFromId;

    @SerializedName("member_to")
    private long memberToId;

    @SerializedName("amount")
    private long amount;

    @SerializedName("comment")
    private String comment;

    @SerializedName("transfer_id")
    private long transferId;

    @SerializedName("member_from_first_name")
    private String memberFromName;

    @SerializedName("member_from_second_name")
    private String memberFromSurname;

    @SerializedName("member_to_first_name")
    private String memberToName;

    @SerializedName("member_to_second_name")
    private String memberToSurname;

    @SerializedName("created_at")
    private String created_at;

    public String getDate() {
        return created_at;
    }

    public long getId() {
        return id;
    }

    public long getMemberFromId() {
        return memberFromId;
    }

    public long getMemberToId() {
        return memberToId;
    }

    public long getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public long getTransferId() {
        return transferId;
    }

    public String getMemberFromName() {
        if (memberFromName == null) return "";
        return memberFromName;
    }

    public String getMemberFromSurname() {
        if (memberFromSurname == null) return "";
        return memberFromSurname;
    }

    public String getMemberToName() {
        if (memberToName == null) return "";
        return memberToName;
    }

    public String getMemberToSurname() {
        if (memberToSurname == null)return "";
        return memberToSurname;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(memberFromId);
        parcel.writeLong(memberToId);
        parcel.writeLong(amount);
        parcel.writeString(comment);
        parcel.writeLong(transferId);
        parcel.writeString(memberFromName);
        parcel.writeString(memberFromSurname);
        parcel.writeString(memberToName);
        parcel.writeString(memberToSurname);
        parcel.writeString(created_at);
    }

    protected Transaction(Parcel in) {
        id = in.readLong();
        memberFromId = in.readLong();
        memberToId = in.readLong();
        amount = in.readLong();
        comment = in.readString();
        transferId = in.readLong();
        memberFromName = in.readString();
        memberFromSurname = in.readString();
        memberToName = in.readString();
        memberToSurname = in.readString();
        created_at = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
