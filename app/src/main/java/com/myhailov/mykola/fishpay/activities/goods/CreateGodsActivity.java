package com.myhailov.mykola.fishpay.activities.goods;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
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
import com.myhailov.mykola.fishpay.adapters.CharityPhotoAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.GoodsRequestBody;
import com.myhailov.mykola.fishpay.api.results.CategoryResult;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.GOODS;

public class CreateGodsActivity extends BaseActivity {

    private List<String> categories = new ArrayList();
    private List<String> photos = new ArrayList();
    private GoodsRequestBody goods = new GoodsRequestBody();
    private EditText etGoodsName, etPrice, etDescription;
    private RecyclerView rvPhoto;
    private ImageView ivPhoto;
    private ImageView ivDeleteMainPhoto;
    private Spinner categorySpinner;
    private SwitchCompat switchStatus;
    private static String PUBLIC = "PUBLIC", PRIVATE = "PRIVATE";
    private boolean mainPhotoPick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gods);
        getCategory();
        initCustomToolbar("Создать товар");
        initViews();
    }


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
        rvPhoto = findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ivDeleteMainPhoto = findViewById(R.id.iv_delete);


        findViewById(R.id.ivBack).setOnClickListener(this);
        ivDeleteMainPhoto.setOnClickListener(this);
        findViewById(R.id.tv_add_photo).setOnClickListener(this);

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
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_add_photo:
                if (photos.size() < 8) {
                    mainPhotoPick = false;
                    ImagePicker.pickImage(this, "Select your image:");
                } else {
                    toast("Больше выбрать нельзя");
                }
                break;
            case R.id.iv_delete:
                ivDeleteMainPhoto.setVisibility(View.GONE);
                ivPhoto.setImageResource(R.drawable.camera);
                break;
            case R.id.tvChangePhoto:
                mainPhotoPick = true;
                ImagePicker.pickImage(this, "Select your image:");
            case R.id.tvCreate:
                if (checkGoods()) {
                    setGoodsValue();
                    Intent intent = new Intent(context, GoodsPreviewActivity.class);
                    intent.putExtra(GOODS, goods);
                    startActivity(intent);
                }
                break;
        }
    }

    private boolean checkGoods() {


        if (TextUtils.isEmpty(etGoodsName.getText().toString()) || etGoodsName.getText().toString().length() < 5) {
            Utils.toast(context, "Имя товара должно состоять не менее чем из 5 символов");
            return false;
        }
        if (TextUtils.isEmpty(etPrice.getText().toString()) || etPrice.getText().toString().equals("0")) {
            Utils.toast(context, getString(R.string.enter_price));
            return false;
        }
        if (TextUtils.isEmpty(etDescription.getText().toString()) || etDescription.getText().toString().length() < 8) {
            Utils.toast(context, "Описание товара должно состоять не менее чем из 8 символов");
            return false;
        }

        if (TextUtils.isEmpty(goods.getMainPhoto())) {
            Utils.toast(context, "Выберите фото");
            return false;
        }
        return true;
    }

    private void setGoodsValue() {
        goods.setTitle(etGoodsName.getText().toString());
        goods.setPrice(Utils.UAHtoPenny(etPrice.getText().toString()) + "");
        goods.setDescription(etDescription.getText().toString());
        goods.setVisibility(switchStatus.isChecked() ? PRIVATE : PUBLIC);
        goods.setCategory(Integer.toString(categorySpinner.getSelectedItemPosition()));
        goods.setPhotos(photos);
    }

    private void getCategory() {
        ApiClient.getApiClient().getCategory(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<CategoryResult>>(context, false) {
                    @Override
                    protected void onResult(int code, ArrayList<CategoryResult> result) {

                        for (CategoryResult categoryResult : result) {
                            categories.add(categoryResult.getCategory());
                        }
                        initSpinner();
                    }
                });
    }

    private CharityPhotoAdapter.OnItemClickListener rvPhotoListener = new CharityPhotoAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            photos.remove(position);
            rvPhoto.setAdapter(new CharityPhotoAdapter(context, photos, rvPhotoListener));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE) {
            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            if (bitmap != null) {
                if (mainPhotoPick) {
                    ivPhoto.setImageBitmap(bitmap);
                    goods.setMainPhoto(createFile(bitmap, "main_photo").getPath());
                    ivDeleteMainPhoto.setVisibility(View.VISIBLE);
                } else {
                    photos.add(createFile(bitmap, "img").getPath());
                    rvPhoto.setAdapter(new CharityPhotoAdapter(context, photos, rvPhotoListener));
                }
            }
        }
    }

    private File createFile(Bitmap bitmap, String name) {
        File imageFile = null;
        try {
            imageFile = File.createTempFile(name + ".jpg", null, context.getCacheDir());
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
        assert imageFile != null;
        return imageFile;
    }
}
