package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.ApiInterface;

/**
 * Created by Mykola Myhailov  on 01.12.17.
 *
 *  *  Value type of field "result" in response of {@link ApiInterface#checkRecoveryOTP)}
 */

public class CheckRecoveryResult {
    @SerializedName("userId")
    private String userId;

    public String getUserId() {
        return userId;
    }
}
