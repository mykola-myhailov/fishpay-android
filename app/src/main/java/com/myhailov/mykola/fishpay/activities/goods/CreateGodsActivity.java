package com.myhailov.mykola.fishpay.activities.goods;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateGodsActivity extends BaseActivity {

    private String [] categories = {"Недвижимость", "Средства передвижения",
            "Игрушки", "Офисные товары", "Домашние товары",
            "Животные", "Елекнтроника", "Спорт", "Одежда", "Другое"};
    private EditText etGoodsName, etPrice, etDescription;

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
        etGoodsName = findViewById(R.id.etGoodsName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
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
        String name = etGoodsName.getText().toString();
        if (name == null || name.equals("")) Utils.toast(context, getString(R.string.enter_name));
        String price = etPrice.getText().toString();
        if (price == null || price.equals("") || price.equals("0"))
            Utils.toast(context, getString(R.string.enter_price));
        String descripton = etDescription.getText().toString();
        if (descripton == null) descripton = "";
        if (!Utils.isOnline(context)){
            Utils.noInternetToast(context);
            return;
        }

        ApiClient.getApiClient().createGoods(TokenStorage.getToken(context),
                Utils.makeRequestBody(name),
                Utils.makeRequestBody(descripton),
                Utils.makeRequestBody(price),
                null).enqueue(new BaseCallback<Object>(context, true) {
            @Override
            protected void onResult(int code, Object result) {

            }
        });

    }


}
