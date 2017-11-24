package com.myhailov.mykola.fishpay.api;

import com.myhailov.mykola.fishpay.api.models.CheckMobileResult;
import com.myhailov.mykola.fishpay.api.models.RegistrationResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/** Created by Mykola Myhailov  on 14.11.17. */

public interface ApiInterface {

    @GET ("user/checkMobile/{phoneNumber}") // check is there users with this phone number exist
    Call<BaseResponse<CheckMobileResult>> checkMobile(@Path("phoneNumber")String phone);


    @FormUrlEncoded @POST("user/registration/checkOtp") // check code from sms
    Call<BaseResponse<String>> checkOTP (@Field("phoneNumber") String phone, @Field("codeOTP") String codeOTP);


    @Multipart @POST("user/create")   // registration
    Call<BaseResponse<RegistrationResult>> registration(@Part("phoneNumber") RequestBody phoneNumber,
                                                        @Part("firstName") RequestBody firstName,
                                                        @Part("secondName") RequestBody secondName,
                                                        @Part("birthday") RequestBody birthday,
                                                        @Part("email") RequestBody email,
                                                        @Part("pass") RequestBody password,
                                                        @Part("deviceId") RequestBody deviceId,
                                                        @Part("deviceInfo") RequestBody deviceInfo,
                                                         @Part  MultipartBody.Part img);


    @FormUrlEncoded @POST("user/login")      // login
    Call<BaseResponse<Void>> login (@Field("phoneNumber") String phoneNumber,
                                      @Field("pass") String password,
                                      @Field("deviceId") String deviceId,
                                      @Field("deviceInfo") String deviceInfo);


    @GET("user/profile")                     // get profile info
    Call<BaseResponse<Object>> getProfile(@Header("Authorization") String token);

    @FormUrlEncoded @POST("user/profile")   // upload updated profile info
    Call<BaseResponse<Object>> editProfile(@Header("Authorization") String token,
                                           @Field("firstName") String firstName,
                                           @Field("secondName") String secondName,
                                           @Field("birthday") String birthday,
                                           @Field("email") String email,
                                           @Part ("img")  MultipartBody.Part img);


}
