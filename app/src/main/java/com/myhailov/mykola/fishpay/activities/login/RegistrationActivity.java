package com.myhailov.mykola.fishpay.activities.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvc.imagepicker.ImagePicker;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.ContactsResult;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.DBUtils;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegistrationActivity extends BaseActivity {

    private String phone, name, surname, email, birthday, deviceId, deviceInfo;
    private EditText etName, etSurname, etEmail, etBirthday;
    private ImageView ivAvatar;
    private TextView tvSave;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        phone = extras.getString(Keys.PHONE);

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etBirthday = findViewById(R.id.etBirthday);
        etBirthday.setOnClickListener(this);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivAvatar.setOnClickListener(this);
        tvSave = findViewById(R.id.tvSave);
        tvSave.setOnClickListener(this);
//        uploadContactsRequest();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAvatar:
                ImagePicker.pickImage(this, "Select your image:"); // onActivityResult after picking image
                break;
            case R.id.etBirthday:
                showDataPicker();
                break;
            case R.id.tvSave:
                name = etName.getText().toString();
                surname = etSurname.getText().toString();
                email = etEmail.getText().toString();
                if (name.equals("")) Utils.toast(context, getString(R.string.enter_name));
                else if (surname.equals(""))
                    Utils.toast(context, getString(R.string.enter_surname));
                else if (birthday == null) Utils.toast(context, getString(R.string.enter_birthday));
                else if (surname.length() < 2 || surname.length() > 20)
                    Utils.toast(context, getString(R.string.wrong_surname));
                else if (!email.equals("") && !Utils.isValidEmail(email))
                    Utils.toast(context, getString(R.string.incorrect_email));
                else {
                    Intent intent = new Intent(context, SetPasswordActivity.class);
                    intent.putExtra(Keys.PHONE, phone);
                    intent.putExtra(Keys.NAME, name);
                    intent.putExtra(Keys.SURNAME, surname);
                    intent.putExtra(Keys.BIRTHDAY, birthday);
                    intent.putExtra(Keys.EMAIL, email);
                    if (imageUri != null) intent.putExtra(Keys.IMAGE, imageUri.toString());
                    context.startActivity(intent);
                }
                break;
        }
    }


    private void showDataPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, createDateSetListener(), 2000, 0, 1);
        datePickerDialog.show();
    }

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
//                uploadContactsRequest();
            }
        }
    }

    private void uploadContactsRequest() {
        List<Contact> contacts = DBUtils.getDaoSession(context).getContactDao().loadAll();
        if (!Utils.isOnline(this)) return;
        JSONArray contactsArray = new JSONArray();
        JSONObject preparedContacts = new JSONObject();
        try {
            for (Contact contactInfo : contacts) {
                JSONObject contactObject = new JSONObject();
                contactObject.put("first_name", contactInfo.getName());
                contactObject.put("last_name", contactInfo.getSurname());
                contactObject.put("phone_number", contactInfo.getPhone());
                contactsArray.put(contactObject);
            }

            preparedContacts.put("contacts_data", contactsArray);
        } catch (Exception ignored) {
        }
        ApiClient.getApiInterface().exportContacts(TokenStorage.getToken(this), preparedContacts.toString())
                .enqueue(new BaseCallback<Object>(context, false) {

                    @Override
                    protected void onError(int code, String errorDescription) {
                        super.onError(code, errorDescription);
                        Log.d("sss", "onError: " + errorDescription);
                    }

                    @Override
                    protected void onResult(int code, Object result) {
                        getContactsRequest();
                    }
                });
    }

    private void getContactsRequest() {
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
            return;
        }
        ApiClient.getApiInterface()
                .getContacts(TokenStorage.getToken(context), true, true)
                .enqueue(new BaseCallback<ContactsResult>(context, false) {
                    @Override
                    protected void onResult(int code, ContactsResult result) {
                        if (result == null) return;
                        ArrayList<Contact> appContacts = result.getContacts();
                        DBUtils.saveAppContacts(context, appContacts);
                    }
                });
    }
}
