package com.myhailov.mykola.fishpay.api.requestBodies;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;

/**
 * Created by Mykola Myhailov  on 16.03.18.
 */

public class SelectedGoods  {


    private int count;

    private GoodsResults goods;

    public SelectedGoods(GoodsResults goods) {
        this.count = 0;
        this.goods = goods;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public GoodsResults getGoods() {
        return goods;
    }


}
