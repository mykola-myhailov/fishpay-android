package com.myhailov.mykola.fishpay.api.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @NonNull
    public String getCodeOTP() {
        if (codeOTP == null) return "";
        return codeOTP;
    }

    @SerializedName("recoveryId")
    private String recoveryId;

    @Nullable
    public String getRecoveryId() {
        return recoveryId;
    }
}
