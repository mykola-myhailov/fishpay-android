package com.myhailov.mykola.fishpay.api;


import com.google.gson.JsonElement;
import com.myhailov.mykola.fishpay.activities.profile.DeleteAccountActivity;
import com.myhailov.mykola.fishpay.api.requestBodies.CommonPurchaseBody;
import com.myhailov.mykola.fishpay.api.requestBodies.GroupSpendBody;
import com.myhailov.mykola.fishpay.api.results.AuditPayResult;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.api.results.CategoryResult;
import com.myhailov.mykola.fishpay.api.results.ChangePassVerifyResult;
import com.myhailov.mykola.fishpay.api.results.CharityResult;
import com.myhailov.mykola.fishpay.api.results.CharityResultById;
import com.myhailov.mykola.fishpay.api.results.CheckMobileResult;
import com.myhailov.mykola.fishpay.api.results.CheckRecoveryResult;
import com.myhailov.mykola.fishpay.api.results.ContactDetailResult;
import com.myhailov.mykola.fishpay.api.results.ContactsResult;
import com.myhailov.mykola.fishpay.api.results.CreateInvoiceResult;
import com.myhailov.mykola.fishpay.api.results.GoodsByIdResult;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;
import com.myhailov.mykola.fishpay.api.results.InvoiceDetailsResult;
import com.myhailov.mykola.fishpay.api.results.JointPurchase;
import com.myhailov.mykola.fishpay.api.results.JointPurchaseDetailsResult;
import com.myhailov.mykola.fishpay.api.results.LoginResult;
import com.myhailov.mykola.fishpay.api.results.PayRequest;
import com.myhailov.mykola.fishpay.api.results.ProfileResult;
import com.myhailov.mykola.fishpay.api.results.RegistrationResult;
import com.myhailov.mykola.fishpay.api.results.RemoveAccResult;
import com.myhailov.mykola.fishpay.api.results.RemoveReason;
import com.myhailov.mykola.fishpay.api.results.SearchedContactsResult;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/** Created by Mykola Myhailov  on 14.11.17. */

public interface ApiInterface {

    // 1) registration
    @GET ("api/user/checkMobile/{phoneNumber}") // check is there users with this phone number exist
    Call<BaseResponse<Object>> checkMobile(@Header("deviceType") String deviceType ,
            @Header("version") int version,
            @Header("language") String language,
            @Path("phoneNumber")String phone);


    @FormUrlEncoded @POST("api/user/registration/checkOtp") // check code from sms
    Call<BaseResponse<String>> checkOTP (@Field("phoneNumber") String phone, @Field("codeOTP") String codeOTP);

    @Multipart @POST("api/user/create")   // registration
    Call<BaseResponse<RegistrationResult>>
    registration(@Header("deviceType") String device ,
                 @Header("version") int version,
                 @Header("language") String language,
                 @Part("phoneNumber") RequestBody phoneNumber,
                 @Part("firstName") RequestBody firstName,
                 @Part("secondName") RequestBody secondName,
                 @Part("birthday") RequestBody birthday,
                 @Part("email") RequestBody email,
                 @Part("pass") RequestBody password,
                 @Part("deviceId") RequestBody deviceId,
                 @Part("deviceInfo") RequestBody deviceInfo,
                 @Part("device_type") RequestBody deviceType,
                 @Part("user_token") RequestBody firebaseToken,
                 @Part  MultipartBody.Part img);

    // 2) Authentication
    @FormUrlEncoded @POST("api/user/login")      // login
    Call<BaseResponse<JsonElement>> login (@Header("deviceType") String device ,
                                           @Header("version") int version,
                                           @Header("language") String language,
                                           @Field("phoneNumber") String phoneNumber,
                                           @Field("pass") String password,
                                           @Field("deviceId") String deviceId,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("device_type") String deviceType,
                                           @Field("user_token") String firebaseToken);

    @PUT ("api/sessions/invalidate")    //invalidation
    Call<BaseResponse<Object>> invalidion (@Query("phoneNumber") String phoneNumber,
                                           @Query("jti") String jti);

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

    @POST("api/removeAcc")
    Call<BaseResponse<RemoveAccResult>> removeAccount(@Header("Authorization") String token,
                                                      @Body() DeleteAccountActivity.RemoveBody body);

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

    @PUT("api/user/preferences")
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
    Call<BaseResponse<ArrayList<Card>>> getCards (@Header("Authorization") String token);


    @FormUrlEncoded @POST("api/user/cards")
    Call<BaseResponse<Object>> createCard (@Header("Authorization") String token,
                                           @Field("name") String cardName,
                                           @Field("pan") String cardNumber,
                                           @Field("expiresAt") String expiresAt);

