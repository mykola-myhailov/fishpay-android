package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.goods.CreateGodsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class MyGoodsActivity extends DrawerActivity {

    private ArrayList<GoodsResults> publicGoods = new ArrayList<>(), privateGoods = new ArrayList<>();
    private RecyclerView recyclerView;
    private GoodsAdapter goodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_drawer_my_goods);

        initDrawerToolbar(getString(R.string.my_purchases));
        findViewById(R.id.ivPlus).setOnClickListener(this);
        createDrawer();
        initToggleButtons();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ApiClient.getApiClient()
                .getGoods(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GoodsResults>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GoodsResults> result) {

                        for (GoodsResults product: result) {
                            if (product.isVisibility()) publicGoods.add(product);
                            else privateGoods.add(product);
                        }
                        goodsAdapter = new GoodsAdapter(publicGoods);
                        recyclerView.setAdapter(goodsAdapter);
                    }
                });
    }

    private void initToggleButtons() {
        ToggleSwitch toggleSwitch = findViewById(R.id.toggleSwitch);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Все");
        labels.add("Мои");;
        toggleSwitch.setLabels(labels);
        toggleSwitch.setCheckedTogglePosition(0);
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position == 0){
                    goodsAdapter = new GoodsAdapter(publicGoods);
                    recyclerView.setAdapter(goodsAdapter);
                }
                else {
                    goodsAdapter = new GoodsAdapter(privateGoods);
                    recyclerView.setAdapter(goodsAdapter);
                }
            }
        });
    }






    @Override
    public void onClick(View view) {
        context.startActivity(new Intent(context, CreateGodsActivity.class));
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPrice, tvTitle;
        private ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }

    private class GoodsAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<GoodsResults> goods;


        public GoodsAdapter(ArrayList<GoodsResults> goods) {
            this.goods = goods;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.goods_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            GoodsResults item = goods.get(position);
            holder.tvTitle.setText(item.getTitle());
            holder.tvPrice.setText(item.getPrice());
            Utils.displayGoods(context, holder.ivPhoto, item.getMainPhoto(), item.getId());

        }

        @Override
        public int getItemCount() {
            return goods.size();
        }
    }
}
