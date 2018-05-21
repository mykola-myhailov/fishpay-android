package com.myhailov.mykola.fishpay.activities.goods;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
    private Spinner categorySpinner;
    private SwitchCompat switchStatus;
    private static  String PUBLIC = "PUBLIC", PRIVATE = "PRIVATE";

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
        final TextView tvStatus = findViewById(R.id.tvStatus);
        switchStatus = findViewById(R.id.switchStatus);
        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) tvStatus.setText("Приватный");
                    else tvStatus.setText("Публичный");
            }
        });
        etGoodsName = findViewById(R.id.etGoodsName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        ivPhoto = findViewById(R.id.ivMainPhoto);
        initSpinner();
    }

    private void initSpinner() {
        categorySpinner = findViewById(R.id.categorySpinner);
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
                String price = etPrice.getText().toString();
                String descripton = etDescription.getText().toString();
                if (name.equals(""))  Utils.toast(context, getString(R.string.enter_name));
                else if (name.length() < 5) Utils.toast(context, "Имя товара должно состоять не менее чем из 5 символов");
                else if (price.equals("") || price.equals("0")) Utils.toast(context, getString(R.string.enter_price));
                else if (descripton.equals("")) Utils.toast(context, "Введите описание товара");
                else if (descripton.length() < 8) Utils.toast(context, "Описание товара должно состоять не менее чем из 8 символов");
                else if (imageUri == null) Utils.toast(context, "Выберите фото");
                else if (!Utils.isOnline(context))Utils.noInternetToast(context);
                else {
                    String visibility = switchStatus.isChecked()? PRIVATE : PUBLIC;
                    String categoryId = Integer.toString(categorySpinner.getSelectedItemPosition());
                    ApiClient.getApiClient().createGoods(TokenStorage.getToken(context),
                            Utils.makeRequestBody(name),
                            Utils.makeRequestBody(descripton),
                            Utils.makeRequestBody(price),
                            Utils.makeRequestBody(categoryId),
                            Utils.makeRequestBody(visibility),
                            makeRequestBodyFile(imageUri)).enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            context.startActivity(new Intent(context, MyGoodsActivity.class));
                        }
                    });
                }
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
