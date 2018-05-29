package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.goods.CreateGoodsActivity;
import com.myhailov.mykola.fishpay.activities.goods.GoodsFilterActivity;
import com.myhailov.mykola.fishpay.activities.goods.ReviewGoodsActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

import static com.myhailov.mykola.fishpay.utils.Keys.CATEGORY;
import static com.myhailov.mykola.fishpay.utils.Keys.GOODS_ID;
import static com.myhailov.mykola.fishpay.utils.Keys.MAX_PRICE;
import static com.myhailov.mykola.fishpay.utils.Keys.MIN_PRICE;

public class MyGoodsActivity extends DrawerActivity {
    private static final int CODE_FILTER = 564;

    private ArrayList<GoodsResults> publicGoods = new ArrayList<>(), privateGoods = new ArrayList<>();
    private List<GoodsResults> filteredGoods = new ArrayList<>();
    private List<String> category;
    private String minPrice, maxPrice;
    private RecyclerView recyclerView;
    private ToggleSwitch toggleSwitch;
    private GoodsAdapter goodsAdapter;

    private TextView tvInform;
    private TextView tvInform2;

    private String filterQuery = "";
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

        initViews();
        sendRequestGoods();
        initSearchView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivPlus:
                context.startActivity(new Intent(context, CreateGoodsActivity.class));
                break;
            case R.id.tv_filter:
                Intent intent = new Intent(context, GoodsFilterActivity.class);
                intent.putExtra(MAX_PRICE, getMaxPrice());
                startActivityForResult(intent, CODE_FILTER);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_FILTER) {
            if (data != null) {
                category = data.getStringArrayListExtra(CATEGORY);
                minPrice = data.getStringExtra(MIN_PRICE);
                maxPrice = data.getStringExtra(MAX_PRICE);
                if (tabPosition == 0){
                    filterGoods(publicGoods);
                }else filterGoods(privateGoods);

//                Map<String, String> parameters = new HashMap<>();
//
//                parameters.put("visibility", "public");
//                parameters.put("priceFrom", Utils.UAHtoPenny(minPrice) + "");
//                parameters.put("priceTo", Utils.UAHtoPenny(maxPrice) + "");
//                publicGoods.clear();
//                getGoods(category, parameters);
//                parameters.put("visibility", "private");
//                getGoods(category, parameters);
            }
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        tvInform = findViewById(R.id.tv_clear);
        tvInform2 = findViewById(R.id.tv_clear2);
        findViewById(R.id.tv_filter).setOnClickListener(this);
    }


    private void filterGoods(List <GoodsResults> lists) {
        filteredGoods.clear();
        for (GoodsResults  list : lists) {
            if (list.getPrice() >= Utils.UAHtoPenny(minPrice) && list.getPrice() <= Utils.UAHtoPenny(maxPrice)) {
                if (!category.isEmpty()) {
                    for (String s : category) {
                        if (s.equals(list.getCategory())) {
                            filteredGoods.add(list);
                            break;
                        }
                    }
                } else {
                    filteredGoods.add(list);
                }
            }
        }
        recyclerView.setAdapter(new GoodsAdapter((ArrayList) filteredGoods));
    }

    private void initSearchView() {
        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterQuery = query;
                filter();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterQuery = newText;
                filter();
                return true;
            }
        });
        searchView.setQueryHint("Поиск по названию");
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.blue1));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    }

    public int getMaxPrice() {
        int maxPrice = 0;
        for (GoodsResults publicGood : publicGoods) {
            if (maxPrice < publicGood.getPrice()) {
                maxPrice = publicGood.getPrice();
            }
        }
        return maxPrice;
    }

    private void filter() {
        if (filterQuery == null || filterQuery.equals("")) {
            if (tabPosition == 0) {
                recyclerView.setAdapter(new GoodsAdapter(publicGoods));
            } else {
                recyclerView.setAdapter(new GoodsAdapter(privateGoods));
            }
            return;
        }
        if (tabPosition == 0) {
            filterList(publicGoods);
        } else {
            filterList(privateGoods);
        }

    }

    private void filterList(List<GoodsResults> goods) {
        List<GoodsResults> filteredGoods = new ArrayList<>();
        String search = filterQuery.toLowerCase();
        for (GoodsResults item : goods) {
            String title = item.getTitle().toLowerCase();
            if (title.contains(search)) {
                filteredGoods.add(item);
            }
        }
        recyclerView.setAdapter(new GoodsAdapter((ArrayList) filteredGoods));
    }

    private void sendRequestGoods() {
        publicGoods.clear();
        getGoods("public");
        getGoods("private");
        getUserGoods();
    }


    private void getGoods(List<String> categories, Map<String, String> parameters) {
        ApiClient.getApiClient()
                .getGoods(TokenStorage.getToken(context), categories, parameters)
                .enqueue(new BaseCallback<ArrayList<GoodsResults>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GoodsResults> result) {
                        if (result != null && result.size() != 0) {
                            publicGoods.addAll(result);
                            Collections.sort(publicGoods, new Comparator<GoodsResults>() {
                                @Override
                                public int compare(GoodsResults o1, GoodsResults o2) {
                                    if (o1.getCreatedAt() == null || o2.getCreatedAt() == null)
                                        return 0;
                                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                                }
                            });
                            goodsAdapter = new GoodsAdapter(publicGoods);
                            recyclerView.setAdapter(goodsAdapter);
                        }
                        if (publicGoods.isEmpty()) {
                            showListEmpty(true);
                        } else showListEmpty(false);

                    }
                });
    }


    private void getGoods(String visibility) {
        ApiClient.getApiClient()
                .getGoods(TokenStorage.getToken(context), visibility)
                .enqueue(new BaseCallback<ArrayList<GoodsResults>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GoodsResults> result) {
                        if (result != null && result.size() != 0) {
                            publicGoods.addAll(result);
                            Collections.sort(publicGoods, new Comparator<GoodsResults>() {
                                @Override
                                public int compare(GoodsResults o1, GoodsResults o2) {
                                    if (o1.getCreatedAt() == null || o2.getCreatedAt() == null)
                                        return 0;
                                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                                }
                            });
                            goodsAdapter = new GoodsAdapter(publicGoods);
                            recyclerView.setAdapter(goodsAdapter);
                        }
                        if (publicGoods.isEmpty()) {
                            showListEmpty(true);
                        } else showListEmpty(false);
                    }
                });
    }

    private void getUserGoods() {
        ApiClient.getApiClient()
                .getUserGoods(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<GoodsResults>>(context, true) {
                    @Override
                    protected void onResult(int code, ArrayList<GoodsResults> result) {
                        if (result != null && result.size() != 0) {
                            privateGoods.addAll(result);
                        }
                    }
                });
    }


    private void showListEmpty(boolean flag) {
        if (flag) {
            tvInform.setVisibility(View.VISIBLE);
            tvInform2.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvInform.setVisibility(View.GONE);
            tvInform2.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void deleteGoods(final long id) {
        ApiClient.getApiClient().deleteGoods(TokenStorage.getToken(context), id + "")
                .enqueue(new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, Object result) {
                        GoodsResults deleteGoods = new GoodsResults();
                        for (GoodsResults publicGood : publicGoods) {
                            if (publicGood.getId() == id) {
                                deleteGoods = publicGood;
                            }
                        }
                        publicGoods.remove(deleteGoods);
                        for (GoodsResults publicGood : privateGoods) {
                            if (publicGood.getId() == id) {
                                deleteGoods = publicGood;
                            }
                        }
                        privateGoods.remove(deleteGoods);
                        goodsAdapter.notifyDataSetChanged();
                        toast("Успешно удалено");

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
                    if (tabPosition != position) {
                        tabPosition = 0;
                        if (publicGoods.size() != 0) {
                            filter();
                            showListEmpty(false);
                        } else {
                            showListEmpty(true);
                        }
                    }
                } else {
                    if (tabPosition != position) {
                        tabPosition = 1;
                        if (privateGoods.size() != 0) {
                            filter();
                            showListEmpty(false);
                        } else {
                            showListEmpty(true);
                        }
                    }
                }
            }
        });
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeRevealLayout swipe_layout;
        private TextView tvPrice, tvTitle, tvDelete;
        private ImageView ivPhoto;
        private View container;

        public ViewHolder(View itemView) {
            super(itemView);
            swipe_layout = itemView.findViewById(R.id.swipe_layout);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            container = itemView.findViewById(R.id.container);
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

            holder.tvTitle.setText(item.getTitle());
            holder.tvPrice.setText(Utils.pennyToUah(item.getPrice()));
            Utils.displayGoods(context, holder.ivPhoto, item.getMainPhoto(), item.getId());

            viewBinderHelper.bind(holder.swipe_layout, item.getId() + "");
            if (item.getUserId() != id) {
                holder.swipe_layout.setLockDrag(true);
            } else {
                holder.swipe_layout.setLockDrag(false);
            }
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReviewGoodsActivity.class);
                    intent.putExtra(GOODS_ID, item.getId());
                    startActivity(intent);
                }
            });

            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteGoods(item.getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return goods.size();
        }
    }
}
