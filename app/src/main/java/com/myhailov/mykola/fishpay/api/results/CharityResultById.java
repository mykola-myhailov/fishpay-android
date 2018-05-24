package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CharityResultById implements Serializable{
        @SerializedName("id")
        private Integer id;
        @SerializedName("title")
        private String title;
        @SerializedName("description")
        private String description;
        @SerializedName("required_amount")
        private Integer requiredAmount;
        @SerializedName("init_collected_amount")
        private Integer initCollectedAmount;
        @SerializedName("main_photo")
        private String mainPhoto;
        @SerializedName("author_id")
        private Integer authorId;
        @SerializedName("name")
        private Object name;
        @SerializedName("status")
        private String status;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("author_name")
        private String authorName;
        @SerializedName("total_amount")
        private Integer totalAmount;
        @SerializedName("execution")
        private Double execution;
        @SerializedName("photos")
        private List<Photo> photos = null;
        @SerializedName("donation")
        private List<Donation> donation = null;

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public String getMainPhoto() {
            return mainPhoto;
        }

        public void setMainPhoto(String mainPhoto) {
            this.mainPhoto = mainPhoto;
        }

        public Integer getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Integer authorId) {
            this.authorId = authorId;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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
        public Integer getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Integer totalAmount) {
            this.totalAmount = totalAmount;
        }


        public Double getExecution() {
            return execution;
        }

        public void setExecution(Double execution) {
            this.execution = execution;
        }

        public List<Photo> getPhotos() {
            return photos;
        }

        public void setPhotos(List<Photo> photos) {
            this.photos = photos;
        }

        public List<Donation> getDonation() {
            return donation;
        }

        public void setDonation(List<Donation> donation) {
            this.donation = donation;
        }

        public class Photo implements Serializable{
            @SerializedName("charity_item_id")
            private Integer id;
            @SerializedName("photo_url")
            private String photoUrl;
            @SerializedName("order")
            private Integer order;
            @SerializedName("created_at")
            private String createdAt;
            @SerializedName("updated_at")
            private String updatedAt;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getPhotoUrl() {
                return photoUrl;
            }

            public void setPhotoUrl(String photoUrl) {
                this.photoUrl = photoUrl;
            }

            public Integer getOrder() {
                return order;
            }

            public void setOrder(Integer order) {
                this.order = order;
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
        }

    public class Donation implements Serializable{

        @SerializedName("id")
        private Integer id;
        @SerializedName("user_id")
        private Integer userId;
        @SerializedName("is_anonymous")
        private String isAnonymous;
        @SerializedName("amount")
        private Integer amount;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("second_name")
        private String secondName;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        public String getIsAnonymous() {
            return isAnonymous;
        }

        public void setIsAnonymous(String isAnonymous) {
            this.isAnonymous = isAnonymous;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

    }




}
