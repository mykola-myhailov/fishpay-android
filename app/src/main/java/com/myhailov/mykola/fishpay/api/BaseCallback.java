package com.myhailov.mykola.fishpay.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.models.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Created by Mykola Myhailov  on 14.11.17. */

public class BaseCallback<T> implements Callback<BaseResponse<T>> {

    protected Context context;
    private ProgressDialog progressDialog;



    protected void onSuccessful(int code, T result) {

    }

    protected void onError(int code, String errorDescription) {
        Toast.makeText(context, errorDescription, Toast.LENGTH_SHORT).show();
    }

    protected BaseCallback(Context context, boolean showProgress) {
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
        BaseResponse<T> body = response.body();
        if (body == null) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        int code = response.code();
        T result = body.getResult();
        String errorDescription = body.getErrorDescription();


        if (response.isSuccessful()) onSuccessful(code, result);
        else onError(code, errorDescription);
    }



    @Override
    public void onFailure(@NonNull Call<BaseResponse<T>> call, @NonNull Throwable t) {
        if (progressDialog != null) progressDialog.cancel();
    }
}
