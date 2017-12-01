package com.myhailov.mykola.fishpay.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

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

    public static String toTwoSigns(int number){
        if (0 < number && number < 10) return "0" + number;
        else return "" + number;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



}
