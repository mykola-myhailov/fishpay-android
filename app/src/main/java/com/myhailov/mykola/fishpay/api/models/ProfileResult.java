package com.myhailov.mykola.fishpay.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mykola Myhailov  on 06.12.17.
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



        public String getPhone() {
            return phone;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public String getBirthday() {
            return birthday;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoto() {
            return photo;
        }

     /*   @SerializedName("properties")
        private Properties properties;

        public Properties getProperties() {
            return properties;
        }*/

       /* public class Properties{
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
        }*/



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
        "properties": {
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
