package com.myhailov.mykola.fishpay.api.requestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CharityRequestBody implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("required_amount")
    private String requiredAmount;
    @SerializedName("init_collected_amount")
    private String initCollectedAmount;
    @SerializedName("description")
    private String description;
    @SerializedName("user_card_id")
    private String userCardId;
    @SerializedName("item_visibility")
    private String itemVisibility;
    @SerializedName("members_visibility")
    private String membersVisibility;
    @SerializedName("pseudonym")
    private String pseudonym;
    @SerializedName("main_photo")
    private String mainPhoto;
    private List<String> photos = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(String requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public String getInitCollectedAmount() {
        return initCollectedAmount;
    }

    public void setInitCollectedAmount(String initCollectedAmount) {
        this.initCollectedAmount = initCollectedAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(String userCardId) {
        this.userCardId = userCardId;
    }

    public String getItemVisibility() {
        return itemVisibility;
    }

    public void setItemVisibility(String itemVisibility) {
        this.itemVisibility = itemVisibility;
    }

    public String getMembersVisibility() {
        return membersVisibility;
    }

    public void setMembersVisibility(String membersVisibility) {
        this.membersVisibility = membersVisibility;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(String mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

//    public class CharityPhoto implements Serializable {
//        @SerializedName("img")
//        private String photo;
//        @SerializedName("id")
//        private String id;
//
//        public String getPhoto() {
//            return photo;
//        }
//
//        public void setPhoto(String photo) {
//            this.photo = photo;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//    }
}
