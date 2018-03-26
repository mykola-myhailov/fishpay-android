package com.myhailov.mykola.fishpay.api.requestBodies;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 16.03.18.
 */

public class SelectedGoods {

    private int count;

    public SelectedGoods(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public int getGoods_id() {
        return id;
    }

    public int getCount() {
        return count;
    }

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private long userId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("main_photo")
    private String mainPhoto;

    @SerializedName("status")
    private String status;

    @SerializedName("visibility")
    private boolean visibility;

    @SerializedName("price")
    private String price;

    @SerializedName("category")
    private String category;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public String getStatus() {
        return status;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }
}
