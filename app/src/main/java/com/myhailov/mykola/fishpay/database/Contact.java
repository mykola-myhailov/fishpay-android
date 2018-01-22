package com.myhailov.mykola.fishpay.database;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Mykola Myhailov  on 15.01.18.
 */
@Entity
public class Contact {


    @Id(autoincrement = true)
    private Long id;
    
    @SerializedName("contact_id")
    private long userId;

    @SerializedName("phone_number")
    private String phone;

    @SerializedName("first_name")
    private String name;

    @SerializedName("last_name")
    private String surname;

    @SerializedName("photo_link")
    private String photo;

    @Generated(hash = 1956934569)
    public Contact(Long id, long userId, String phone, String name, String surname,
            String photo) {
        this.id = id;
        this.userId = userId;
        this.phone = phone;
        this.name = name;
        this.surname = surname;
        this.photo = photo;
    }
    @Generated(hash = 672515148)
    public Contact() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhoto() {
        return this.photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getSurname() {
        return this.surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
}
