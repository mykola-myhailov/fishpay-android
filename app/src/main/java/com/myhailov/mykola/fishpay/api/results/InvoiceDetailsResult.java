package com.myhailov.mykola.fishpay.api.results;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mykola Myhailov  on 29.03.18.
 */
public class InvoiceDetailsResult {

    @SerializedName ("request_id")
    private String id;

    @SerializedName("pan_masked")
    private String pan_masked;

    @SerializedName("requester")
    private Contact requester;

    @SerializedName("amount")
    private int amount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("status_changed_at")
    private String statusChangedAt;

    @SerializedName("comment")
    private String comment;

    @SerializedName("status")
    private String status;
    @SerializedName("goods")
    private List<GoodsRequest> goods = null;

    public List<GoodsRequest> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsRequest> goods) {
        this.goods = goods;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPan_masked(String pan_masked) {
        this.pan_masked = pan_masked;
    }

    public void setRequester(Contact requester) {
        this.requester = requester;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatusChangedAt() {
        return statusChangedAt;
    }

    public void setStatusChangedAt(String statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    @NonNull
    public String getPan_masked() {
        if (pan_masked == null) return "";
        return pan_masked;
    }

    public Contact getRequester() {
        return requester;
    }

    @NonNull
    public int getAmount() {
        return amount;
    }

    @NonNull
    public String getComment() {
        if (comment == null) return "";
        return comment;
    }

    public String getStatus() {
        return status;
    }

    public class Contact {


        @SerializedName("id")
        private String id;

        @SerializedName("ful_name")
        private String name;


        @SerializedName("phone")
        private String phone;

        @SerializedName("photo_url")
        private String photo;

        public String getId() {
            return id;
        }

        @NonNull
        public String getName() {
            if (name == null) return "";
            return name;
        }

        @NonNull
        public String getPhone() {
            if (phone == null) return "";
            return phone;
        }


    public String getPhoto() {
            return photo;
        }


    }

    public class GoodsRequest{
        @SerializedName("money_request_id")
        private long moneyRequestId;
        @SerializedName("goods_id")
        private long goodsId;
        @SerializedName("count")
        private int count = 0;
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
        @SerializedName("price")
        private Integer price;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("category")
        private String category;
        @SerializedName("status")
        private String status;
        @SerializedName("visibility")
        private String visibility;


        public long getMoneyRequestId() {
            return moneyRequestId;
        }

        public void setMoneyRequestId(long moneyRequestId) {
            this.moneyRequestId = moneyRequestId;
        }

        public long getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(long goodsId) {
            this.goodsId = goodsId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMainPhoto() {
            return mainPhoto;
        }

        public void setMainPhoto(String mainPhoto) {
            this.mainPhoto = mainPhoto;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }
    }



/*
    "result": {


    }
        "request_id": 114,
                "pan_masked": "not_found",
                "amount": 800,
                "comment": null,
                "status": "ACTIVE",
                "created_at": "2018-03-27 13:39:37",
                "status_changed_at": "2018-03-27 13:39:37",
                "requester": {
            "id": 133,
                    "ful_name": "yĝtyhg ttfgh",
                    "contact_full_name": " ",
                    "photo_url": "28baf49117081a1eee95c85f3477bab2.tmp",
                    "phone": "380123456789",
                    "requester_contact_id": null
        },
        "addressee": {
            "id": 133,
                    "photo_url": "28baf49117081a1eee95c85f3477bab2.tmp",
                    "ful_name": "yĝtyhg ttfgh",
                    "contact_full_name": " ",
                    "phone": "380123456789",
                    "addressee_contact_id": null
        },
        "goods": [
        {
            "money_request_id": 114,
                "goods_id": 123,
                "count": 30,
                "id": null,
                "user_id": null,
                "title": null,
                "description": null,
                "main_photo": null,
                "price": null,
                "created_at": null,
                "updated_at": null,
                "category": null,
                "visibility": null,
                "status": null
        },
        {
            "money_request_id": 114,
                "goods_id": 324,
                "count": 2,
                "id": null,
                "user_id": null,
                "title": null,
                "description": null,
                "main_photo": null,
                "price": null,
                "created_at": null,
                "updated_at": null,
                "category": null,
                "visibility": null,
                "status": null
        }*/
}
