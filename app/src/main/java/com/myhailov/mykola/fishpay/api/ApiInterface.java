package com.myhailov.mykola.fishpay.api;

import android.provider.CalendarContract;

import com.myhailov.mykola.fishpay.api.models.CheckMobileResult;
import com.myhailov.mykola.fishpay.api.models.CheckRecoveryResult;
import com.myhailov.mykola.fishpay.api.models.LoginResult;
import com.myhailov.mykola.fishpay.api.models.ProfileResult;
import com.myhailov.mykola.fishpay.api.models.RegistrationResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Created by Mykola Myhailov  on 14.11.17. */

public interface ApiInterface {

    // 1) registration
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




    // 2) Authentication
    @FormUrlEncoded @POST("api/user/login")      // login
    Call<BaseResponse<LoginResult>> login (@Field("phoneNumber") String phoneNumber,
                                           @Field("pass") String password,
                                           @Field("deviceId") String deviceId,
                                           @Field("deviceInfo") String deviceInfo);

    // TODO: put /rest/api/sessions/invalidate

    @POST ("api/user/logout")   // logout
    Call<Void> logout (@Header("Authorization") String token);




    /// ?) password recovering
    @PUT("admin/passwordRecovery/init/{phone}")   // send sms on number
    Call<BaseResponse<CheckMobileResult>> initPassRecovery(@Path("phone") String phone);

    @FormUrlEncoded @POST("admin/passwordRecovery/checkOtp") // check code from sms
    Call<BaseResponse<CheckRecoveryResult>> checkRecoveryOTP(@Field("phoneNumber") String phone,
                                                             @Field("codeOTP") String code,
                                                             @Field("recoveryId") String recoveryId);

    @FormUrlEncoded @POST("admin/passwordRecovery/{userId}")  // set new password
    Call<BaseResponse<String>> passRecovery(@Field("recoveryId") String recoveryId,
                                          @Field("pass") String password,
                                          @Path("userId")String userId);




    // 3)delete account
    @GET("api/removeAcc/reasons")   // list of account reasons
    Call<BaseResponse<Object>> removeAccReasons(@Header("Authorization") String token);

    @POST("rest/api/removeAcc")
    Call<BaseResponse<Object>> removeAccount(@Header("Authorization") String token,
                                             @Field("Request") RequestBody body);

    @FormUrlEncoded @POST("api/removeAcc/confirm")
    Call<BaseResponse<Object>>  removeAccConfirm(@Header("Authorization") String token,
                                                 @Field("password") String password,
                                                 @Field("request_id") String requestId);




    // 4) profile
    @GET("api/user/profile")                     // get profile info
    Call<BaseResponse<ProfileResult>> getProfile(@Header("Authorization") String token);

    @FormUrlEncoded @POST("api/user/profile")   // upload updated profile info
    Call<BaseResponse<Object>> editProfile(@Header("Authorization") String token,
                                           @Field("firstName") String firstName,
                                           @Field("secondName") String secondName,
                                           @Field("birthday") String birthday,
                                           @Field("email") String email,
                                           @Part ("img")  MultipartBody.Part img);

    @PUT("rest/api/user/preferences")
    Call<BaseResponse<Object>> setPreferences(@Header("Authorization") String token,
                                              @Query("ALLOW_MONEY_REQUESTS") String allowMoneyRequests,
                                              @Query("TOUCH_ID_LOGIN") String touchIdLogin,
                                              @Query("UI_LANG")String lang);


    @GET("api/user/preferences/{preferenceKey}")
    Call<BaseResponse<Object>> getPreference(@Header("Authorization") String token,
                                             @Path(("preferenceKey")) String key);

    @GET("api/user/devices")
    Call<BaseResponse<Object>> getDevices(@Header("Authorization") String token,
                                          @Query("pass") boolean isNeededAllDevices);

    @PUT("api/user/changePass/verify")
    Call<BaseResponse<Object>> changePassVerify(@Header("Authorization") String token,
                                                @Query("pass") String oldPassword);

