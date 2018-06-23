package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mvc.imagepicker.ImagePicker;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.profile.CardsActivity;
import com.myhailov.mykola.fishpay.adapters.PhotoAdapter;
import com.myhailov.mykola.fishpay.api.requestBodies.CharityRequestBody;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_CREATE;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_RESULT;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;

public class CreateCharityActivity extends BaseActivity {
    private EditText etTitle, etCollectedAmount, etRequiredAmount, etDescription;
    private Switch switchIndefinitely;
    private ImageView ivMainPhoto, ivDeleteMainPhoto, ivCard;
    private TextView tvCardName, tvCardNumber, tvChangePhoto, tvAddMainPhoto, tvAddSecondaryPhoto, tvAddPhoto, tvAddCard;
    private RecyclerView rvPhoto;

    private Card card;
    private boolean mainPhotoPick = false;

    private List<String> photos = new ArrayList<>();
    private CharityRequestBody charity = new CharityRequestBody();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_charity);
        initCustomToolbar(getString(R.string.create_new));
        assignViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.iv_delete_main_photo:
                ivMainPhoto.setImageBitmap(null);
                ivMainPhoto.setVisibility(View.INVISIBLE);
                tvAddMainPhoto.setVisibility(View.VISIBLE);
                tvChangePhoto.setVisibility(View.INVISIBLE);
                ivDeleteMainPhoto.setVisibility(View.GONE);
                break;
            case R.id.tv_add_photo:
                if (photos.size() < 8) {
                    mainPhotoPick = false;
                    ImagePicker.pickImage(this, "Select your image:");
                } else {
                    toast(getString(R.string.you_cant_choose_more));
                }
                break;
            case R.id.tv_add_secondary_photo:
                mainPhotoPick = false;
                ImagePicker.pickImage(this, "Select your image:");
                break;
            case R.id.tv_add_main_photo:
                mainPhotoPick = true;
                ImagePicker.pickImage(this, "Select your image:");
                break;
            case R.id.tv_change_photo:
                mainPhotoPick = true;
                ImagePicker.pickImage(this, "Select your image:");
                break;
            case R.id.iv_card:
                startActivityForResult(new Intent(context, CardsActivity.class)
                        .putExtra(REQUEST, true), REQUEST_CARD);
                break;
            case R.id.tv_add_card:
                startActivityForResult(new Intent(context, CardsActivity.class)
                        .putExtra(REQUEST, true), REQUEST_CARD);
                break;
            case R.id.switch_indefinitely:
                if (switchIndefinitely.isChecked()) {
                    etRequiredAmount.setEnabled(false);
                    etRequiredAmount.setText("0");
                    etRequiredAmount.setError(null);
                } else {
                    etRequiredAmount.setEnabled(true);
                }
                break;
            case R.id.tv_create:
                if (checkViews()) {
                    setCharity();
                    Intent intent = new Intent(context, CharitySettingsActivity.class);
                    intent.putExtra(CHARITY_CREATE, true);
                    intent.putExtra(CHARITY_RESULT, charity);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CARD && resultCode == RESULT_OK) {
            card = data.getParcelableExtra(CARD);
            if (card != null) {
                tvCardName.setText(card.getName());
                tvCardNumber.setText("|" + card.getLastFourNumbers());
                tvCardName.setVisibility(View.VISIBLE);
                tvCardNumber.setVisibility(View.VISIBLE);
                tvAddCard.setVisibility(View.GONE);
            } else {

            }
        }
        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE) {
            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            if (bitmap != null) {
                if (mainPhotoPick) {
                    ivMainPhoto.setImageBitmap(bitmap);
                    charity.setMainPhoto(createFile(bitmap, "main_photo").getPath());
                    ivDeleteMainPhoto.setVisibility(View.VISIBLE);
                    ivMainPhoto.setVisibility(View.VISIBLE);
                    tvChangePhoto.setVisibility(View.VISIBLE);
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

    private void assignViews() {
        etTitle = findViewById(R.id.et_title);
        etRequiredAmount = findViewById(R.id.et_total_amount);
        etCollectedAmount = findViewById(R.id.et_collected_amount);
        etDescription = findViewById(R.id.et_description);
        switchIndefinitely = findViewById(R.id.switch_indefinitely);
        ivCard = findViewById(R.id.iv_card);
        tvCardName = findViewById(R.id.tv_card_name);
        tvCardNumber = findViewById(R.id.tv_card_number);
        tvChangePhoto = findViewById(R.id.tv_change_photo);
        ivMainPhoto = findViewById(R.id.iv_main_photo);
        ivDeleteMainPhoto = findViewById(R.id.iv_delete_main_photo);
        rvPhoto = findViewById(R.id.rv_photo);
        tvAddMainPhoto = findViewById(R.id.tv_add_main_photo);
        tvAddSecondaryPhoto = findViewById(R.id.tv_add_secondary_photo);
        tvAddPhoto = findViewById(R.id.tv_add_photo);
        tvAddCard = findViewById(R.id.tv_add_card);
        initRvPhoto();

        tvAddCard.setOnClickListener(this);
        tvAddSecondaryPhoto.setOnClickListener(this);
        tvAddMainPhoto.setOnClickListener(this);
        tvChangePhoto.setOnClickListener(this);
        ivCard.setOnClickListener(this);
        switchIndefinitely.setOnClickListener(this);
        findViewById(R.id.tv_create).setOnClickListener(this);
        ivDeleteMainPhoto.setOnClickListener(this);
        tvAddPhoto.setOnClickListener(this);
    }

    private void initRvPhoto() {
        rvPhoto.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

    }

    private boolean checkViews() {
        if (etTitle.getText().length() < 5) {
            toast(getString(R.string.error_fill_information));
            etTitle.setError(getString(R.string.error_county, 5));
            scrollToView(etTitle);
            return false;
        }
        if (!switchIndefinitely.isChecked()) {
            if (etRequiredAmount.getText().toString().equals("0") || TextUtils.isEmpty(etRequiredAmount.getText())) {
                toast(getString(R.string.enter_amount));
                etRequiredAmount.setError(getString(R.string.error_fill_information));
                scrollToView(etRequiredAmount);
                return false;
            }
        }
        if (etDescription.getText().length() < 15) {
            etDescription.setError(getString(R.string.error_county, 15));
            toast(getString(R.string.enter_description));
            scrollToView(etDescription);
            return false;
        }
        if (TextUtils.isEmpty(etCollectedAmount.getText())) {
            etCollectedAmount.setText("0");
        }
        if (card == null) {
            scrollToView(ivCard);
            toast(getString(R.string.choose_card));
            return false;
        }
        if (TextUtils.isEmpty(charity.getMainPhoto())) {
            scrollToView(ivMainPhoto);
            toast(getString(R.string.select_a_photo));
            return false;
        }

        return true;
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


    private void setCharity() {
        charity.setTitle(etTitle.getText().toString());
        charity.setInitCollectedAmount(Utils.UAHtoPenny(etCollectedAmount.getText().toString()) + "");
        charity.setDescription(etDescription.getText().toString());
        charity.setRequiredAmount(Utils.UAHtoPenny(etRequiredAmount.getText().toString()) + "");
        charity.setMembersVisibility("true");
        charity.setItemVisibility("PUBLIC");
        charity.setUserCardId(card.getId());
        charity.setPhotos(photos);
    }

    private void scrollToView(View v) {
        v.getParent().requestChildFocus(v, v);
    }

}
