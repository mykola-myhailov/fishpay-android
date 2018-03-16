package com.myhailov.mykola.fishpay.api.requestBodies;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 16.03.18.
 */

public class Goods {
    @SerializedName("goods_id")
    private int goods_id;

    @SerializedName("count")
    private int count;

    public Goods(int goods_id, int count) {
        this.goods_id = goods_id;
        this.count = count;
    }
}
