package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 02.05.18.
 */
public class AuditPayResult {

    @SerializedName("type")
    private String type;

    @SerializedName("key")
    private String key;

    @SerializedName("id")
    private String id;

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }
}
