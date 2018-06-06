package com.myhailov.mykola.fishpay.activities.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.contacts.ContactDetailsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GoodsByIdResult;
import com.myhailov.mykola.fishpay.api.results.SearchedContactsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_ID;
import static com.myhailov.mykola.fishpay.utils.Utils.buildPhotoUrlGoods;

public class ReviewGoodsActivity extends BaseActivity {
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPrice;
    private TextView tvDescription;
    private TextView tvCategory;
    private SliderLayout sliderPhoto;

    private GoodsByIdResult goods;
    private long id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_goods);
        initCustomToolbar("Просмотр товара");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong(GOODS_ID, 0);
        }
        assignViews();
        getGoodsById(id + "");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_author:
                getUserInfo();
                break;
        }
    }


    private void assignViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvAuthor = findViewById(R.id.tv_author);
        tvPrice = findViewById(R.id.tv_price);
        tvDescription = findViewById(R.id.tv_description);
        tvCategory = findViewById(R.id.tv_category);
        sliderPhoto = findViewById(R.id.slider_image);
        findViewById(R.id.ivBack).setOnClickListener(this);
        tvAuthor.setOnClickListener(this);

    }

    private void setValue() {
        tvTitle.setText(goods.getTitle());
        tvDescription.setText(goods.getDescription());
        tvPrice.setText(Utils.pennyToUah(Integer.parseInt(goods.getPrice())));
        if (goods.getCategory() != null) {
            tvCategory.setText(goods.getCategory().getCategory());
        }
        tvAuthor.setText(goods.getUser().getFirstName() + " " + goods.getUser().getSecondName());

        sliderPhoto.addSlider(getSliderView(goods.getMainPhoto(), goods.getId()));
        if (goods.getPhotos() != null) {
            for (GoodsByIdResult.Photo s : goods.getPhotos()) {
                sliderPhoto.addSlider(getSliderView(s.getPhotoUrl(), s.getId()));
            }
        }
    }


    private TextSliderView getSliderView(String photo, int id) {
        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView.image(buildPhotoUrlGoods(photo, id))
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        return textSliderView;
    }

    private void getUserInfo() {
        if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else
            ApiClient.getApiInterface().searchContact(TokenStorage.getToken(context), goods.getUser().getPhone())
                    .enqueue(new BaseCallback<SearchedContactsResult>(context, true) {
                        @Override
                        protected void onResult(int code, SearchedContactsResult result) {
                            ArrayList<SearchedContactsResult.SearchedContact> contacts = result.getContacts();
                            if (contacts == null || contacts.size() < 1) return;
                            SearchedContactsResult.SearchedContact contact = contacts.get(0);
                            if (contact == null) return;
                            Intent intent = new Intent(context, ContactDetailsActivity.class);
                            intent.putExtra(Keys.SEARCHED_CONTACT, contact);
                            context.startActivity(intent);
                        }
                    });
    }

    private void getGoodsById(String id) {
        if (!Utils.isOnline(context)) {
            Utils.noInternetToast(context);
        } else {
            ApiClient.getApiInterface().getGoodsDetails(TokenStorage.getToken(context), id)
                    .enqueue(new BaseCallback<GoodsByIdResult>(context, true) {
                        @Override
                        protected void onResult(int code, GoodsByIdResult result) {
                            if (result != null) {
                                goods = result;
                                setValue();
                            }
                        }
                    });
        }
    }
}


