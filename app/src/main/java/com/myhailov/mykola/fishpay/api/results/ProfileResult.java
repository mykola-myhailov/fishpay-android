package com.myhailov.mykola.fishpay.api.results;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.ApiInterface;

/**
 * Created by Mykola Myhailov  on 06.12.17.
 *
 * *  Value TYPE of field "result" in response of {@link ApiInterface#getProfile}
 */

public class ProfileResult {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("profile")
    private Profile profile;

    public class Profile {

        @SerializedName("phoneNumber")
        private String phone;

        @SerializedName("firstName")
        private String name;

        @SerializedName("secondName")
        private String surname;

        @SerializedName("birthday")
        private String birthday;

        @SerializedName("email")
        private String email;

        @SerializedName("photo")
        private String photo;

        @SerializedName("publicCard") private Card card;

        public Card getCard() {
            return card;
        }

        public String getPhone() {
            return phone;
        }

        @NonNull
        public String getName() {
            if (name == null) return "";
            return name;
        }

        @NonNull
        public String getSurname() {
            if (surname == null) return "";
            return surname;
        }

        @NonNull
        public String getBirthday() {
            if (birthday == null) return "";
            return birthday;
        }

        @NonNull
        public String getEmail() {
            if (email == null) return  "";
            return email;
        }

        @NonNull
        public String getPhoto() {
            if (phone == null) return "";
            return photo;
        }

        @SerializedName("profileProperties")
        private ProfileProperties profileProperties;

        public ProfileProperties getProfileProperties() {
            return profileProperties;
        }

       public class ProfileProperties {
            @SerializedName("ALLOW_MONEY_REQUESTS")
            private int allow;

            @SerializedName("TOUCH_ID_LOGIN")
            private int touch;

            @SerializedName("UI_LANG")
            private int lang;

            public int getAllow() {
                return allow;
            }

            public int getTouch() {
                return touch;
            }

            public int getLang() {
                return lang;
            }
        }
           /*      @SerializedName("publicCard")
                private */
    }

    public String getUserId() {
        return userId;
    }

    public Profile getProfile() {
        return profile;
    }
}

/*
{
        "user_id": 73,
        "profile": {
        "phoneNumber": "380123456789",
        "firstName": "ккее",
        "secondName": "ееп",
        "birthday": "1990-03-05",
        "email": "test@gmail.com",
        "photo": null,
        "profileProperties": {
        "lacalization": {
        "ALLOW_MONEY_REQUESTS": "Разрешить запросы",
        "TOUCH_ID_LOGIN": "TouchID для входа",
        "UI_LANG": "Язык интерфейса"
        },
        "ALLOW_MONEY_REQUESTS": "1",
        "TOUCH_ID_LOGIN": "1",
        "UI_LANG": "ru"
        },
        "publicCard": null
        }
        }*/
