package com.myhailov.mykola.fishpay.api.results;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 29.03.18.
 */
public class InvoiceDetailsResult {

    @SerializedName ("request_id")
    private String id;

    @SerializedName("pan_masked")
    private String pan_masked;

    @SerializedName("requester")
    private Requester requester;

    @SerializedName("amount")
    private String amount;

    @SerializedName("comment")
    private String comment;

    @SerializedName("status")
    private String status;


    public String getId() {
        return id;
    }

    @NonNull
    public String getPan_masked() {
        if (pan_masked == null) return "";
        return pan_masked;
    }

    public Requester getRequester() {
        return requester;
    }

    @NonNull
    public String getAmount() {
        if (amount == null) return "";
        return amount;
    }

    @NonNull
    public String getComment() {
        if (comment == null) return "";
        return comment;
    }

    public String getStatus() {
        return status;
    }

    public class Requester {


        @SerializedName("id")
        private String id;

        @SerializedName("ful_name")
        private String name;


        @SerializedName("phone")
        private String phone;

        @SerializedName("photo_url")
        private String photo;

        public String getId() {
            return id;
        }

        @NonNull
        public String getName() {
            if (name == null) return "";
            return name;
        }

        @NonNull
        public String getPhone() {
            if (phone == null) return "";
            return phone;
        }

        public String getPhoto() {
            return photo;
        }



    }



/*
    "result": {


    }
        "request_id": 114,
                "pan_masked": "not_found",
                "amount": 800,
                "comment": null,
                "status": "ACTIVE",
                "created_at": "2018-03-27 13:39:37",
                "status_changed_at": "2018-03-27 13:39:37",
                "requester": {
            "id": 133,
                    "ful_name": "yĝtyhg ttfgh",
                    "contact_full_name": " ",
                    "photo_url": "28baf49117081a1eee95c85f3477bab2.tmp",
                    "phone": "380123456789",
                    "requester_contact_id": null
        },
        "addressee": {
            "id": 133,
                    "photo_url": "28baf49117081a1eee95c85f3477bab2.tmp",
                    "ful_name": "yĝtyhg ttfgh",
                    "contact_full_name": " ",
                    "phone": "380123456789",
                    "addressee_contact_id": null
        },
        "goods": [
        {
            "money_request_id": 114,
                "goods_id": 123,
                "count": 30,
                "id": null,
                "user_id": null,
                "title": null,
                "description": null,
                "main_photo": null,
                "price": null,
                "created_at": null,
                "updated_at": null,
                "category": null,
                "visibility": null,
                "status": null
        },
        {
            "money_request_id": 114,
                "goods_id": 324,
                "count": 2,
                "id": null,
                "user_id": null,
                "title": null,
                "description": null,
                "main_photo": null,
                "price": null,
                "created_at": null,
                "updated_at": null,
                "category": null,
                "visibility": null,
                "status": null
        }*/
}
