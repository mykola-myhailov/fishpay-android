package com.myhailov.mykola.fishpay.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  Created by Mykola Myhailov  on 14.11.17.
 *
 *  <T> is value type of field "result" in is response json {@link BaseResponse}
 *  necessary model classes are in in package {@link com.myhailov.mykola.fishpay.api.models}
 *
 *  */

public abstract class BaseCallback<T> implements Callback<BaseResponse<T>> {

    private Context context;
    private ProgressDialog progressDialog;

    //protected abstract void onResult(int code, @Nullable T result, @Nullable String errorDescription);
    protected abstract void onResult(int code, @Nullable T result);

    protected BaseCallback(@NonNull Context context, boolean showProgress) {
        this.context = context;
        if (showProgress) {
            progressDialog = new ProgressDialog(context, R.style.CustomDialogTheme);
            progressDialog.setProgressStyle(R.style.CustomAlertDialogStyle);

            progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
            this.progressDialog.show();
        }
    }

    @Override
    public void onResponse(@NonNull Call<BaseResponse<T>> call, @NonNull Response<BaseResponse<T>> response) {
        if (progressDialog != null) progressDialog.cancel();
        if (context == null) return;
        BaseResponse<T> body = response.body();
        if (body == null) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        int code = response.code();
        T result = body.getResult();
        String errorDescription = body.getErrorDescription();

        //onResult(code, result, errorDescription);
        onResult(code, result);
    }

    @Override
    public void onFailure(@NonNull Call<BaseResponse<T>> call, @NonNull Throwable t) {
        if (progressDialog != null) progressDialog.cancel();
        if (context == null) return;
        Log.e("onFailure", t.getMessage());
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
