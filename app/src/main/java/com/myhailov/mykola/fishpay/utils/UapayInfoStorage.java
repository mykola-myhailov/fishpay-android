package com.myhailov.mykola.fishpay.utils;

import android.content.Context;

/**
 * Created by Mykola Myhailov  on 07.08.18.
 */
public class UapayInfoStorage {

    public static String UAPAY_STORAGE = "uapay_storage";
    public static String UAPAY_ID_KEY = "uapay_id_key";
    public static String UAPAY_KEY_KEY = "uapay_key_key";
    public static String UAPAY_SANBOX = "uapay_sanbox";

   // "sanbox":true


    public static String getUapayIdKey(Context context){
        return context.getSharedPreferences(UAPAY_STORAGE, Context.MODE_PRIVATE)
                .getString(UAPAY_ID_KEY, "");
    }


    public static String getUapayKeyKey(Context context){
        return context.getSharedPreferences(UAPAY_STORAGE, Context.MODE_PRIVATE)
                .getString(UAPAY_KEY_KEY, "");
    }

    public static boolean getUapaySandbox(Context context){
        return context.getSharedPreferences(UAPAY_STORAGE, Context.MODE_PRIVATE)
                .getBoolean(UAPAY_SANBOX, false);
    }



}
