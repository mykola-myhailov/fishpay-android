package com.myhailov.mykola.fishpay.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.login.BeginActivity;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  Created by Mykola Myhailov  on 14.11.17.
 *
 *  <T> is value type of field "result" in is response json {@link BaseResponse}
 *  necessary model classes are in in package {@link com.myhailov.mykola.fishpay.api.results}
 *
 *  */

public abstract class BaseCallback<T> implements Callback<BaseResponse<T>> {

    private Context context;
    private ProgressDialog progressDialog;

    protected abstract void onResult(int code, T result);


/*    protected void onError(int code, String errorDescription){
        switch (code){
            case 401:
                Utils.alert(context, "Время сессии истекло");
                context.startActivity(new Intent(context, BeginActivity.class));
                break;
            default:
                if (errorDescription != null) Utils.alert(context, errorDescription);
                else Utils.alert(context, context.getString(R.string.error));
                break;
        }

    }*/


    protected void onError(int code, String errorDescription){
        switch (code){
            case 241:
                Utils.alert(context, context.getString(R.string.session_end));
                context.startActivity(new Intent(context, BeginActivity.class));
                break;
            default:
                if (errorDescription != null) Utils.toast(context, errorDescription);
                else Utils.toast(context, context.getString(R.string.error));
                break;
        }
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

    protected void closeProgressDialog(){
        if (progressDialog != null) progressDialog.cancel();
    }




    @Override
    public void onResponse(@NonNull Call<BaseResponse<T>> call, @NonNull Response<BaseResponse<T>> response) {
        closeProgressDialog();
        if (context == null) return;
        int code = response.code();

     /*   if (response.isSuccessful()){
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
        }*/

        if (response.isSuccessful() && response.body() != null){
            BaseResponse<T> body = response.body();
            if (code < 240)onResult(code, body.getResult());
            else onError(code, body.getErrorDescription());
        }
        else Utils.toast(context, context.getString(R.string.error));
    }

    @Override
    public void onFailure(@NonNull Call<BaseResponse<T>> call, @NonNull Throwable t) {
        closeProgressDialog();
        if (context == null) return;
        if (t.getMessage() != null) {
            Log.e("onFailure", t.getMessage());
            Utils.toast(context, t.getMessage());
        }
    }
}
