package com.myhailov.mykola.fishpay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

public class RegistrationActivity extends BaseActivity {

    private String phone, name, surname, email, birthday, deviceId, deviceInfo;
    private EditText etName, etSurname, etEmail, etBirthday;
    private ImageView ivAvatar;
    private boolean showPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Bundle extras  = getIntent().getExtras();
        assert extras != null;
        phone = extras.getString(Keys.PHONE);

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etBirthday= findViewById(R.id.etBirthday);

        findViewById(R.id.tvSave).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSave:
                name = etName.getText().toString();
                surname = etSurname.getText().toString();
                email = etEmail.getText().toString();
                if (name.equals("")) Utils.toast(context, getString(R.string.enter_name));
                else if (surname.equals("")) Utils.toast(context,getString(R.string.enter_surname));
              //  else if (birthday == null) Utils.toast(context, getString(R.string.enter_birthday));
                else if (surname.length() < 2 || surname.length() > 20 )
                    Utils.toast(context, getString(R.string.wrong_surname));
                else {
                    Intent intent = new Intent(context, SetPasswordActivity.class);
                    intent.putExtra(Keys.PHONE, phone);
                    intent.putExtra(Keys.NAME, name);
                    intent.putExtra(Keys.SURNAME, surname);
                    intent.putExtra(Keys.BIRTHDAY, birthday);
                    intent.putExtra(Keys.EMAIL, email);
                    context.startActivity(intent);
                }

                break;
        }

    }
}
