package com.myhailov.mykola.fishpay.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
   // https://api.uapay.ua/api/payments/p2p/confirm?id=11a36460-5e3e-4470-8753-c03d91be49f4&redirect=https://www.google.com&key=6FWWLpDBzHx3rwo1tNpdA3jKzkR4RcKoVNADmRHXdvqG.jkxymiom309346431777

    // test
   // public static final String BANK_REDIRECT = "http://bank4u.pp.ua/";
    public static final String BANK_REDIRECT = "https://api.uapay.ua/api/payments/p2p/confirm";
    public static final String BASE_API_URL = "http://bank4u.pp.ua/rest/";
    //prod
//    public static final String BANK_REDIRECT = "http://167.99.240.38/";
//    public static final String BASE_API_URL = "http://167.99.240.38/rest/";

    private static Retrofit retrofit = null;

    public static ApiInterface getApiInterface() {
        return getRetrofit().create(ApiInterface.class);
    }


    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
