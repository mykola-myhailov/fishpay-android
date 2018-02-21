package com.myhailov.mykola.fishpay.activities;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.myhailov.mykola.fishpay.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.widget.Toast.LENGTH_SHORT;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected Context context;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) onBackPressed();
    }

    protected void initToolBar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
            actionBar.setDisplayShowCustomEnabled(true);
        }
        TextView tvToolBarTitle = findViewById(R.id.tvToolBarTitle);
        tvToolBarTitle.setText(title.toUpperCase());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    protected void initCustomToolbar(String title) {
        findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvToolBarTitle)).setText(title);
    }

    protected void toast(String message) {
        if (toast == null) toast = Toast.makeText(context, "", LENGTH_SHORT);
        toast.setText(message);
        toast.show();
    }
}
