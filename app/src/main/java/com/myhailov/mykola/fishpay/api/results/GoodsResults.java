package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wwaw on 29.01.18.
 */

public class GoodsResults {

    @SerializedName("id")
    private long id;

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
