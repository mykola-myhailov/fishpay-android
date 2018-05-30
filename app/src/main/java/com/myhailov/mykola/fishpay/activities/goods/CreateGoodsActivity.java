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
import com.myhailov.mykola.fishpay.activities.MyGoodsActivity;
import com.myhailov.mykola.fishpay.adapters.EditPhotoAdapter;
import com.myhailov.mykola.fishpay.adapters.PhotoAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.GoodsRequestBody;
import com.myhailov.mykola.fishpay.api.results.CategoryResult;
import com.myhailov.mykola.fishpay.api.results.GoodsByIdResult;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.GOODS;
import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_ID;

public class CreateGoodsActivity extends BaseActivity {
    private static final String PUBLIC = "PUBLIC", PRIVATE = "PRIVATE";

    private List<String> categories = new ArrayList();
    private List<String> photos = new ArrayList();

    private TextView tvAddMainPhoto, tvChangeMainPhoto, tvAddSecondaryPhoto, tvAddPhoto;
    private EditText etGoodsName, etPrice, etDescription;
    private RecyclerView rvPhoto;
    private ImageView ivPhoto;
    private ImageView ivDeleteMainPhoto;
    private Spinner categorySpinner;
    private SwitchCompat switchStatus;

    private GoodsRequestBody goods = new GoodsRequestBody();
    private GoodsByIdResult goodsById;
    private boolean mainPhotoPick = false;
    private long goodsId;
    private boolean createNewGoods = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gods);
        getCategory();

        initViews();
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(GOODS_ID)) {
            createNewGoods = false;
            goodsId = extras.getLong(GOODS_ID, 0);
            getGoodsById(goodsId + "");
            initCustomToolbar("Редактировать товар");
            ((TextView) findViewById(R.id.tvCreate)).setText(getString(R.string.save));
        } else {
            createNewGoods = true;
            initCustomToolbar("Создать товар");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_add_main_photo:
                if (createNewGoods) {
                    mainPhotoPick = true;
                    ImagePicker.pickImage(this, "Select your image:");
                }
                break;
            case R.id.tv_add_photo:
                if (photos.size() < 8) {
                    mainPhotoPick = false;
                    ImagePicker.pickImage(this, "Select your image:");
                } else {
                    toast("Больше выбрать нельзя");
                }
                break;
            case R.id.tv_add_secondary_photo:
                if (createNewGoods) {
                    mainPhotoPick = false;
                    ImagePicker.pickImage(this, "Select your image:");
                }

                break;
            case R.id.iv_delete_main_photo:
                ivDeleteMainPhoto.setVisibility(View.GONE);
                ivPhoto.setVisibility(View.INVISIBLE);
                tvChangeMainPhoto.setVisibility(View.INVISIBLE);
                tvAddMainPhoto.setVisibility(View.VISIBLE);
                ivDeleteMainPhoto.setVisibility(View.GONE);
                break;
            case R.id.tvChangePhoto:
                mainPhotoPick = true;
                ImagePicker.pickImage(this, "Select your image:");
            case R.id.tvCreate:
                if (checkGoods()) {
                    setGoodsValue();
                    if (createNewGoods) {
                        Intent intent = new Intent(context, GoodsPreviewActivity.class);
                        intent.putExtra(GOODS, goods);
                        startActivity(intent);
                    } else {
                        if (checkGoods()) {
                            editGoods();
                        }
                    }

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE) {
            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            if (bitmap != null) {
                if (mainPhotoPick) {
                    ivPhoto.setImageBitmap(bitmap);
                    goods.setMainPhoto(createFile(bitmap, "main_photo").getPath());
                    ivDeleteMainPhoto.setVisibility(View.VISIBLE);
                    ivPhoto.setVisibility(View.VISIBLE);
                    tvChangeMainPhoto.setVisibility(View.VISIBLE);
                    tvAddMainPhoto.setVisibility(View.GONE);
                } else {
                    if (tvAddPhoto.getVisibility() != View.VISIBLE) {
                        rvPhoto.setVisibility(View.VISIBLE);
                        tvAddPhoto.setVisibility(View.VISIBLE);
                        tvAddSecondaryPhoto.setVisibility(View.INVISIBLE);
                    }
                    photos.add(createFile(bitmap, "img").getPath());
                    rvPhoto.setAdapter(new PhotoAdapter(context, photos, rvPhotoListener));
                }
            }
        }
    }

    private void initViews() {
        tvChangeMainPhoto = findViewById(R.id.tvChangePhoto);
        tvAddMainPhoto = findViewById(R.id.tv_add_main_photo);
        ivDeleteMainPhoto = findViewById(R.id.iv_delete_main_photo);
        tvAddSecondaryPhoto = findViewById(R.id.tv_add_secondary_photo);
        tvAddPhoto = findViewById(R.id.tv_add_photo);

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
        etPrice = findViewById(R.id.etPrice_from);
        ivPhoto = findViewById(R.id.iv_main_photo);
        rvPhoto = findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        tvAddPhoto.setOnClickListener(this);
        tvAddSecondaryPhoto.setOnClickListener(this);
        tvChangeMainPhoto.setOnClickListener(this);
        tvAddMainPhoto.setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
        ivDeleteMainPhoto.setOnClickListener(this);
        findViewById(R.id.tv_add_photo).setOnClickListener(this);
        ivDeleteMainPhoto = findViewById(R.id.iv_delete_main_photo);
        tvAddPhoto = findViewById(R.id.tv_add_photo);
        findViewById(R.id.tvCreate).setOnClickListener(this);

    }

    private void initSpinner() {
        categorySpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.item_spinner, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private boolean checkGoods() {
        if (TextUtils.isEmpty(etGoodsName.getText().toString()) || etGoodsName.getText().toString().length() < 5) {
            Utils.toast(context, "Имя товара должно состоять не менее чем из 5 символов");
            scrollToView(etGoodsName);
            return false;
        }
        if (TextUtils.isEmpty(etPrice.getText().toString()) || etPrice.getText().toString().equals("0")) {
            Utils.toast(context, getString(R.string.enter_price));
            scrollToView(etPrice);
            return false;
        }
        if (TextUtils.isEmpty(etDescription.getText().toString()) || etDescription.getText().toString().length() < 8) {
            Utils.toast(context, "Описание товара должно состоять не менее чем из 8 символов");
            scrollToView(etDescription);
            return false;
        }

        if (createNewGoods && TextUtils.isEmpty(goods.getMainPhoto())) {
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
        goods.setCategoryId(Integer.toString(categorySpinner.getSelectedItemPosition() + 1));
        goods.setCategory(categorySpinner.getSelectedItem().toString());
        goods.setPhotos(photos);
    }

    private void setValue() {
        if (goodsById.getCategory() != null) {
            categorySpinner.setSelection(goodsById.getCategory().getId() - 1);
        }
        switchStatus.setChecked(goodsById.getVisibility().equals(PRIVATE));
        etGoodsName.setText(goodsById.getTitle());
        etGoodsName.setEnabled(false);
        etPrice.setText(Utils.pennyToUah(Integer.parseInt(goodsById.getPrice())));
        etDescription.setText(goodsById.getDescription());

        Utils.displayGoods(context, ivPhoto, goodsById.getMainPhoto(), goodsById.getId());
        ivPhoto.setVisibility(View.VISIBLE);
        tvAddMainPhoto.setVisibility(View.GONE);

        rvPhoto.setAdapter(new EditPhotoAdapter(context, goodsById.getPhotos(), null));
        rvPhoto.setVisibility(View.VISIBLE);
        tvAddSecondaryPhoto.setVisibility(View.INVISIBLE);
    }

    private void getGoodsById(String id) {
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
        } else {
            ApiClient.getApiClient().getGoodsDetails(TokenStorage.getToken(context), id)
                    .enqueue(new BaseCallback<GoodsByIdResult>(context, true) {
                        @Override
                        protected void onResult(int code, GoodsByIdResult result) {
                            if (result != null) {
                                goodsById = result;
                                setValue();
                            }
                        }
                    });
        }
    }

    private void editGoods() {
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
        } else {
            ApiClient.getApiClient().editGoods(TokenStorage.getToken(context),
                    goodsId + "", etDescription.getText().toString(),
                    Utils.UAHtoPenny(etPrice.getText().toString()) + "",
                    Integer.toString(categorySpinner.getSelectedItemPosition() + 1),
                    switchStatus.isChecked() ? PRIVATE : PUBLIC)
                    .enqueue(new BaseCallback<Object>(context, true) {
                        @Override
                        protected void onResult(int code, Object result) {
                            toast("Товар изменен");
                            startActivity(new Intent(context, MyGoodsActivity.class));
                            finish();
                        }
                    });
        }
    }

    private void getCategory() {
        ApiClient.getApiClient().getCategory(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<CategoryResult>>(context, false) {
                    @Override
                    protected void onResult(int code, ArrayList<CategoryResult> result) {
                        if (result == null) return;
                        for (CategoryResult categoryResult : result) {
                            categories.add(categoryResult.getCategory());
                        }
                        initSpinner();
                    }
                });
    }

    private PhotoAdapter.OnItemClickListener rvPhotoListener = new PhotoAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            photos.remove(position);
            if (photos.size() == 0) {
                rvPhoto.setVisibility(View.INVISIBLE);
                tvAddPhoto.setVisibility(View.GONE);
                tvAddSecondaryPhoto.setVisibility(View.VISIBLE);
            }
            rvPhoto.setAdapter(new PhotoAdapter(context, photos, rvPhotoListener));
        }
    };


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

    private void scrollToView(View v) {
        v.getParent().requestChildFocus(v, v);
    }
}
