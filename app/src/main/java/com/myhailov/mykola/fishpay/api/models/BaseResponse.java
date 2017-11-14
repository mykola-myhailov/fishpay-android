package com.myhailov.mykola.fishpay.api.models;

import com.google.gson.annotations.SerializedName;

/** Created by Mykola Myhailov  on 14.11.17. */

public class BaseResponse<T> {

    @SerializedName("errorCode")
    private String errorDescription;

    @SerializedName("result")
    private T result;

    public String getErrorDescription() {
        return errorDescription;
    }

    public T getResult() {
        return result;
    }
}