    @GET ("api/user/withoutCard")
    Call<BaseResponse<Object>> setWithoutCard (@Header("Authorization") String token);

    @DELETE("api/user/cards/{cardId}")
    Call<BaseResponse<Object>> deleteCard (@Header("Authorization") String token,
                                           @Path("cardId") String cardId);

    @FormUrlEncoded @POST("api/user/cards/setPublic")
    Call<BaseResponse<Object>> setPublicCard (@Header("Authorization") String token,
                                              @Field("id") String id);

    //6) contacts
    @FormUrlEncoded
    @PUT ("api/user/contacts/syncData")
    Call<BaseResponse<Object>> exportContacts(@Header("Authorization") String token,
                                              @Field("contacts") String contacts);
    @FormUrlEncoded
    @PUT("api/user/contacts/{userId}/block")
    Call<BaseResponse<Object>> blockUserById(@Header("Authorization") String token,
                                             @Field("status") String status,
                                             @Path("userId") String id);

    @GET("api/user/contacts")
    Call<BaseResponse<ContactsResult>> getContacts(@Header("Authorization") String token,
                                                   @Query("all") boolean all,
                                                   @Query("excludeBlockedByContact") boolean blocked);

    @GET("api/user/contacts/{contactId}")
    Call<BaseResponse<ContactDetailResult>> getContactDetails(@Header("Authorization") String token,
                                                              @Path("contactId") long contactId);

    @GET("api/user/searchByPhone/{phoneNumber}")
    Call<BaseResponse<SearchedContactsResult>> searchContact(@Header("Authorization") String token,
                                                             @Path("phoneNumber") String phoneNumber);

    @POST("api/user/contacts/add/{userId}")
    Call<BaseResponse<String>> addContact(@Header("Authorization") String toke, @Path("userId") long id);


    @FormUrlEncoded
    @PUT ("api/user/contacts/{contactId}")
    Call<BaseResponse<Object>> changeContactStatus (@Header("Authorization") String token,
                                                    @Path("contactId") String id,
                                                    @Field("status") String status);

    @FormUrlEncoded
    @PUT ("api/user/contacts/{userId}/block")
    Call<BaseResponse<Object>> deleteContact (@Header("Authorization") String token,
                                              @Field("status") String status,
                                              @Path("userId") String id);

    //7) invoices
    @FormUrlEncoded @POST("api/moneyRequest/init")
    Call<BaseResponse<CreateInvoiceResult>> createInvoice
                                                           (@Header("Authorization") String token,
                                                            @Field("phone") String phone,
                                                            @Field("card_id")   String card,
                                                            @Field("amount") int amount,
                                                            @Field("comment")   String comment,
                                                            @Field("memberId") String memberId,
                                                            @Field("goods")  String goods);


    @PUT("api/moneyRequest/{requestId}/confirm")
    Call<BaseResponse<Object>> confirmInvoice (@Header("Authorization") String token,
                                              @Path("requestId") String requestId,
                                              @Query("password") String password);
    @GET("api/moneyRequest/incoming")
    Call<BaseResponse<ArrayList<PayRequest>>> getIncomingPayRequests(@Header("Authorization") String token);

    @GET("api/moneyRequest/outComing")
    Call<BaseResponse<ArrayList<PayRequest>>> getOutcomingPayRequests(@Header("Authorization") String token);

    @PUT("api/moneyRequest/{requestId}/delete")
    Call<BaseResponse<Object>>  deleteInvoice(@Header("Authorization") String token,
                                              @Path("requestId") long invoiceId);

    @PUT("api/moneyRequest/{requestId}/removeFromList")
    Call<BaseResponse<Object>> removeInvoice(@Header("Authorization") String token,
                                             @Path("requestId") long invoiceId);

    @GET("api/moneyRequest/{requestId}")
    Call<BaseResponse<InvoiceDetailsResult>> getInvoiceDetails(@Header("Authorization") String token,
                                                               @Path("requestId") long invoiceId);

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
    Call<BaseResponse<ArrayList<JointPurchase>>> getJointPurchases (@Header("Authorization") String token);

    @POST("api/commonPurchases")
    Call<BaseResponse<Object>> createJointPurchase (@Header("Authorization") String token,
                                                    @Body CommonPurchaseBody data);

    @GET("api/commonPurchases/{id}")
    Call<BaseResponse<JointPurchaseDetailsResult>> getJointPurchaseDetails (@Header("Authorization") String token,
                                                                            @Path("id") String id);


    @PUT("/rest/api/commonPurchases/member/{id}")
    Call<BaseResponse<Object>> changeJointPurchase (@Header("Authorization") String token,
                                                    @Path("id") String id,
                                                    @Query("amount") int amount,
                                                    @Query("pay_method") int method);

