package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

public class CategoryResult {
    @SerializedName("id")
    Integer id;
    @SerializedName("name_key")
    String category;
    private boolean checked;

    public CategoryResult(Integer id, String category) {
        this.id = id;
        this.category = category;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

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
