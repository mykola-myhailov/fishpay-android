package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.CharityRequestBody;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_RESULT;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.USER_PREFS;

public class CharityPreviewActivity extends BaseActivity {
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvAmount;
    private TextView tvPercent;
    private TextView tvDescription;
    private SliderLayout sliderPhoto;

    private CharityRequestBody charity = new CharityRequestBody();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_preview);
        initCustomToolbar(getString(R.string.preview));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            charity = (CharityRequestBody) extras.getSerializable(CHARITY_RESULT);
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
        sliderPhoto = findViewById(R.id.slider_image);
        findViewById(R.id.tv_contribution).setOnClickListener(this);

    }

    private void setValue() {
        tvTitle.setText(charity.getTitle());
        if (TextUtils.isEmpty(charity.getPseudonym())) {
            SharedPreferences preferences = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
            tvAuthor.setText(preferences.getString(PrefKeys.NAME, "") + " "
                    + preferences.getString(PrefKeys.SURNAME, ""));
        } else {
            tvAuthor.setText(charity.getPseudonym());
        }
        tvAmount.setText(Utils.pennyToUah(Integer.parseInt(charity.getRequiredAmount())));
        tvDescription.setText(charity.getDescription());
        if (charity.getRequiredAmount().equals("0")) {
            tvPercent.setVisibility(View.GONE);
        } else {
            double percent = (Double.parseDouble(charity.getInitCollectedAmount()) / Double.parseDouble(charity.getRequiredAmount())) * 100;
            tvPercent.setText(new DecimalFormat("#0.00").format(percent) + "%");
        }

        sliderPhoto.addSlider(getSliderView(charity.getMainPhoto()));
        if (charity.getPhotos() != null) {
            for (String s : charity.getPhotos()) {
                sliderPhoto.addSlider(getSliderView(s));
            }
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

    private TextSliderView getSliderView(String path) {
        File imgFile = new File(path);
        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView.image(imgFile)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        return textSliderView;
    }

    private void createCharityRequest() {
        ApiClient.getApiInterface().createCharity(TokenStorage.getToken(context),
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
                if (result == null) return;
                String id = "";
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    id = jsonObject.getInt("id") + "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (String charityPhoto : charity.getPhotos()) {
                    uploadCharityPhoto(charityPhoto, id);
                }
                Intent intent = new Intent(context, CharitySuccessActivity.class);
                intent.putExtra(CHARITY_ID, id);
                context.startActivity(intent);
            }
        });
    }

    private void uploadCharityPhoto(String photo, String id) {
        ApiClient.getApiInterface().uploadCharityPhoto(TokenStorage.getToken(context),
                id,
                Utils.makeRequestBodyFile(Uri.parse(photo)))
                .enqueue(new BaseCallback<Object>(this, true) {
                    @Override
                    protected void onResult(int code, Object result) {
                        if (result == null) return;
                    }
                });
    }
}
