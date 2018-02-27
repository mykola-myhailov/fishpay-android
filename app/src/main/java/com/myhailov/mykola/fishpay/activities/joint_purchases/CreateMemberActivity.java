package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Keys;

import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;

public class CreateMemberActivity extends BaseActivity {

    private EditText etName, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_member);

        initCustomToolbar("добавление участника");
        initViews();
    }

    private void initViews() {
        etName = findViewById(R.id.et_member_name);
        etPhone = findViewById(R.id.et_member_phone);
        findViewById(R.id.tv_add).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_add && isDataValid()) {
            Contact contact = new Contact();
            contact.setUserId(-1);
            contact.setPhone(etPhone.getText().toString());
            contact.setName(etName.getText().toString());
            setResult(RESULT_OK, new Intent().putExtra(CONTACT, contact));
            finish();
        }
    }

    private boolean isDataValid() {
        if (etName.getText().toString().length() > 0) return true;
        toast("Введите имя участника!");
        return false;
    }
}
