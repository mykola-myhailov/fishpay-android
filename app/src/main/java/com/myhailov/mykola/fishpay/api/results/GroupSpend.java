package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 10.04.18.
 */
public class GroupSpend {

    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String amount;

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

    //  "contact_creator_first_name":"y\u011dtyhg",
    //   "contact_creator_last_name":"ttfgh"


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
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
}
