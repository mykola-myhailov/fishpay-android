package com.myhailov.mykola.fishpay.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Mykola Myhailov  on 15.01.18.
 */
@Entity
public class Contact {


    @Id(autoincrement = true)
    private long id;
    private long userId;
    private String name;
    private String phone;
    private String photo;
    @Generated(hash = 1777648207)
    public Contact(long id, long userId, String name, String phone, String photo) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
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
}
