package com.myhailov.mykola.fishpay.api.results;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 06.06.18.
 */
public class MemberDetails implements Parcelable{

    @SerializedName("id")
    private long id;

    @SerializedName("member_part")
    private double part;

    @SerializedName("member_status")
    private String status;

    @SerializedName("type")
    private String type;

    @SerializedName("role")
    private String role;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("phone")
    private String phone;

    @SerializedName("member_id")
    private long memberId;

    @SerializedName("first_name")
    private String name;

    @SerializedName("second_name")
    private String surname;

    @SerializedName("photo_link")
    private String photo;

    @SerializedName("total_member_sum")
    private long sum;

    @SerializedName("size_commitment")
    private long commitment;

    @SerializedName("overpayment")
    private long overpaiment;

    @SerializedName("part_in_overpaiment")
    private double partInOverpaiment;

    @SerializedName("relative_balance")
    private double relativeBallance;


    // getters


    public long getMemberId() {
        return memberId;
    }

    public long getId() {
        return id;
    }

    public double getPart() {
        return part;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        if (TextUtils.isEmpty(surname)){
            return "";
        }
        return surname;
    }

    public String getPhoto() {
        return photo;
    }

    public long getSum() {
        return sum;
    }

    public long getCommitment() {
        return commitment;
    }

    public long getOverpaiment() {
        return overpaiment;
    }

    public double getPartInOverpaiment() {
        return partInOverpaiment;
    }

    public double getRelativeBallance() {
        return relativeBallance;
    }

    public MemberDetails() {
    }


//Parcelable implementation

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeDouble(part);
        parcel.writeString(status);
        parcel.writeString(type);
        parcel.writeString(role);
        parcel.writeString(userId);
        parcel.writeString(phone);
        parcel.writeString(name);
        parcel.writeString(surname);
        parcel.writeString(photo);
        parcel.writeLong(sum);
        parcel.writeLong(memberId);
        parcel.writeLong(commitment);
        parcel.writeLong(overpaiment);
        parcel.writeDouble(partInOverpaiment);
        parcel.writeDouble(relativeBallance);
    }

    protected MemberDetails(Parcel in) {
        id = in.readLong();
        part = in.readDouble();
        status = in.readString();
        type = in.readString();
        role = in.readString();
        userId = in.readString();
        phone = in.readString();
        name = in.readString();
        surname = in.readString();
        photo = in.readString();
        sum = in.readLong();
        memberId = in.readLong();
        commitment = in.readLong();
        overpaiment = in.readLong();
        partInOverpaiment = in.readDouble();
        relativeBallance = in.readDouble();
    }

    public static final Creator<MemberDetails> CREATOR = new Creator<MemberDetails>() {
        @Override
        public MemberDetails createFromParcel(Parcel in) {
            return new MemberDetails(in);
        }

        @Override
        public MemberDetails[] newArray(int size) {
            return new MemberDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
