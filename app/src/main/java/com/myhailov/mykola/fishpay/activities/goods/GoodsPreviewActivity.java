package com.myhailov.mykola.fishpay.activities.goods;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.MyGoodsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.GoodsRequestBody;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static com.myhailov.mykola.fishpay.utils.Keys.GOODS;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.USER_PREFS;
import static com.myhailov.mykola.fishpay.utils.Utils.makeRequestBodyFile;

public class GoodsPreviewActivity extends BaseActivity {
    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvDescription;
    private SliderLayout sliderPhoto;

    private GoodsRequestBody goods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_preview);
        initCustomToolbar("Просмотр товара");
        if (getIntent() != null) {
            goods = (GoodsRequestBody) getIntent().getSerializableExtra(GOODS);
        }
        assignViews();
        setValue();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                createGoods();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }

    }

    private void assignViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvPrice = findViewById(R.id.tv_price);
        tvDescription = findViewById(R.id.tv_description);
        sliderPhoto = findViewById(R.id.slider_image);
        findViewById(R.id.tv_save).setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);

    }

    private void setValue() {
        tvTitle.setText(goods.getTitle());
        tvDescription.setText(goods.getDescription());
        tvPrice.setText(Utils.pennyToUah(Integer.parseInt(goods.getPrice())));

        sliderPhoto.addSlider(getSliderView(goods.getMainPhoto()));
        if (goods.getPhotos() != null) {
            for (String s : goods.getPhotos()) {
                sliderPhoto.addSlider(getSliderView(s));
            }
        }
    }

    private void createGoods() {
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
        } else {
            ApiClient.getApiClient().createGoods(TokenStorage.getToken(context),
                    Utils.makeRequestBody(goods.getTitle()),
                    Utils.makeRequestBody(goods.getDescription()),
                    Utils.makeRequestBody(goods.getPrice()),
                    Utils.makeRequestBody(goods.getCategoryId()),
                    Utils.makeRequestBody(goods.getVisibility()),
                    makeRequestBodyFile(Uri.parse(goods.getMainPhoto()), "main_photo"))
                    .enqueue(new BaseCallback<Object>(context, true) {
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
                            for (String photo : goods.getPhotos()) {
                                uploadGoodsPhoto(photo, id);
                            }
                            Intent intent = new Intent(context, MyGoodsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    });
        }
    }

    private void uploadGoodsPhoto(String photo, String id) {
        ApiClient.getApiClient().uploadGoodsPhoto(TokenStorage.getToken(context),
                id,
                makeRequestBodyFile(Uri.parse(photo)))
                .enqueue(new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, Object result) {

                    }
                });
    }

    private TextSliderView getSliderView(String path) {
        File imgFile = new File(path);
        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView.image(imgFile)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        return textSliderView;
    }
}
