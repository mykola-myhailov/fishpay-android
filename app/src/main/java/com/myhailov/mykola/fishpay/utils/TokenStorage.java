package com.myhailov.mykola.fishpay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.myhailov.mykola.fishpay.api.ApiClient;

/** Created by Mykola Myhailov  on 24.11.17.*/

public class TokenStorage {


    private static String TOKEN_STORAGE = "token_storage";
    private static String TOKEN_KEY = "token_key";

    @NonNull
    public static String getToken(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_STORAGE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_KEY, "");
    }

    public static void setToken(@NonNull Context context, String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }
}
