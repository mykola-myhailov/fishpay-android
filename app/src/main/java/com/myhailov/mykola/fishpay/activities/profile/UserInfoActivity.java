package com.myhailov.mykola.fishpay.activities.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mvc.imagepicker.ImagePicker;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.ProfileSettingsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserInfoActivity extends BaseActivity {

//    private Switch switchOffer;
//    private TextView tvOffer;

    private String phone, name, surname, email, birthday, deviceId, deviceInfo;
    private EditText etName, etSurname, etEmail, etBirthday;
    private ImageView ivAvatar;
    private TextView tvSave;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

//        switchOffer = findViewById(R.id.switch_offer);
//        tvOffer = findViewById(R.id.tv_offer);
//        setOffer();
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        String photo = extras.getString(Keys.PHOTO, "");
        name = extras.getString(Keys.NAME, "");
        surname = extras.getString(Keys.SURNAME, "");
        birthday = extras.getString(Keys.BIRTHDAY, "");
        email = extras.getString(Keys.EMAIL, "");

//        switchOffer.setChecked(true);
        etName = findViewById(R.id.etName);
        etName.setText(name);
        etSurname = findViewById(R.id.etSurname);
        etSurname.setText(surname);
        etEmail = findViewById(R.id.etEmail);
        etEmail.setText(email);
        etBirthday = findViewById(R.id.etBirthday);
        etBirthday.setText(birthday);
        ivAvatar = findViewById(R.id.ivAvatar);
        String initails = Utils.extractInitials(name, surname);
        Utils.displayAvatar(context, ivAvatar, photo, initails);
        ivAvatar.setOnClickListener(this);
        tvSave = findViewById(R.id.tvSave);
        tvSave.setOnClickListener(this);
        //title = "Основная информация"
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAvatar:
                ImagePicker.pickImage(this, "Select your image:"); // onActivityResult after picking image
                break;
            case R.id.tvSave:
                name = etName.getText().toString();
                surname = etSurname.getText().toString();
                email = etEmail.getText().toString();

//                if (!switchOffer.isChecked()){
//                    toast(getString(R.string.privacy_licence_error));
//                    break;
//                }

                if (name.equals("")) Utils.toast(context, getString(R.string.enter_name));
                else if (surname.equals(""))
                    Utils.toast(context, getString(R.string.enter_surname));
                    //  else if (birthday == null) Utils.toast(context, getString(R.string.enter_birthday));
                else if (surname.length() < 2 || surname.length() > 20)
                    Utils.toast(context, getString(R.string.wrong_surname));
                else if (!email.equals("") && !Utils.isValidEmail(email))
                    Utils.toast(context, getString(R.string.incorrect_email));
                else editProfileRequest();
                break;
        }
    }

//    private void setOffer(){
//        SpannableString ss = new SpannableString(getString(R.string.privacy_licence_switch));
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//                startActivity(new Intent(context, PublicOfferActivity.class));
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setUnderlineText(true);
//            }
//        };
//        ss.setSpan(clickableSpan, 11, ss.length(), 0);
//        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue1)), 11, ss.length(), 0);
//
//
//        tvOffer.setText(ss);
//        tvOffer.setMovementMethod(LinkMovementMethod.getInstance());
//        tvOffer.setHighlightColor(Color.TRANSPARENT);
//    }


    private DatePickerDialog.OnDateSetListener createDateSetListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Locale locale = getResources().getConfiguration().locale;
                Locale.setDefault(locale);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat monthFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
                etBirthday.setText(monthFormat.format(calendar.getTimeInMillis()));
                birthday = year + "-" + Utils.toTwoSigns(month + 1) + "-" + Utils.toTwoSigns(dayOfMonth);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE) {
            // get new image, make file and upload to server
            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            if (bitmap != null) {
                ivAvatar.setImageBitmap(bitmap);

                File imageFile = null;
                try {
                    imageFile = File.createTempFile("avatar" + ".jpg", null, context.getCacheDir());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                OutputStream outputStream = null;
                try {
                    if (imageFile != null) outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    if (outputStream != null) outputStream.flush();
                    if (outputStream != null) outputStream.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
                imageUri = Uri.fromFile(imageFile);
            }
        }
    }

    private void editProfileRequest() {
        ApiClient.getApiInterface().editProfile(TokenStorage.getToken(context),
                Utils.makeRequestBody(name),
                Utils.makeRequestBody(surname),
                Utils.makeRequestBody(birthday),
                Utils.makeRequestBody(email),
                makeRequestBodyFile(imageUri)
        ).enqueue(new BaseCallback<Object>(context, true) {
                      @Override
                      protected void onResult(int code, Object result) {
                          if (code == 200)
                              context.startActivity(new Intent(context, ProfileSettingsActivity.class));
                          else Utils.toast(context, getString(R.string.error));
                      }
                  }
        );
    }

    private MultipartBody.Part makeRequestBodyFile(Uri imageUri) {
        if (imageUri == null) return null;
        File file = new File(imageUri.getPath());
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestFile = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData("img", file.getName(), requestFile);
    }
}
