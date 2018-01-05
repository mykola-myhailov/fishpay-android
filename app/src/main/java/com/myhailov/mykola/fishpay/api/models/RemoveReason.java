package com.myhailov.mykola.fishpay.api.models;

import com.google.gson.annotations.SerializedName;

/** Created by Mykola Myhailov  on 05.01.18.*/

public class RemoveReason {

    @SerializedName("id")
    private long id;

    @SerializedName("key")
    private String key;

    @SerializedName("description_key")
    private String descriptionKey;

    @SerializedName("description")
    private String description;

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public String getDescription() {
        return description;
    }
}
