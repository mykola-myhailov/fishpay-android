package com.myhailov.mykola.fishpay.activities.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.DrawerActivity;
import com.myhailov.mykola.fishpay.activities.goods.CreateGodsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class MyGoodsActivity extends DrawerActivity {

    private ArrayList<GoodsResults> publicGoods = new ArrayList<>(), privateGoods = new ArrayList<>();
    private RecyclerView recyclerView;
    private ToggleSwitch toggleSwitch;
    private GoodsAdapter goodsAdapter;

    private TextView tvInform;
    private TextView tvInform2;

    private long id;
    private int tabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_my_goods);

        initDrawerToolbar(getString(R.string.my_purchases));
        findViewById(R.id.ivPlus).setOnClickListener(this);
        id = getMyId();
        createDrawer();
        initToggleButtons();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        tvInform = findViewById(R.id.tv_clear);
        tvInform2 = findViewById(R.id.tv_clear2);

        getGoods();
        Log.d("sss", "onCreate: ");


    }

    private void getGoods() {
        ApiClient.getApiClient()
                .getGoods(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GoodsResults>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GoodsResults> result) {
                        if (result.size() != 0) {
                            for (GoodsResults product : result) {
                                if (product.isVisibility() || product.getUserId() == id) {
                                    publicGoods.add(product);
                                }
                                if (product.getUserId() == id) {
                                    privateGoods.add(product);
                                }
                            }
                            goodsAdapter = new GoodsAdapter(publicGoods);
                            recyclerView.setAdapter(goodsAdapter);
                        }else {
                            tvInform.setVisibility(View.VISIBLE);
                            tvInform2.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            toggleSwitch.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void deleteGoods(String id, final GoodsResults item) {
        ApiClient.getApiClient().deleteGoods(TokenStorage.getToken(context), id)
                .enqueue(new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, Object result) {
                        if (code == 202) {
                            publicGoods.remove(item);
                            privateGoods.remove(item);
                            goodsAdapter.notifyDataSetChanged();
                            toast("Удалено успешно");

                        }
                    }
                });
    }

    private long getMyId() {
        return Long.valueOf(context.getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, ""));
    }

    private void initToggleButtons() {
        toggleSwitch = findViewById(R.id.toggleSwitch);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Все");
        labels.add("Мои");
        toggleSwitch.setLabels(labels);
        toggleSwitch.setCheckedTogglePosition(0);
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener() {

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position == 0) {
                    tabPosition = 0;
                    goodsAdapter = new GoodsAdapter(publicGoods);
                    recyclerView.setAdapter(goodsAdapter);
                } else {
                    tabPosition = 1;
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
        private SwipeRevealLayout swipe_layout;
        private TextView tvPrice, tvTitle, tvDelete;
        private ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            swipe_layout = itemView.findViewById(R.id.swipe_layout);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvDelete = itemView.findViewById(R.id.tv_delete);
        }
    }

    private class GoodsAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<GoodsResults> goods;
        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


        public GoodsAdapter(ArrayList<GoodsResults> goods) {
            this.goods = goods;
            viewBinderHelper.setOpenOnlyOne(true);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.goods_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final GoodsResults item = goods.get(position);
            if (item.getUserId() != id) holder.swipe_layout.setLockDrag(true);

            holder.tvTitle.setText(item.getTitle());
            holder.tvPrice.setText(Utils.pennyToUah(item.getPrice()));
            Utils.displayGoods(context, holder.ivPhoto, item.getMainPhoto(), item.getId());
            viewBinderHelper.bind(holder.swipe_layout, item.getId() + "");

            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteGoods(item.getId() + "", item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return goods.size();
        }
    }
}