    @PUT("api/user/changePass")
    Call<BaseResponse<Object>> changePass (@Header("Authorization") String token,
                                           @Query("pass") String newPassword,
                                           @Query("passChangeId") String passChangeId);



    // 5) cards
    @GET("api/user/cards")
    Call<BaseResponse<Object>> getCards (@Header("Authorization") String token);


    @FormUrlEncoded @POST("api/user/cards")
    Call<BaseResponse<Object>> createCard (@Header("Authorization") String token,
                                           @Field("name") String cardName,
                                           @Field("card_number") String cardNumber);

    @GET ("api/user/withoutCard")
    Call<BaseResponse<Object>> getWithoutCard (@Header("Authorization") String token);

    @DELETE("api/user/cards/{cardNumber}")
    Call<BaseResponse<Object>> deleteCard (@Header("Authorization") String token,
                                           @Path("cardNumber") String cardNumber);

    @FormUrlEncoded @POST("api/user/cards/setPublic")
    Call<BaseResponse<Object>> setPublicCard (@Header("Authorization") String token,
                                              @Field("card_number") String cardNumber);


    //6) contacts


    //7) invoices
    @FormUrlEncoded @POST("api/moneyRequest/init")
    Call<BaseResponse<Object>> createInvoice (@Header("Authorization") String token,
                                      @Field("phone") String phone,
                                      @Field("card") String card,
                                      @Field("amount") String amount,
                                      @Field("comment") String comment,
                                      @Field("commonPurchaseMember") String memberId,
                                      @Field("goods") String goods);

    @PUT("api/moneyRequest/{requestId}/confirm")
    Call<BaseResponse<Object>> confirmInvoice (@Header("Authorization") String token,
                                             @Path("requestId") String requestId,
                                             @Query("password") String password);
    @GET("api/moneyRequest/incoming")
    Call<BaseResponse<Object>> getIncomingInvoices(@Header("Authorization") String token);

    @GET("api/moneyRequest/outComing")
    Call<BaseResponse<Object>> getOuncomingInvoices (@Header("Authorization") String token);

    @PUT("api/moneyRequest/{requestId}/delete")
    Call<BaseResponse<Object>>  deleteInvoice(@Header("Authorization") String token,
                                              @Path("requestId") String invoiceId);

    @PUT("rest/api/moneyRequest/{requestId}/removeFromList")
    Call<BaseResponse<Object>> removeInvoice(@Header("Authorization") String token,
                                             @Path("requestId") String invoiceId);

    @GET("api/moneyRequest/{requestId}")
    Call<BaseResponse<Object>> getInvoiceDetails(@Header("Authorization") String token,
                                                @Path("requestId") String invoiceId);

    @PUT("api/moneyRequest/{requestId}/reject")
    Call<BaseResponse<Object>> rejectInvoice (@Header("Authorization") String token,
                                              @Path("requestId") String invoiceId);

    @PUT("api/moneyRequest/{requestId}/accept")
    Call<BaseResponse<Object>> acceptInvoice (@Header("Authorization") String token,
                                              @Path("requestId") String invoiceId);

    @PUT("api/moneyRequest/{requestId}/approve")
    Call<BaseResponse<Object>> approveInvoice (@Header("Authorization") String token,
                                               @Path("requestId") String invoiceId);




    // 8) joint purchases
    @GET("api/commonPurchases")
    Call<BaseResponse<Object>> getJointPurchases (@Header("Authorization") String token);

    @FormUrlEncoded @POST("api/commonPurchases")
    Call<BaseResponse<Object>> createJointPurchase (@Header("Authorization") String token,
                                                    @Field("Data") RequestBody data);

    @GET("api/commonPurchases/{id}")
    Call<BaseResponse<Object>> getJointPurchaseDetails (@Header("Authorization") String token,
                                                        @Path("id") String id);


    @PUT("/rest/api/commonPurchases/member/{id}")
    Call<BaseResponse<Object>> changeJointPurchase (@Header("Authorization") String token,
                                                    @Path("id") String id,
                                                    @Query("amount") String amount,
                                                    @Query("pay_method") String method);

}
