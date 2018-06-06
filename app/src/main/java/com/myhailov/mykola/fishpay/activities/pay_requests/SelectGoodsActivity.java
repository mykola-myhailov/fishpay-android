package com.myhailov.mykola.fishpay.activities.pay_requests;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.SelectedGoods;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

public class SelectGoodsActivity extends BaseActivity{

    private ArrayList<SelectedGoods> selectedGoods = new ArrayList<>();
    private RecyclerView recyclerView;
    private GoodsAdapter goodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_goods);

        setContentView(R.layout.activity_drawer_my_goods);
        initToolBar("Выберите товары");

        findViewById(R.id.ivPlus).setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ApiClient.getApiInterface()
                .getUserGoods(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GoodsResults>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GoodsResults> result) {

                        for (GoodsResults product: result) {

                        }
                        goodsAdapter = new GoodsAdapter(selectedGoods);
                        recyclerView.setAdapter(goodsAdapter);
                    }
                });
    }

    @Override
    public void onClick(View view) {

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


    class GoodsAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<SelectedGoods> goods;


        GoodsAdapter(ArrayList<SelectedGoods> goods) {
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
            SelectedGoods item = goods.get(position);
            holder.tvTitle.setText(item.getGoods().getTitle());
            holder.tvPrice.setText(item.getGoods().getPrice());
            Utils.displayGoods(context, holder.ivPhoto, item.getGoods().getMainPhoto(), item.getGoods().getId());

        }

        @Override
        public int getItemCount() {
            return goods.size();
        }
    }
}
