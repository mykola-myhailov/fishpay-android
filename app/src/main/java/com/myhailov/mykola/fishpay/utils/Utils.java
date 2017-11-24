package com.myhailov.mykola.fishpay.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;

/** Created by Mykola Myhailov  on 15.11.17. */

public class Utils {

    /** @return true, if internet is available*/
    public static boolean isOnline(@NonNull Context context) {
        Boolean online = true;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            online = (netInfo != null && netInfo.isConnected());
        }
        catch (Exception e){
            e.printStackTrace();
        }
            return online;
    }

    /** show toast with {@param text} */
    public static void toast(@NonNull Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /** show toast about internet non-availability */
    public static void noInternetToast(@NonNull Context context){
        Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show();
    }
}
