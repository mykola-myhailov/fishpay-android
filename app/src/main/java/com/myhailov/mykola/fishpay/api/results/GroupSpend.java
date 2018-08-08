package com.myhailov.mykola.fishpay.api.results;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 10.04.18.
 */
public class GroupSpend implements Parcelable {

    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private String created_at;


    @SerializedName("start_amount")
    private int startAmount;

    @SerializedName("member_part")
    private String memberPart;

    @SerializedName("status")
    private String status;

    @SerializedName("creator_id")
    private long creatorId;

    @SerializedName("creator_first_name")
    private String creatorName;

    @SerializedName("creator_last_name")
    private String creatorSurname;

    // unused:
    //  "contact_creator_first_name":"",
    //   "contact_creator_last_name":""




    //Getters:
    public long getId() { return id; }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public int getStartAmount() {
        return startAmount;
    }

    public String getMemberPart() {
        return memberPart;
    }

    public String getStatus() {
        return status;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getCreatorSurname() {
        return creatorSurname;
    }


    public GroupSpend(long id) {
        this.id = id;
    }

    //Parcelable implementation:
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(startAmount);
        parcel.writeString(memberPart);
        parcel.writeString(status);
        parcel.writeLong(creatorId);
        parcel.writeString(creatorName);
        parcel.writeString(creatorSurname);
    }

    protected GroupSpend(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        startAmount = in.readInt();
        memberPart = in.readString();
        status = in.readString();
        creatorId = in.readLong();
        creatorName = in.readString();
        creatorSurname = in.readString();
    }

    public static final Creator<GroupSpend> CREATOR = new Creator<GroupSpend>() {
        @Override
        public GroupSpend createFromParcel(Parcel in) {
            return new GroupSpend(in);
        }

        @Override
        public GroupSpend[] newArray(int size) {
            return new GroupSpend[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }


}
