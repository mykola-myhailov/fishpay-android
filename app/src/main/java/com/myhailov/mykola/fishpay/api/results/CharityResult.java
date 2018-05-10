package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CharityResult {
        @SerializedName("charity_program")
        private List<CharityProgram> charityProgram = null;
        @SerializedName("donation")
        private List<Object> donation = null;
        @SerializedName("total_donation")
        private Double totalDonation;

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

        public List<Object> getDonation() {
            return donation;
        }

        public void setDonation(List<Object> donation) {
            this.donation = donation;
        }

        public CharityResult withDonation(List<Object> donation) {
            this.donation = donation;
            return this;
        }

        public Double getTotalDonation() {
            return totalDonation;
        }

        public void setTotalDonation(Double totalDonation) {
            this.totalDonation = totalDonation;
        }

        public CharityResult withTotalDonation(Double totalDonation) {
            this.totalDonation = totalDonation;
            return this;
        }


    public class CharityProgram {

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
        private Object pseudonym;
        @SerializedName("is_contact")
        private Boolean isContact;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("author_name")

        private String authorName;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public CharityProgram withId(Integer id) {
            this.id = id;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public CharityProgram withTitle(String title) {
            this.title = title;
            return this;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public CharityProgram withUserId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Integer getRequiredAmount() {
            return requiredAmount;
        }

        public void setRequiredAmount(Integer requiredAmount) {
            this.requiredAmount = requiredAmount;
        }

        public CharityProgram withRequiredAmount(Integer requiredAmount) {
            this.requiredAmount = requiredAmount;
            return this;
        }

        public Double getExecution() {
            return execution;
        }

        public void setExecution(Double execution) {
            this.execution = execution;
        }

        public CharityProgram withExecution(Double execution) {
            this.execution = execution;
            return this;
        }

        public Integer getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Integer totalAmount) {
            this.totalAmount = totalAmount;
        }

        public CharityProgram withTotalAmount(Integer totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Integer getInitCollectedAmount() {
            return initCollectedAmount;
        }

        public void setInitCollectedAmount(Integer initCollectedAmount) {
            this.initCollectedAmount = initCollectedAmount;
        }

        public CharityProgram withInitCollectedAmount(Integer initCollectedAmount) {
            this.initCollectedAmount = initCollectedAmount;
            return this;
        }

        public String getMainPhoto() {
            return mainPhoto;
        }

        public void setMainPhoto(String mainPhoto) {
            this.mainPhoto = mainPhoto;
        }

        public CharityProgram withMainPhoto(String mainPhoto) {
            this.mainPhoto = mainPhoto;
            return this;
        }

        public String getItemVisibility() {
            return itemVisibility;
        }

        public void setItemVisibility(String itemVisibility) {
            this.itemVisibility = itemVisibility;
        }

        public CharityProgram withItemVisibility(String itemVisibility) {
            this.itemVisibility = itemVisibility;
            return this;
        }

        public String getMembersVisibility() {
            return membersVisibility;
        }

        public void setMembersVisibility(String membersVisibility) {
            this.membersVisibility = membersVisibility;
        }

        public CharityProgram withMembersVisibility(String membersVisibility) {
            this.membersVisibility = membersVisibility;
            return this;
        }

        public Object getPseudonym() {
            return pseudonym;
        }

        public void setPseudonym(Object pseudonym) {
            this.pseudonym = pseudonym;
        }

        public CharityProgram withPseudonym(Object pseudonym) {
            this.pseudonym = pseudonym;
            return this;
        }

        public Boolean isContact() {
            return isContact;
        }

        public void setIsContact(Boolean isContact) {
            this.isContact = isContact;
        }

        public CharityProgram withIsContact(Boolean isContact) {
            this.isContact = isContact;
            return this;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public CharityProgram withCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public CharityProgram withAuthorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

    }
}