package com.myhailov.mykola.fishpay.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.database.Database;

/**
 * Created by Mykola Myhailov  on 15.01.18.
 */
@Entity
public class Contact implements Parcelable {


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

    @SerializedName("isActiveUser ")
    private boolean isActiveUser;

    @Generated(hash = 1934826131)
    public Contact(Long id, long userId, String phone, String name, String surname,
            String photo, boolean isActiveUser) {
        this.id = id;
        this.userId = userId;
        this.phone = phone;
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.isActiveUser = isActiveUser;
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

    public boolean isActiveUser() {
        return isActiveUser;
    }

    public void setActiveUser(boolean activeUser) {
        isActiveUser = activeUser;
    }
    // Parcelable implementation;


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeLong(userId);
        parcel.writeString(phone);
        parcel.writeString(name);
        parcel.writeString(surname);
        parcel.writeString(photo);
        parcel.writeByte((byte) (isActiveUser ? 1 : 0));
    }

    protected Contact(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        userId = in.readLong();
        phone = in.readString();
        name = in.readString();
        surname = in.readString();
        photo = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }
    public boolean getIsActiveUser() {
        return this.isActiveUser;
    }
    public void setIsActiveUser(boolean isActiveUser) {
        this.isActiveUser = isActiveUser;
    }




    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
