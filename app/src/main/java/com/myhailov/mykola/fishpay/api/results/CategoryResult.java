package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

public class CategoryResult {
    @SerializedName("id")
    Integer id;
    @SerializedName("name_key")
    String category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
