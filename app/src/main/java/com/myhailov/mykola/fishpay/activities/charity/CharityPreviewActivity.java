package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.CharityRequestBody;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.io.File;
import java.text.DecimalFormat;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_RESULT;

public class CharityPreviewActivity extends BaseActivity {
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvAmount;
    private TextView tvPercent;
    private TextView tvDescription;
    private ImageView ivPhoto;

    private CharityRequestBody charity = new CharityRequestBody();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_preview);
        initCustomToolbar("Предпросмотр");
        if (getIntent() != null) {
            charity = (CharityRequestBody) getIntent().getSerializableExtra(CHARITY_RESULT);
        }

        assignViews();
        setValue();
    }

    private void assignViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvAuthor = findViewById(R.id.tv_author);
        tvAmount = findViewById(R.id.tv_amount);
        tvPercent = findViewById(R.id.tv_percent);
        tvDescription = findViewById(R.id.tv_description);
        ivPhoto = findViewById(R.id.iv_charity_avatar);
        findViewById(R.id.tv_contribution).setOnClickListener(this);

    }

    private void setValue() {
        tvTitle.setText(charity.getTitle());
        tvAmount.setText(Utils.pennyToUah(Integer.parseInt(charity.getRequiredAmount())));
        tvDescription.setText(charity.getDescription());
        if (charity.getInitCollectedAmount().equals("0")) {
            tvPercent.setVisibility(View.GONE);
        } else {

            double percent = (Double.parseDouble(charity.getInitCollectedAmount()) / Double.parseDouble(charity.getRequiredAmount())) * 100;
            tvPercent.setText(new DecimalFormat("#0.00").format(percent) + "%");
        }
        File imgFile = new File(charity.getMainPhoto());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ivPhoto.setImageBitmap(myBitmap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_contribution:
                createCharityRequest();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void createCharityRequest() {
        ApiClient.getApiClient().createCharity(TokenStorage.getToken(context),
                Utils.makeRequestBody(charity.getTitle()),
                Utils.makeRequestBody(charity.getRequiredAmount()),
                Utils.makeRequestBody(charity.getInitCollectedAmount()),
                Utils.makeRequestBody(charity.getDescription()),
                Utils.makeRequestBody(charity.getUserCardId()),
                Utils.makeRequestBody(charity.getItemVisibility()),
                Utils.makeRequestBody(charity.getMembersVisibility()),
                Utils.makeRequestBody(charity.getPseudonym()),
                Utils.makeRequestBodyFile(Uri.parse(charity.getMainPhoto()), "main_photo")
        ).enqueue(new BaseCallback<Object>(context, true) {
            @Override
            protected void onResult(int code, Object result) {
                if (code == 201) {
//                    for (CharityRequestBody.CharityPhoto charityPhoto : charity.getPhotos()) {
//                        uploadCharityPhoto(charityPhoto);
//                    }
                    Intent intent = new Intent(context, CharitySuccessActivity.class);
                    intent.putExtra(CHARITY_ID, result.toString());

                    context.startActivity(intent);
                } else {
                    Utils.toast(context, "Произошла ошибка");
                    Log.d("sss", "onResult: " + code);
                }
            }
        });
    }

    private void uploadCharityPhoto(CharityRequestBody.CharityPhoto photo) {
        // TODO: 16.05.2018 fix request
        ApiClient.getApiClient().uploadCharityPhoto(TokenStorage.getToken(context),
                photo.getId(),
                Utils.makeRequestBodyFile(Uri.parse(photo.getPhoto()), "main_photo"))
                .enqueue(new BaseCallback<Object>(this, true) {
                    @Override
                    protected void onResult(int code, Object result) {

                    }
                });
    }
}
