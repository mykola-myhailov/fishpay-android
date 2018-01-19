package com.myhailov.mykola.fishpay.api.results;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 15.12.17.
 */

public class ChangePassVerifyResult {

    @SerializedName("passChangeId")
    private String passChangeId;

    @Nullable
    public String getPassChangeId() {
        return passChangeId;
    }
}