    @PUT("api/commonPurchases/{id}/delete")
    Call<BaseResponse<Object>>  deleteJointPurchase (@Header("Authorization") String token,
                                                     @Path("id") String id);


    @PUT("api/commonPurchases/{id}/close")
    Call<BaseResponse<Object>>  closeJointPurchase (@Header("Authorization") String token,
                                                    @Path("id") String id);

    @PUT("api/commonPurchases/member/{id}/accept")
    Call<BaseResponse<Object>>  acceptJointPurchase (@Header("Authorization") String token,
                                                     @Path("id") String id);

    @PUT("api/commonPurchases/member/{id}/reject")
    Call<BaseResponse<Object>>  rejectJointPurchase (@Header("Authorization") String token,
                                                     @Path("id") String id);



    // 9) spending
    @GET("api/commonSpendings")
    Call<BaseResponse<ArrayList<GroupSpend>>> getSpending(@Header("Authorization") String token);

    @POST("api/commonSpendings")
    Call<BaseResponse<Object>> createSpending(@Header("Authorization") String token,
                                              @Body GroupSpendBody body);


    @DELETE("api/commonSpendings/{id}")
    Call<BaseResponse<Object>> deleteSpending(@Header("Authorization") String token,
                                              @Path("id") String id);

    @GET("api/commonSpendings/{id}")
    Call<BaseResponse<SpendDetailResult>> getSpendingDetails (@Header("Authorization") String token,
                                                              @Path("id") long id);

    @FormUrlEncoded @POST("api/commonSpendings/{id}/transactions")
    Call<BaseResponse<JsonElement>>  spendTransaction   (@Header("Authorization") String token,
                                               @Path("id") long id,
                                               @Field("returnUpdated") boolean returnUpdated,
                                               @Field("member_from") String memberFrom,
                                               @Field("member_to") String memberTo,
                                               @Field("amount") int amount,
                                               @Field("comment") String comment);

    @DELETE("api/commonSpendings/transactions/{trans_id}")
    Call<BaseResponse<Object>> deleteTransaction (@Header("Authorization") String token,
                                                 @Path("trans_id") String transId,
                                                 @Path("returnUpdated") boolean returnUpdated);


    @FormUrlEncoded @POST("api/moneyRequest/initFromCommonSpending")
    Call<BaseResponse<Object>> initInvoice(@Header("Authorization") String token,
                                           @Field("phone") String phone,
                                           @Field("card_id") String card,
                                           @Field("amount") String amount,
                                           @Field("comment") String comment,
                                           @Field("member_from") String memberFrom,
                                           @Field("member_to") String memberTo);
    // Charity

    @GET("api/charity")
    Call<BaseResponse<CharityResult>> getCharity (@Header("Authorization") String token);

    @Multipart @POST("api/charity")
    Call<BaseResponse<Object>> createCharity (@Header("Authorization") String token,
                                              @Part("title") RequestBody title,
                                              @Part("required_amount") RequestBody requiredAmount,
                                              @Part("init_collected_amount") RequestBody initCollAmount,
                                              @Part("description") RequestBody description,
                                              @Part("user_card_id") RequestBody userCardId,
                                              @Part("item_visibility") RequestBody itemVisibility,
                                              @Part("members_visibility") RequestBody membersVisibility,
                                              @Part("pseudonym") RequestBody pseudonym,
                                              @Part  MultipartBody.Part img);   //main_photo


    @Multipart @POST("api/charity/{charityId}/attemptpay")
    Call<BaseResponse<Object>> attemptPayCharity (@Header("Authorization") String token,
                                                  @Part("amount") RequestBody amount,
                                                  @Part("returnUpdated") RequestBody returnUpdated,
                                                  @Part("is_anonymous") RequestBody isAnonymous,
                                                  @Path("charityId") String charityId,
                                                  @Part("card_id") RequestBody cardId,
                                                  @Part("securityCode") RequestBody securityCode);

    @Multipart @POST("api/charity/{id}/uploadPhoto")
    Call<BaseResponse<Object>> uploadCharityPhoto  (@Header("Authorization") String token,
                                                    @Path("id") String id,
                                                    @Part  MultipartBody.Part img);  //main_photo


