package com.myhailov.mykola.fishpay.api;

import com.myhailov.mykola.fishpay.api.models.CheckMobileResult;
import com.myhailov.mykola.fishpay.api.models.CheckRecoveryResult;
import com.myhailov.mykola.fishpay.api.models.LoginResult;
import com.myhailov.mykola.fishpay.api.models.ProfileResult;
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
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/** Created by Mykola Myhailov  on 14.11.17. */

public interface ApiInterface {

    @GET ("api/user/checkMobile/{phoneNumber}") // check is there users with this phone number exist
    Call<BaseResponse<Object>> checkMobile(@Path("phoneNumber")String phone);


    @FormUrlEncoded @POST("api/user/registration/checkOtp") // check code from sms
    Call<BaseResponse<String>> checkOTP (@Field("phoneNumber") String phone, @Field("codeOTP") String codeOTP);


    @Multipart @POST("api/user/create")   // registration
    Call<BaseResponse<RegistrationResult>> registration(@Part("phoneNumber") RequestBody phoneNumber,
                                                        @Part("firstName") RequestBody firstName,
                                                        @Part("secondName") RequestBody secondName,
                                                        @Part("birthday") RequestBody birthday,
                                                        @Part("email") RequestBody email,
                                                        @Part("pass") RequestBody password,
                                                        @Part("deviceId") RequestBody deviceId,
                                                        @Part("deviceInfo") RequestBody deviceInfo,
                                                         @Part  MultipartBody.Part img);


    @FormUrlEncoded @POST("api/user/login")      // login
    Call<BaseResponse<LoginResult>> login (@Field("phoneNumber") String phoneNumber,
                                           @Field("pass") String password,
                                           @Field("deviceId") String deviceId,
                                           @Field("deviceInfo") String deviceInfo);


    @GET("api/user/profile")                     // get profile info
    Call<BaseResponse<ProfileResult>> getProfile(@Header("Authorization") String token);

    @FormUrlEncoded @POST("api/user/profile")   // upload updated profile info
    Call<BaseResponse<Object>> editProfile(@Header("Authorization") String token,
                                           @Field("firstName") String firstName,
                                           @Field("secondName") String secondName,
                                           @Field("birthday") String birthday,
                                           @Field("email") String email,
                                           @Part ("img")  MultipartBody.Part img);


    @PUT("admin/passwordRecovery/init/{phone}")
    Call<BaseResponse<CheckMobileResult>> initPassRecovery(@Path("phone") String phone);

    @FormUrlEncoded @POST("admin/passwordRecovery/checkOtp")
    Call<BaseResponse<CheckRecoveryResult>> checkRecoveryOTP(@Field("phoneNumber") String phone,
                                                             @Field("codeOTP") String code,
                                                             @Field("recoveryId") String recoveryId);

    @FormUrlEncoded @POST("admin/passwordRecovery/{userId}")
    Call<BaseResponse<String>> passRecovery(@Field("recoveryId") String recoveryId,
                                          @Field("pass") String password,
                                          @Path("userId")String userId);


}
