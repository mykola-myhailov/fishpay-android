package com.myhailov.mykola.fishpay.api.models;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.ApiInterface;

/**
 * Created by Mykola Myhailov  on 09.01.18.
 *
 * *  Value type of field "result" in response of {@link ApiInterface#removeAccount}
 */

public class RemoveAccResult {

    @SerializedName("request_id")
    private String requestId;


    public String getRequestId() {
        return requestId;
    }
}
