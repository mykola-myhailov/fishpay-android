package com.myhailov.mykola.fishpay.activities.goods;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mvc.imagepicker.ImagePicker;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.drawer.MyGoodsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreateGodsActivity extends BaseActivity {

    private String [] categories = {"Недвижимость", "Средства передвижения",
            "Игрушки", "Офисные товары", "Домашние товары",
            "Животные", "Елекнтроника", "Спорт", "Одежда", "Другое"};
    private EditText etGoodsName, etPrice, etDescription;
    private ImageView ivPhoto;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gods);
        getCategoriesRequest();
        initViews();
    }

    private void getCategoriesRequest() {

    };

    private void initViews() {
        findViewById(R.id.tvCreate).setOnClickListener(this);
        findViewById(R.id.tvChangePhoto).setOnClickListener(this);
        etGoodsName = findViewById(R.id.etGoodsName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        ivPhoto = findViewById(R.id.ivMainPhoto);
        initSpinner();
    }

    private void initSpinner() {
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvChangePhoto:
                ImagePicker.pickImage(this, "Select your image:"); // onActivityResult after picking image
            case R.id.tvCreate:
                String name = etGoodsName.getText().toString();
                if (name == null || name.equals("")) Utils.toast(context, getString(R.string.enter_name));
                String price = etPrice.getText().toString();
                if (price == null || price.equals("") || price.equals("0"))
                    Utils.toast(context, getString(R.string.enter_price));
                String descripton = etDescription.getText().toString();
                if (descripton == null) descripton = "";
                if (imageUri == null) Utils.toast(context, "Выберите фото");
                if (name == null || name.equals("")
                        ||price == null || price.equals("") || price.equals("0")
                    || (imageUri == null)) return;
                if (!Utils.isOnline(context)) {
                    Utils.noInternetToast(context);
                    return;
                }
                    ApiClient.getApiClient().createGoods(TokenStorage.getToken(context),
                            Utils.makeRequestBody(name),
                            Utils.makeRequestBody(descripton),
                            Utils.makeRequestBody(price),
                            Utils.makeRequestBody("2"),
                            Utils.makeRequestBody("true"),
                            makeRequestBodyFile(imageUri)).enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            context.startActivity(new Intent(context, MyGoodsActivity.class));
                        }
                    });
                break;
        }
    }

    private MultipartBody.Part makeRequestBodyFile(Uri imageUri) {
        if (imageUri == null) return null;
        File file = new File(imageUri.getPath());
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestFile = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData("main_photo", file.getName(), requestFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE) {
            // get new image, make file and upload to server
            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            if (bitmap != null){
                ivPhoto.setImageBitmap(bitmap);

                File imageFile = null;
                try {imageFile = File.createTempFile("avatar" + ".jpg", null, context.getCacheDir());}
                catch (IOException e) {e.printStackTrace();}
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
}
