package com.myhailov.mykola.fishpay.api.models;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.ApiInterface;

/**
 *  Created by Mykola Myhailov on 21.11.17.
 *
 *  Value type of field "result" in response of {@link ApiInterface#registration,
 *
 *  */

public class RegistrationResult {

    @SerializedName("access_token")
    private String token;

}