    @GET("api/charity/{id}")
    Call<BaseResponse<CharityResultById>> getCharityDetails   (@Header("Authorization") String token,
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


    @PUT("api/goods/{id}")
    Call<BaseResponse<Object>> editGoods(@Header("Authorization") String token,
                                         @Path("id") String id,
                                         @Query("description") String description,
                                         @Query("price") String price,
                                         @Query("category") String category,
                                         @Query("visibility") String visibility);
    // SelectedGoods
    @GET("api/goods")
    Call<BaseResponse<ArrayList<GoodsResults>>> getUserGoods (@Header("Authorization") String token);

    @GET("api/goods")
    Call<BaseResponse<ArrayList<GoodsResults>>> getGoods (@Header("Authorization") String token,
                                                          @Query("visibility") String visibility);
    @GET("api/goods")
    Call<BaseResponse<ArrayList<GoodsResults>>> getGoods (@Header("Authorization") String token,
                                                          @Query("category") List<String> category,
                                                          @QueryMap Map<String, String> options);

    @GET("api/goods/categories")
    Call<BaseResponse<ArrayList<CategoryResult>>>  getCategory (@Header("Authorization") String token);

    @DELETE("api/goods/{id}")
    Call<BaseResponse<Object>> deleteGoods (@Header("Authorization") String token,
                                           @Path("id") String id);


    @Multipart @POST("api/goods")
    Call<BaseResponse<Object>>  createGoods (@Header("Authorization") String token,
                                          @Part("title") RequestBody title,
                                          @Part("description") RequestBody description,
                                          @Part("price") RequestBody price,
                                          @Part("category") RequestBody category,
                                          @Part("visibility") RequestBody visibility,
                                          @Part MultipartBody.Part img);      //main_photo

    @Multipart @POST("api/goods/{id}/uploadPhoto")
    Call<BaseResponse<Object>>  uploadGoodsPhoto (@Header("Authorization") String token,
                                                  @Path("id") String id,
                                                  @Part MultipartBody.Part img);      //img            ?

    @GET("api/goods/{id}")
    Call<BaseResponse<GoodsByIdResult>> getGoodsDetails(@Header("Authorization") String token, @Path("id") String id);






    // Transaction (Transfer user-to-user)
    @FormUrlEncoded
    @POST("api/transfer")
    Call<BaseResponse<Object>>  transfer(@Header("Authorization") String token,
                                         @Field("user_id_to") String user_id,
                                         @Field("card_id") String card_id,
                                         @Field("securityCode") String securityCode,
                                         @Field("amount") long amount);

    @FormUrlEncoded
    @POST ("api/transfer/auditpay")
    Call<BaseResponse<Object>> auditpay(@Header("Authorization") String token,
                                                  @Field("fpt") String fpt,
                                                  @Field("id") String id);

    @FormUrlEncoded
    @POST ("api/transfer/sendlookup")
    Call<BaseResponse<String>> sendLookup(@Header("Authorization") String token,
                                          @Field("fpt") String ftp,
                                          @Field("id") String id,
                                          @Field("code") String code);




    // Transaction (Payment on incoming request)
    @FormUrlEncoded
    @POST("api/moneyRequest/{requestId}/attemptpay")
    Call<BaseResponse<Object>>  paymentIncoming(@Header("Authorization") String token,
                                         @Field("requestId") String requestId,
                                         @Field("card_id") String card_id,
                                         @Field("securityCode") String securityCode);

    @FormUrlEncoded
    @POST ("api/moneyRequest/{requestId}/auditpay")
    Call<BaseResponse<Object>> auditpayIncomging(@Header("Authorization") String token,
                                        @Field("fpt") String fpt,
                                        @Field("requestId") String requestId,
                                        @Field("id") String id);

    @FormUrlEncoded
    @POST ("api/moneyRequest/{requestId}/sendlookup")
    Call<BaseResponse<String>> sendLookupIncoming(@Header("Authorization") String token,
                                                  @Field("requestId") String requestId,
                                                  @Field("fpt") String fpt,
                                                  @Field("id") String id,
                                                  @Field("code") String code);


    // Transaction (Payment on joint purchase)
    @FormUrlEncoded
    @POST("api/commonPurchases/member/{purchasesId}/attemptpay")
    Call<BaseResponse<Object>>  paymentPurchase(@Header("Authorization") String token,
                                                @Field("purchasesId") String purchaseId,
                                                @Field("card_id") String card_id,
                                                @Field("securityCode") String securityCode);

    @FormUrlEncoded
    @POST ("api/commonPurchases/member/{purchasesId}/auditpay")
    Call<BaseResponse<Object>> auditpayPurchase(@Header("Authorization") String token,
                                                 @Field("fpt") String fpt,
                                                 @Field("purchasesId") String purchasesId,
                                                 @Field("id") String id);

    @FormUrlEncoded
    @POST ("api/commonPurchases/member/{purchasesId}/sendlookup")
    Call<BaseResponse<String>> sendLookupPurhcase(@Header("Authorization") String token,
                                                  @Field("purchasesId") String purchasesId,
                                                  @Field("fpt") String fpt,
                                                  @Field("id") String id,
                                                  @Field("code") String code);
}
