package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 06.02.18.
 */

public class ContactDetailResult {

    @SerializedName("phone_number")
    private String phone;

    @SerializedName("public_card")
    private String panMasked;

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

    @SerializedName("status")
    private String status;

    @SerializedName("is_blocked")
    private boolean isBlocked;

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublicCard() {
        if (panMasked == null) return "-";
        return panMasked.substring(0, 4)
                + " " + panMasked.substring(4, 6)
                + "** **** " + panMasked.substring(6);
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

    public String getPhone() {
        return phone;
    }
}
