package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.myhailov.mykola.fishpay.api.requestBodies.CharityRequestBody;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_CREATE;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_RESULT;
import static com.myhailov.mykola.fishpay.utils.Keys.REQUEST;

public class CreateCharityActivity extends BaseActivity {
    private EditText etTitle;
    private EditText etRequiredAmount;
    private EditText etCollectedAmount;
    private EditText etDescription;
    private Switch switchIndefinitely;
    private String pathPhoto;
    private ImageView ivCard;
    private ImageView ivMainPhoto;
    private TextView tvCardName;
    private TextView tvCardNumber;
    private TextView tvChangePhoto;

    private Card card;


    private CharityRequestBody charity = new CharityRequestBody();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_charity);
        initCustomToolbar("Создать благотворительный сбор");
        assignViews();
    }

    private void assignViews() {
        findViewById(R.id.tv_create).setOnClickListener(this);
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

        tvChangePhoto.setOnClickListener(this);
        ivCard.setOnClickListener(this);
        switchIndefinitely.setOnClickListener(this);
    }

    private boolean checkViews() {
        // TODO: 16.05.2018
        if (etTitle.getText().length() < 5) {
            etTitle.setError(getString(R.string.error_fill_information));
            return false;
        }
        if (!switchIndefinitely.isChecked()) {
            if (etRequiredAmount.getText().toString().equals("0") || TextUtils.isEmpty(etRequiredAmount.getText())) {
                etRequiredAmount.setError(getString(R.string.error_fill_information));
                return false;
            }
        }
        if (etDescription.getText().length() < 15) {
            etDescription.setError(getString(R.string.error_fill_information));
            return false;
        }
        if (TextUtils.isEmpty(etCollectedAmount.getText())) {
            etCollectedAmount.setText("0");
        }
        if (TextUtils.isEmpty(etCollectedAmount.getText())) {
            etCollectedAmount.setText("0");
        }
        if (card == null) {
            toast("Виберете карту");
            return false;
        }
        if (TextUtils.isEmpty(charity.getMainPhoto())) {
            toast("Виберете фото");
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CARD && resultCode == RESULT_OK) {
            card = data.getParcelableExtra(CARD);
            if (card != null) {
                tvCardName.setText(card.getName());
                tvCardNumber.setText(card.getLastFourNumbers());
            } else {

            }
        }
        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE) {
            // get new image, make file and upload to server
            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            if (bitmap != null) {
                ivMainPhoto.setImageBitmap(bitmap);

                File imageFile = null;
                try {
                    imageFile = File.createTempFile("main_photo" + ".jpg", null, context.getCacheDir());
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
                charity.setMainPhoto(imageFile.getPath());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_change_photo:
                ImagePicker.pickImage(this, "Select your image:");
                break;
            case R.id.iv_card:
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

    private void setCharity() {
        charity.setTitle(etTitle.getText().toString());
        charity.setInitCollectedAmount(Utils.UAHtoPenny(etCollectedAmount.getText().toString()) + "");
        charity.setDescription(etDescription.getText().toString());
        charity.setRequiredAmount(Utils.UAHtoPenny(etRequiredAmount.getText().toString()) + "");
        charity.setMembersVisibility("true");
        charity.setItemVisibility("PUBLIC");
        charity.setUserCardId(card.getId());
    }


}
