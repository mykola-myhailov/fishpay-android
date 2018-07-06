package com.myhailov.mykola.fishpay.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
        if (text == null) text = "";
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /** show simple alertDialog with {@param message} and "ok" button*/
    public static void alert (@NonNull Context context, String message ){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
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

    public static void displayAvatar(final Context context, final ImageView imageView, String photo, final String initials){
        if (photo != null && !photo.equals("")){
            Picasso picasso = new Picasso.Builder(context)
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Utils.setInitialsImage(context, initials, imageView);
                        }
                    })
                    .build();
            picasso.load(ApiClient.BASE_API_URL + "api/resources/photo/" + photo)
                    .into(imageView);
        } else Utils.setInitialsImage(context, initials, imageView);
    }

    public static void displayGoods(final Context context, final ImageView imageView, String photo, long id){
        if (photo != null && !photo.equals("")) {
            Picasso picasso = new Picasso.Builder(context).build();
            picasso.load(ApiClient.BASE_API_URL + "api/resources/goods/" + id + "/" + photo)
                    .fit()
                    .centerInside()
                    .into(imageView);
        }
    }

    public static void displayGoods(Picasso picasso, final ImageView imageView, String photo, long id){
        if (photo != null && !photo.equals("")) {
            picasso.load(ApiClient.BASE_API_URL + "api/resources/goods/" + id + "/" + photo)
                    .fit()
                    .centerInside()
                    .into(imageView);
        }
    }

    public static String buildPhotoUrl(String photo, int id){
        return ApiClient.BASE_API_URL + "api/resources/charity/" + id + "/" + photo;
    }

    public static String buildPhotoUrlGoods(String photo, int id){
        return ApiClient.BASE_API_URL + "api/resources/goods/" + id + "/" + photo;
    }


    public static void displayAvatar(final Context context, final ImageView imageView, Uri photo, final String initials){
        if (photo != null){
            Picasso picasso = new Picasso.Builder(context)
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Utils.setInitialsImage(context, initials, imageView);
                        }
                    })
                    .build();
            picasso.load(ApiClient.BASE_API_URL + photo).into(imageView);
        } else Utils.setInitialsImage(context, initials, imageView);
    }


    /** Create image from {@param initials}, set this image to {@param imageView} */
    public static void setInitialsImage(@NonNull Context context, @NonNull String initials, ImageView imageView){
        int size = context.getResources().getDimensionPixelSize(R.dimen.avatar_size);
        try {
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setTextSize(16 * context.getResources().getDisplayMetrics().density);
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(ContextCompat.getColor(context, R.color.black));
            canvas.drawColor(ContextCompat.getColor(context, R.color.white));
            canvas.drawText(initials, size/2, size/2 + 16 , paint);
            imageView.setImageBitmap(bitmap);
        } catch (Exception ignored){};


    }

    @NonNull
    public static String extractInitials (String name, String surname){
        if (name == null) name = "";
        if (surname == null) surname = "";
        String nameInitial = " ", surnameInitial = " ", initials;
        if (name.length() > 0) nameInitial = name.substring(0, 1);
        else name = " ";
        if (surname.length() > 0) surnameInitial = surname.substring(0, 1);
        else if (name.contains(" ")){
            String[] nameParts = name.split(" ");
            if (nameParts.length > 1) surname = nameParts[1];
            if (surname.length() > 0) surnameInitial = surname.substring(0, 1);
            else surnameInitial = " ";
        }
        initials = nameInitial.toUpperCase() + surnameInitial.toUpperCase()  + "";
        return initials;
    }


    public static RequestBody makeRequestBody(String text){
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }

    public static MultipartBody.Part makeRequestBodyFile(Uri imageUri) {
        if (imageUri == null) return null;
        File file = new File(imageUri.getPath());
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestFile = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData("img", file.getName(), requestFile);
    }


    public static MultipartBody.Part makeRequestBodyFile(Uri imageUri, String name) {
        if (imageUri == null) return null;
        File file = new File(imageUri.getPath());
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestFile = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }


    public static String pennyToUah(int penny) {
        String amount;
        if (penny != 0) {
            amount = String.format(Locale.ENGLISH, "%.2f", (double) (penny / (double) 100));
        }
        else amount = "0";
        return amount;
    }

    public static String pennyToUah(long penny) {
        String amount;
        if (penny != 0)
            amount = String.format(Locale.ENGLISH,"%.2f", (float) (penny / (float) 100));
        else amount = "0";
        return amount;
    }

    public static int UAHtoPenny(String UAH) {
        Double penny = 0.0;
        try {
            penny = Double.valueOf(UAH)*100;
        } catch (Exception ignored){};
        return penny.intValue();
    }


    public static void setPhoto(Context context, String photo, String initials, TextView tvInitials, ImageView ivAvatar) {
        if (photo != null && !photo.equals("") && !photo.equals("null")) {
            Uri photoUri = Uri.parse(ApiClient.BASE_API_URL + "api/resources/photo/" + photo);
            Picasso.with(context).load(photoUri).into(ivAvatar);
            tvInitials.setText("");
        } else {
            ivAvatar.setImageDrawable(null);
            tvInitials.setText(initials);
        }
    }

    public static String convertStringToDate(Context context, String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat("d MMMM y",
                context.getResources().getConfiguration().locale);
        return newDateFormat.format(convertedDate);
    }

    public static String checkDateIsToday(Context context, String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat newDateFormat = new SimpleDateFormat("d MMMM y");

        Date convertedDate = new Date();
        String today = newDateFormat.format(convertedDate);
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String convertDate = newDateFormat.format(convertedDate);
        if (convertDate.equals(today)){
            return context.getString(R.string.today,  new SimpleDateFormat("H:mm").format(convertedDate));
        }
        return new SimpleDateFormat("H:mm d MMMM y ",
                context.getResources().getConfiguration().locale).format(convertedDate);
    }

    public static String convertStringToDateWithCustomFormat(Context context, String date, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat(format, context.getResources().getConfiguration().locale);
        return newDateFormat.format(convertedDate);
    }

    public static void setText(TextView textView, String text){
        if (text != null && text != "null" && textView != null){
            textView.setText(text);
        }
    }

    public static void setText(TextView textView, long text){
        if (textView!= null){
            textView.setText(String.valueOf(text));
        }
    }

    public static void setText(TextView textView, int text){
        if (textView!= null){
            textView.setText(String.valueOf(text));
        }
    }

    public static void setText(TextView textView, double text){
        if (textView!= null){
            textView.setText(String.valueOf(text));
        }
    }

    public static void setText(TextView textView, float text){
        if (textView!= null){
            textView.setText(String.valueOf(text));
        }
    }

    public static String toPercentage(double part) {
        DecimalFormat percentFormat = new DecimalFormat("0.0%");
        percentFormat.setDecimalSeparatorAlwaysShown(false);
        percentFormat.setMinimumFractionDigits(0);
        percentFormat.setMaximumFractionDigits(0);
        return percentFormat.format(0);
    }

    public static  void showInfoAlert(Context context) {
        TextView tvTitle;
        final AlertDialog infoAlert;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = (LayoutInflater) context.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.alert_with_one_action, null);
        dialogBuilder.setView(dialogView);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvTitle.setText(context.getString(R.string.info));


        ((TextView)dialogView.findViewById(R.id.tv_description)).setText(context.getString(R.string.info_description));

        infoAlert = dialogBuilder.create();
        infoAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoAlert.show();
        dialogView.findViewById(R.id.tv_action_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoAlert.cancel();
            }
        });
    }

    public static  void showInfoNotSuportedAlert(Context context) {
        TextView tvTitle;
        final AlertDialog infoAlert;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = (LayoutInflater) context.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.alert_with_one_action, null);
        dialogBuilder.setView(dialogView);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvTitle.setText(context.getString(R.string.info));


        ((TextView)dialogView.findViewById(R.id.tv_description)).setText(context.getString(R.string.info_not_sup_description));

        infoAlert = dialogBuilder.create();
        infoAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoAlert.show();
        dialogView.findViewById(R.id.tv_action_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoAlert.cancel();
            }
        });
    }
}
