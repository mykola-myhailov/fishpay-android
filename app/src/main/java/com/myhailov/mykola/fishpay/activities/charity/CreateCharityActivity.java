package com.myhailov.mykola.fishpay.activities.charity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.requestBodies.CharityRequestBody;
import com.myhailov.mykola.fishpay.api.results.Card;

import static com.myhailov.mykola.fishpay.utils.Keys.CHARITY_CREATE;

public class CreateCharityActivity extends BaseActivity {
    private EditText etTitle;
    private EditText etRequiredAmount;
    private EditText etCollectedAmount;
    private EditText etDescription;
    private Switch switchIndefinitely;
    private String pathPhoto;
    private Card card;


    private CharityRequestBody charity = new CharityRequestBody();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_charity);
        initCustomToolbar("Создать благотворительный сбор");
        assignViews();
    }

    private void assignViews() {
        findViewById(R.id.tv_create).setOnClickListener(this);
        etTitle = findViewById(R.id.et_title);
        etRequiredAmount = findViewById(R.id.et_total_amount);
        etCollectedAmount = findViewById(R.id.et_collected_amount);
        etDescription = findViewById(R.id.et_description);
        switchIndefinitely = findViewById(R.id.switch_indefinitely);
    }

    private boolean checkViews(){
//        if (TextUtils.isEmpty(etTitle.getText())){
//            etTitle.setError(getString(R.string.error_fill_information));
//            return false;
//        }
//        if (TextUtils.isEmpty(etDescription.getText())){
//            etDescription.setError(getString(R.string.error_fill_information));
//            return false;
//        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_create:
                if (checkViews()) {
                    Intent intent = new Intent(context, CharitySettingsActivity.class);
                    intent.putExtra(CHARITY_CREATE, true);
                    startActivity(intent);
                }
                break;
        }
    }


}
