package com.myhailov.mykola.fishpay.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.json.JSONObject;

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

    protected abstract void onResult(int code, T result);


    protected void onError(int code, String errorDescription){
        if (errorDescription != null) Utils.alert(context, errorDescription);
        else Utils.alert(context, context.getString(R.string.error));
    }

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
        int code = response.code();
        if (response.isSuccessful()){
            BaseResponse<T> body = response.body();
            if (body != null) {
                T result = body.getResult();
                onResult(code, result);
            }
            else Utils.alert(context, "Error");
        }

        else {
            try {
                JSONObject jError = new JSONObject(response.errorBody().string());
                onError(code, jError.getString("errorDescription"));
            } catch (Exception e) {Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();}
        }

    }

    @Override
    public void onFailure(@NonNull Call<BaseResponse<T>> call, @NonNull Throwable t) {
        if (progressDialog != null) progressDialog.cancel();
        if (context == null) return;
        Log.e("onFailure", t.getMessage());
        Utils.alert(context, t.getMessage());
    }
}
