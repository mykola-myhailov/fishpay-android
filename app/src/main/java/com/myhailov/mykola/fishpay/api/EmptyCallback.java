package com.myhailov.mykola.fishpay.api;

import android.support.annotation.NonNull;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mykola Myhailov  on 11.12.17.
 */

public class EmptyCallback implements Callback<Void> {


    @Override
    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
        // do nothing
    }

    @Override
    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
        String errorMessage = t.getMessage();
        if (errorMessage == null) errorMessage = "t.getMessage() == null";
        Log.e("onFailure:  ", errorMessage);
    }
}
