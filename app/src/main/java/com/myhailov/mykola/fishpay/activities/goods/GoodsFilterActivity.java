package com.myhailov.mykola.fishpay.activities.goods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.adapters.CategoryAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.CategoryResult;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Keys.CATEGORY;

public class GoodsFilterActivity extends BaseActivity {

    private RecyclerView rvCategory;
    private EditText etStartPrice, etEndPrice;
    private TextView tvOk;

    private List<String> categories = new ArrayList();
    private List<CategoryResult> categoriesWithID = new ArrayList();
    private CategoryAdapter.OnItemClickListener categoryListener = new CategoryAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(CheckedTextView ctv, int id, int position) {
            clearFocus();
            if (ctv.isChecked()) {
                ctv.setChecked(false);
                categoriesWithID.get(position).setChecked(false);
                categories.remove(id + "");
            } else {
                ctv.setChecked(true);
                setChecked(id, position);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_filter);
        getCategory();
        initCustomToolbar("Фильтр");

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_ok:
                Intent intent = new Intent();
                intent.putStringArrayListExtra(CATEGORY, (ArrayList<String>) categories);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }


    private void initViews() {
        rvCategory = findViewById(R.id.rv_category);
        rvCategory.setLayoutManager(new LinearLayoutManager(context));

        tvOk = findViewById(R.id.tv_ok);
        etStartPrice = findViewById(R.id.etPrice_from);
        etEndPrice = findViewById(R.id.etPrice_to);
        clearFocus();

        tvOk.setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private void setChecked(int id, int position) {
        if (id == -1) {
            categories.clear();
            for (CategoryResult categoryResult : categoriesWithID) {
                categoryResult.setChecked(false);
            }
            categoriesWithID.get(0).setChecked(true);
            rvCategory.getAdapter().notifyDataSetChanged();
        } else {
            categoriesWithID.get(0).setChecked(false);
            categoriesWithID.get(position).setChecked(true);
            rvCategory.getAdapter().notifyDataSetChanged();
            categories.add(id + "");
        }
    }

    private void clearFocus() {
        etEndPrice.clearFocus();
        etStartPrice.clearFocus();
    }

    private void getCategory() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().getCategory(TokenStorage.getToken(context))
                    .enqueue(new BaseCallback<ArrayList<CategoryResult>>(context, false) {
                        @Override
                        protected void onResult(int code, ArrayList<CategoryResult> result) {
                            if (result == null) return;
                            categoriesWithID.clear();
                            categoriesWithID.add(new CategoryResult(-1, getString(R.string.all)));
                            if (result.size() != 0) {
                                categoriesWithID.addAll(result);
                                rvCategory.setAdapter(new CategoryAdapter(context, categoriesWithID,
                                        categoryListener));
                            }
                        }
                    });
        } else {
            toast(getString(R.string.no_internet));
        }
    }
}
