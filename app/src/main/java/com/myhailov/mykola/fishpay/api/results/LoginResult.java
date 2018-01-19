package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.ApiInterface;

/**
 * Created by Mykola Myhailov  on 06.12.17.
 *
 *  *  Value type of field "result" in response of {@link ApiInterface#login}
 */

public class LoginResult {

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("UI_LANG")
    private String lang;

    public String getToken(){
        return tokenType + " " + accessToken;
    }

    public String getLang() {
        return lang;
    }
}
