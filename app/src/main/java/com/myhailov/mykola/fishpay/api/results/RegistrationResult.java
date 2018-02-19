package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.ApiInterface;

/**
 *  Created by Mykola Myhailov on 21.11.17.
 *
 *  Value TYPE of field "result" in response of {@link ApiInterface#registration
 *
 *  */

public class RegistrationResult {

    @SerializedName("access_token")
    private String token;

}
