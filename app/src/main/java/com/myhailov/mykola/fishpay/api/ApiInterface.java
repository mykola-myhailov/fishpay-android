package com.myhailov.mykola.fishpay.api;

import android.provider.CalendarContract;

import com.myhailov.mykola.fishpay.api.models.ChangePassVerifyResult;
import com.myhailov.mykola.fishpay.api.models.CheckMobileResult;
import com.myhailov.mykola.fishpay.api.models.CheckRecoveryResult;
import com.myhailov.mykola.fishpay.api.models.JointPurchasesResult;
import com.myhailov.mykola.fishpay.api.models.LoginResult;
import com.myhailov.mykola.fishpay.api.models.ProfileResult;
import com.myhailov.mykola.fishpay.api.models.RegistrationResult;
import com.myhailov.mykola.fishpay.api.models.RemoveReason;

import java.util.ArrayList;

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
    Call<BaseResponse<ArrayList<RemoveReason>>> removeAccReasons(@Header("Authorization") String token);

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

    @Multipart @POST("api/user/profile")   // upload updated profile info
    Call<BaseResponse<Object>> editProfile(@Header("Authorization") String token,
                                           @Part("firstName") RequestBody firstName,
                                           @Part("secondName") RequestBody secondName,
                                           @Part("birthday") RequestBody birthday,
                                           @Part("email") RequestBody email,
                                           @Part   MultipartBody.Part img);

    @PUT("rest/api/user/preferences")
    Call<BaseResponse<Object>> setPreferences(@Header("Authorization") String token,
                                              @Query("ALLOW_MONEY_REQUESTS") String allowMoneyRequests,
                                              @Query("TOUCH_ID_LOGIN") String touchIdLogin,
                                              @Query("UI_LANG")String lang);


    @GET("api/user/preferences/{preferenceKey}")
    Call<BaseResponse<Object>> getPreferences(@Header("Authorization") String token,
                                              @Path(("preferenceKey")) String key);

    @GET("api/user/devices")
    Call<BaseResponse<Object>> getDevices(@Header("Authorization") String token,
                                          @Query("pass") boolean isNeededAllDevices);

    @PUT("api/user/changePass/verify")
    Call<BaseResponse<ChangePassVerifyResult>> changePassVerify(@Header("Authorization") String token,
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
    Call<BaseResponse<JointPurchasesResult>> getJointPurchases (@Header("Authorization") String token);

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

    @PUT("api/commonPurchases/{id}/delete")
    Call<Void>  deleteJointPurchase (@Header("Authorization") String token,
                                                     @Path("id") String id);


    @PUT("api/commonPurchases/{id}/close")
    Call<BaseCallback<Object>>  closeJointPurchase (@Header("Authorization") String token,
                                                    @Path("id") String id);

    @PUT("api/commonPurchases/{id}/accept")
    Call<BaseCallback<Object>>  acceptJointPurchase (@Header("Authorization") String token,
                                                     @Path("id") String id);

    @PUT("api/commonPurchases/{id}/reject")
    Call<BaseCallback<Object>>  rejectJointPurchase (@Header("Authorization") String token,
                                                     @Path("id") String id);



    // 9) spending
    @GET("api/commonSpendings")
    Call<BaseCallback<Object>> getSpending(@Header("Authorization") String token);

    @POST("api/commonSpendings")
    Call<BaseResponse<Object>> createSpending(@Header("Authorization") String token,
                                              @Body RequestBody body);


    @DELETE("api/commonSpendings/{id}")
    Call<BaseResponse<Object>> deleteSpending(@Header("Authorization") String token,
                                              @Path("id") String id);

    @GET("api/commonSpendings/{id}")
    Call<BaseResponse<Object>> getSpendingDetails (@Header("Authorization") String token,
                                                   @Path("id") String id);

    @FormUrlEncoded @POST("api/commonSpendings/{id}/transactions")
    Call<BaseResponse<Object>>  transaction   (@Header("Authorization") String token,
                                               @Path("id") String id,
                                               @Field("returnUpdated") boolean returnUpdated,
                                               @Field("member_from") String memberFrom,
                                               @Field("member_to") String memberTo,
                                               @Field("amount") String amount,
                                               @Field("comment") String comment);


    @DELETE("api/commonSpendings/transactions/{trans_id}")
    Call<BaseResponse<Object>> deleteTransaction (@Header("Authorization") String token,
                                                 @Path("trans_id") String transId,
                                                 @Path("returnUpdated") boolean returnUpdated);


    @FormUrlEncoded @POST("api/moneyRequest/initFromCommonSpending")
    Call<BaseResponse<Object>> initInvoice(@Header("Authorization") String token,
                                           @Field("phone") String phone,
                                           @Field("card") String card,
                                           @Field("amount") String amount,
                                           @Field("comment") String comment,
                                           @Field("member_from") String memberFrom,
                                           @Field("member_to") String memberTo);



    // Charity

    @GET("api/charity")
    Call<BaseResponse<Object>> getChatiry (@Header("Authorization") String token);

    @Multipart @POST("api/charity")
    Call<BaseResponse<Object>> createChatiry (@Header("Authorization") String token,
                                              @Part("title") RequestBody title,
                                              @Part("required_amount") RequestBody requiredAmount,
                                              @Field("init_collected_amount") RequestBody initCollAmount,
                                              @Field("description") RequestBody description,
                                              @Field("user_card_id") RequestBody userCardId,
                                              @Field("item_visibility") RequestBody itemVisibility,
                                              @Field("members_visibility") RequestBody membersVisibility,
                                              @Field("pseudonym") RequestBody pseudonym,
                                              @Part  MultipartBody.Part img);   //main_photo


    @Multipart @POST("api/charity/{id}/uploadPhoto")
    Call<BaseResponse<Object>> uploadCharityPhoto  (@Header("Authorization") String token,
                                                    @Path("id") String id,
                                                    @Part  MultipartBody.Part img);  //main_photo


    @GET("api/charity/{id}")
    Call<BaseResponse<Object>> getCharityDetails   (@Header("Authorization") String token,
                                                    @Path("id") String id);


    @FormUrlEncoded @POST("api/charity/{id}/donation")
    Call<BaseResponse<Object>> donation             (@Header("Authorization") String token,
                                                     @Field("amount") String amount,
                                                     @Field("returnUpdated") boolean returnUpdated,
                                                     @Field("is_anonymous") boolean isAnonymos,
                                                     @Field("id") String id);

    @PUT("api/charity/{id}/close")
    Call<BaseResponse<Object>>  closeChatiry (@Header("Authorization") String token,
                                              @Path("id") String id);



    // Goods
    @GET("api/goods")
    Call<BaseResponse<Object>>  getGoods (@Header("Authorization") String token);


    @Multipart @POST("api/goods")
    Call<BaseResponse<Object>>  createGoods (@Header("Authorization") String token,
                                          @Part("title") RequestBody title,
                                          @Part("description") RequestBody description,
                                          @Part("price") RequestBody price,
                                          @Part MultipartBody.Part img);      //main_photo


    @Multipart @POST("api/goods/{id}/uploadPhoto")
    Call<BaseResponse<Object>>  uploadGoodsPhoto (@Header("Authorization") String token,
                                                  @Path("id") String id,
                                                  @Part MultipartBody.Part img);      //img            ?

    @GET("api/goods/{id}")
    Call<BaseResponse<Object>> getGoodsDetails(@Header("Authorization") String token,
                                               @Path("id") String id);



}
