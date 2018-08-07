package com.myhailov.mykola.fishpay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by Mykola Myhailov  on 07.08.18.
 */
public class UapayInfoStorage {

    public static String UAPAY_STORAGE = "uapay_storage";
    public static String UAPAY_ID_KEY = "uapay_id_key";
    public static String UAPAY_KEY_KEY = "uapay_key_key";


    public static String getUapayIdKey(Context context){
        return context.getSharedPreferences(UAPAY_STORAGE, Context.MODE_PRIVATE)
                .getString(UAPAY_ID_KEY, "");
    }


    public static String getUapayKeyKey(Context context){
        return context.getSharedPreferences(UAPAY_STORAGE, Context.MODE_PRIVATE)
                .getString(UAPAY_ID_KEY, "");
    }

}
