package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 07.08.18.
 */
public class UapayInfo {

    @SerializedName("uapay_id") private String uapayId;
    @SerializedName("uapay_key") private String uapayKey;

    public String getUapayId() {
        return uapayId;
    }

    public String getUapayKey() {
        return uapayKey;
    }


   // {"uapay_key":"cURkWWxmU0h5MGpaSExpQlRpeUJlZz09","uapay_id":"545","sanbox":true}
}
