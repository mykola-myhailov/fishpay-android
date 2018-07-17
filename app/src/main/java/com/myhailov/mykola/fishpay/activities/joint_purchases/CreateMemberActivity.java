package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.database.Contact;

import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;

public class CreateMemberActivity extends BaseActivity {

    private EditText etName, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_member);

        initCustomToolbar(getString(R.string.add_participant));
        initViews();
    }

    private void initViews() {
        etName = findViewById(R.id.et_member_name);
        etPhone = findViewById(R.id.et_member_phone);
        findViewById(R.id.tv_add).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_add:
                if (isDataValid()) {
                    Contact contact = new Contact();
                    contact.setUserId(-1);
                    String phone = etPhone.getText().toString();
                    if (phone.charAt(0) == '+') {
                        contact.setPhone(phone.substring(1));
                    } else {
                        contact.setPhone(phone);
                    }
                    String nameTmp = etName.getText().toString().replaceAll("[\\-\\+\\.\\^:,]", "");
                    String[] name = nameTmp.split(" ");
                    contact.setName(name[0]);
                    if (name.length > 1) {
                        contact.setSurname(name[1]);
                    }
                    setResult(RESULT_OK, new Intent().putExtra(CONTACT, contact));
                    finish();
                }
        }
    }

    private boolean isDataValid() {
        if (etName.getText().toString().length() > 0) return true;
        toast(getString(R.string.enter_participant_name));
        return false;
    }
}
