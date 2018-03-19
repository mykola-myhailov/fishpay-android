package com.myhailov.mykola.fishpay.api.requestBodies;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 16.03.18.
 */

public class SelectedGoods {
    private int goods_id;

    private int count;

    public SelectedGoods(int goods_id, int count) {
        this.goods_id = goods_id;
        this.count = count;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public int getCount() {
        return count;
    }
}
