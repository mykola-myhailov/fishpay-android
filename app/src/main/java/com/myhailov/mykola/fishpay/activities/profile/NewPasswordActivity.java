package com.myhailov.mykola.fishpay.activities.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.login.BeginActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

public class NewPasswordActivity extends BaseActivity {

    private String passChangeId;
    private EditText etPassword;
    private TextView tvOk;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        passChangeId = getIntent().getExtras().getString(Keys.PASS_CHANGE_ID);
        findViewById(R.id.tvNext).setOnClickListener(this);
        findViewById(R.id.ivNextArrow).setOnClickListener(this);
        etPassword = findViewById(R.id.etPassword);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    checkPassword();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvNext:
                checkPassword();
                break;
            case R.id.ivNextArrow:
                checkPassword();
                break;
            case R.id.tv_action_1:
                alertDialog.cancel();
                startActivity(new Intent(context, BeginActivity.class));
                break;
        }
    }

    private void checkPassword(){
        String password = etPassword.getText().toString();
        if (password.equals("")) Utils.toast(context, getString(R.string.enter_password));
        else if (password.length() < 8) Utils.toast(context, getString(R.string.short_password));
        else if (!Utils.isOnline(context)) Utils.noInternetToast(context);
        else  changePassRequest(password);
    }

    private void changePassRequest(String newPassword) {
        String token = TokenStorage.getToken(context);
        Bundle extras = getIntent().getExtras();
        if (extras != null) passChangeId = extras.getString(Keys.PASS_CHANGE_ID);

        ApiClient.getApiInterface().changePass(token, newPassword, passChangeId).enqueue(
                new BaseCallback<Object>(context, true) {
                    @Override
                    protected void onResult(int code, Object result) {
                        new AlertDialog.Builder(context)
                                .setMessage("Пароль успешно изменён. Для входа в систему воспользуйтесь новым паролем")
                                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        context.startActivity(new Intent(context, BeginActivity.class));
                                    }
                                })
                                .create().show();
                    }
                }
        );
    }
}
