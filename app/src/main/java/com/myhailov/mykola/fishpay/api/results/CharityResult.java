package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class CharityResult implements Serializable{
    @SerializedName("charity_program")
    private List<CharityProgram> charityProgram = null;
    @SerializedName("donation")
    private List<Donation> donation = null;
    @SerializedName("total_donation")
    private Integer totalDonation;

    public List<CharityProgram> getCharityProgram() {
        return charityProgram;
    }

    public void setCharityProgram(List<CharityProgram> charityProgram) {
        this.charityProgram = charityProgram;
    }

    public CharityResult withCharityProgram(List<CharityProgram> charityProgram) {
        this.charityProgram = charityProgram;
        return this;
    }

    public List<Donation> getDonation() {
        return donation;
    }

    public void setDonation(List<Donation> donation) {
        this.donation = donation;
    }

    public CharityResult withDonation(List<Donation> donation) {
        this.donation = donation;
        return this;
    }

    public Integer getTotalDonation() {
        return totalDonation;
    }

    public void setTotalDonation(Integer totalDonation) {
        this.totalDonation = totalDonation;
    }

    public class Donation implements Serializable{

        @SerializedName("id")
        private Integer id;
        @SerializedName("charity_item_id")
        private Integer charityItemId;
        @SerializedName("user_id")
        private Integer userId;
        @SerializedName("amount")
        private Integer amount;
        @SerializedName("is_anonymous")
        private String isAnonymous;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("transfer_id")
        private Integer transferId;
        @SerializedName("author_name")
        private String authorName;
        @SerializedName("pseudonym")
        private String pseudonym;
        @SerializedName("title")
        private String title;


        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCharityItemId() {
            return charityItemId;
        }

        public void setCharityItemId(Integer charityItemId) {
            this.charityItemId = charityItemId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getIsAnonymous() {
            return isAnonymous;
        }

        public void setIsAnonymous(String isAnonymous) {
            this.isAnonymous = isAnonymous;
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

        public Integer getTransferId() {
            return transferId;
        }

        public void setTransferId(Integer transferId) {
            this.transferId = transferId;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public String getPseudonym() {
            return pseudonym;
        }

        public void setPseudonym(String pseudonym) {
            this.pseudonym = pseudonym;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


    public class CharityProgram implements Serializable {

        @SerializedName("id")
        private Integer id;
        @SerializedName("title")
        private String title;
        @SerializedName("user_id")
        private Integer userId;
        @SerializedName("required_amount")
        private Integer requiredAmount;
        @SerializedName("execution")
        private Double execution;
        @SerializedName("total_amount")
        private Integer totalAmount;
        @SerializedName("init_collected_amount")
        private Integer initCollectedAmount;
        @SerializedName("main_photo")
        private String mainPhoto;
        @SerializedName("item_visibility")
        private String itemVisibility;
        @SerializedName("members_visibility")
        private String membersVisibility;
        @SerializedName("pseudonym")
        private String pseudonym;
        @SerializedName("is_contact")
        private Boolean isContact;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("author_name")
        private String authorName;
        @SerializedName("status")
        private String status;

        public String getStatus() {
            if (TextUtils.isEmpty(status)) return "";
            return status;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getRequiredAmount() {
            return requiredAmount;
        }

        public void setRequiredAmount(Integer requiredAmount) {
            this.requiredAmount = requiredAmount;
        }

        public Double getExecution() {
            return execution;
        }

        public void setExecution(Double execution) {
            this.execution = execution;
        }

        public Integer getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Integer totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Integer getInitCollectedAmount() {
            return initCollectedAmount;
        }

        public void setInitCollectedAmount(Integer initCollectedAmount) {
            this.initCollectedAmount = initCollectedAmount;
        }

        public String getMainPhoto() {
            return mainPhoto;
        }

        public void setMainPhoto(String mainPhoto) {
            this.mainPhoto = mainPhoto;
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


        public Boolean isContact() {
            return isContact;
        }

        public void setIsContact(Boolean isContact) {
            this.isContact = isContact;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }


    }
}