package com.myhailov.mykola.fishpay.api;

import com.google.gson.annotations.SerializedName;

/**
 *  Created by Mykola Myhailov  on 15.11.17.
 *
 *  Response json of all request in this app
 *  <T> is model class different depending on the request
 *  these classes are in package {@link com.myhailov.mykola.fishpay.api.models}
 */


class BaseResponse<T> {

    @SerializedName("errorDescription")
    private String errorDescription;

    @SerializedName("result")
    private T result;

    String getErrorDescription() {return errorDescription;}

    T getResult() {
        return result;
    }
}
