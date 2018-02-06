package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 06.02.18.
 */

public class ContactDetailResult {

    @SerializedName("public_card")
    private String publicCard;

    @SerializedName("first_name")
    private String name;

    @SerializedName("last_name")
    private String suname;

    public String getPublicCard() {
        return publicCard;
    }

    public String getName() {
        return name;
    }

    public String getSuname() {
        return suname;
    }
}
