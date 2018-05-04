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

    @SerializedName("photo_link")
    private String photo;

    @SerializedName("imported_contact_id")
    private String contactId;

    @SerializedName("user_id")
    private String userId;

    public String getPublicCard() {
        return publicCard;
    }

    public String getName() {
        return name;
    }

    public String getSuname() {
        return suname;
    }

    public String getPhoto() { return photo; }

    public String getContactId() { return contactId; }

    public String getUserId() { return userId; }

    /*        "user_id": 136,
                "phone_number": "380972193386",
                "first_name": "Сергій",
                "last_name": "Чистяков",
                "email": "itp@com.ua",
                "status": "ACTIVE",
                "is_blocked": false,
                "public_card": null,
                "imported_contact_id": 18108,
                "imported_contact_first_name": "Чистяков",
                "imported_contact_last_name": null,
                "photo_link": "e6629087ab01edbe878da2ce5b74b412.jpg"*/
}
