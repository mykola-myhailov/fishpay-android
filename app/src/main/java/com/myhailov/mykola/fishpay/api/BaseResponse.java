package com.myhailov.mykola.fishpay.api;

import com.google.gson.annotations.SerializedName;

/**
 *  Response json of all request in this app
 *  <T> is model class different depending on the request
 *  these classes are in package {@link com.myhailov.mykola.fishpay.api.results}
 */


public class BaseResponse<T> {

    @SerializedName("errorDescription")
    private String errorDescription;

    @SerializedName("result")
    private T result;

    public String getErrorDescription() {
        if (errorDescription == null) return "";
        return errorDescription;}

    public T getResult() {
        return result;
    }
}
