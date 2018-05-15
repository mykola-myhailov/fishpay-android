package com.myhailov.mykola.fishpay.api.requestBodies;

import com.google.gson.annotations.SerializedName;

import com.myhailov.mykola.fishpay.api.results.CharityResultById;

import java.io.Serializable;
import java.util.List;

public class CharityRequestBody implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("required_amount")
    private Integer requiredAmount;
    @SerializedName("init_collected_amount")
    private Integer initCollectedAmount;
    @SerializedName("description")
    private String description;
    @SerializedName("user_card_id")
    private Integer userCardId;
    @SerializedName("item_visibility")
    private String itemVisibility;
    @SerializedName("members_visibility")
    private String membersVisibility;
    @SerializedName("pseudonym")
    private String pseudonym;
    @SerializedName("main_photo")
    private String mainPhoto;
    private List<CharityPhoto> photos = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(Integer requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public Integer getInitCollectedAmount() {
        return initCollectedAmount;
    }

    public void setInitCollectedAmount(Integer initCollectedAmount) {
        this.initCollectedAmount = initCollectedAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(Integer userCardId) {
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

    public List<CharityPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<CharityPhoto> photos) {
        this.photos = photos;
    }

    public class CharityPhoto implements Serializable{
        @SerializedName("img")
        private String photo;
        @SerializedName("id")
        private String id;
    }
}
