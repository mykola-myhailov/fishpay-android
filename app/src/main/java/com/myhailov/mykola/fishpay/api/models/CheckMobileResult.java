package com.myhailov.mykola.fishpay.api.models;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.ApiInterface;

/**
 *  Created by Mykola Myhailov  on 14.11.17.
 *
 *  Value type of field "result" in response of {@link ApiInterface#checkMobile}
 *  */

public class CheckMobileResult {

    @SerializedName("codeOTP")
    private String codeOTP;

    public String getCodeOTP() {
        return codeOTP;
    }
}
