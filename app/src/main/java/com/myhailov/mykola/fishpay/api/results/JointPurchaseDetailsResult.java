package com.myhailov.mykola.fishpay.api.results;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mykola Myhailov  on 15.12.17.
 *
 "id":41,
 "title":"\u0433\u043e\u0430",
 "creator":127,
 "description":null,
 "date_to":"",
 "status":"VIEWED",
 "amount_to_pay":2266,
 "created_at":"2018-02-27 14:32:54",
 "contact_creator_first_name":"Kiev",
 "contact_creator_last_name":null,
 "creator_first_name":"Nichlas",
 "creator_last_name":"Oli"
 */

public class JointPurchaseDetailsResult implements Parcelable {
    private String id;
    @SerializedName("creator") private String creatorId;
    private String description;
    private int amount;
    @SerializedName("date_to") private String to;
    @SerializedName("card_number") private String cardNumber;
    @SerializedName("pan_masked") private String panMasked;
    private ArrayList<Member> members;

    protected JointPurchaseDetailsResult(Parcel in) {
        id = in.readString();
        creatorId = in.readString();
        description = in.readString();
        amount = in.readInt();
        to = in.readString();
        cardNumber = in.readString();
        panMasked = in.readString();
        members = in.createTypedArrayList(Member.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(creatorId);
        dest.writeString(description);
        dest.writeInt(amount);
        dest.writeString(to);
        dest.writeString(cardNumber);
        dest.writeString(panMasked);
        dest.writeTypedList(members);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JointPurchaseDetailsResult> CREATOR = new Creator<JointPurchaseDetailsResult>() {
        @Override
        public JointPurchaseDetailsResult createFromParcel(Parcel in) {
            return new JointPurchaseDetailsResult(in);
        }

        @Override
        public JointPurchaseDetailsResult[] newArray(int size) {
            return new JointPurchaseDetailsResult[size];
        }
    };

    public String getPanMasked() {
        return panMasked;
    }

    public void setPanMasked(String panMasked) {
        this.panMasked = panMasked;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTo() {
        if (to == null) return "-";
        else {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(to);
                return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return "-";
            }
        }
    }

    public String getCreatorId() {
        return creatorId;
    }

    public int getAmount() {
        return amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public String getLastFourNumbers() {
        return "**** " + cardNumber.substring(12, 16);
    }

    public String getCreatorName (){
        if (members != null && members.size() > 0)
        for (Member member : members) {
            if (member != null){
               if(member.getUserId().equals(creatorId)) return member.getFullUserName();
            }
        }
        return "";
    }

}
