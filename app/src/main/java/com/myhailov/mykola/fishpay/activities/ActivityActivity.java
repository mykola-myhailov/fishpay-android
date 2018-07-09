package com.myhailov.mykola.fishpay.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.adapters.ActivityAdapter;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.ActivityResult;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

public class ActivityActivity extends DrawerActivity {
    private TextView tvDateFrom, tvDateTo, tvEmptyLog;
    private RecyclerView rvLog;
    private LinearLayoutManager layoutManager;

    private boolean isFrom, isLastPage, isLoading;
    private String dateFrom, dateTo;
    private int start = 0, count = 30;
    private List<ActivityResult.LogResult> logResult = new ArrayList<>();
    private ActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        initDrawerToolbar(getString(R.string.activity));
        createDrawer();
        initView();
        getLog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date_from:
                isFrom = true;
                showDataPicker();
                break;
            case R.id.tv_date_to:
                isFrom = false;
                showDataPicker();
                break;
            case R.id.tv_today:
                clearCount();
                tvDateFrom.setText(getDate());
                tvDateTo.setText(getDate());
                logResult.clear();
                getLog();
                break;
            case R.id.tv_yesterday:
                clearCount();
                tvDateFrom.setText(getDateYesterday(yesterday()));
                tvDateTo.setText(getDateYesterday(yesterday()));
                logResult.clear();
                getLog();
                break;
            case R.id.tv_before_yesterday:
                clearCount();
                tvDateFrom.setText(getDateYesterday(beforeYesterday()));
                tvDateTo.setText(getDateYesterday(beforeYesterday()));
                logResult.clear();
                getLog();
                break;
        }
    }

    private void initView() {
        tvDateTo = findViewById(R.id.tv_date_to);
        tvDateFrom = findViewById(R.id.tv_date_from);
        rvLog = findViewById(R.id.rv_log);
        tvEmptyLog = findViewById(R.id.tv_empty_log);
        layoutManager = new LinearLayoutManager(context);
        rvLog.setLayoutManager(layoutManager);
        rvLog.addOnScrollListener(rvOnScrolListener);
        tvDateFrom.setText(getDate());
        tvDateTo.setText(getDate());
        adapter = new ActivityAdapter(context, logResult, null);
        rvLog.setAdapter(adapter);


        tvDateTo.setOnClickListener(this);
        tvDateFrom.setOnClickListener(this);
        findViewById(R.id.tv_today).setOnClickListener(this);
        findViewById(R.id.tv_yesterday).setOnClickListener(this);
        findViewById(R.id.tv_before_yesterday).setOnClickListener(this);

    }

    private RecyclerView.OnScrollListener rvOnScrolListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    start = count;
                    count += 30;
                    getLog();
                }
            }
        }
    };

    private void clearCount() {
        start = 0;
        count = 30;
        isLastPage = false;
    }

    private String getDate() {
        SimpleDateFormat DateFormat = new SimpleDateFormat("d.MM.y");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("y-MM-dd");
        Date convertedDate = new Date();
        dateFrom = newDateFormat.format(convertedDate);
        dateTo = dateFrom;
        return DateFormat.format(convertedDate);
    }

    private String getDateYesterday(Date date) {
        SimpleDateFormat DateFormat = new SimpleDateFormat("d.MM.y");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("y-MM-dd");
        dateFrom = newDateFormat.format(date);
        dateTo = dateFrom;
        return DateFormat.format(date);
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private Date beforeYesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        return cal.getTime();
    }

    private void getLog() {
        if (Utils.isOnline(context)) {
            isLoading = true;
            ApiClient.getApiInterface().getLog(TokenStorage.getToken(context), dateFrom, dateTo, start, count)
                    .enqueue(new BaseCallback<ActivityResult>(context, true) {
                        @Override
                        protected void onResult(int code, ActivityResult result) {
                            isLoading = false;
                            if (result.getResult().size() == 0) {
                                isLastPage = true;
                                if (logResult.size() == 0) {
                                    tvEmptyLog.setVisibility(View.VISIBLE);
                                    rvLog.setVisibility(View.GONE);
                                }
                            } else {
                                if (rvLog.getVisibility() == View.GONE){
                                    tvEmptyLog.setVisibility(View.GONE);
                                    rvLog.setVisibility(View.VISIBLE);
                                }
                                logResult.addAll(result.getResult());
                            }
                            adapter.notifyDataSetChanged();

                        }
                    });

        } else Utils.noInternetToast(context);
    }

    private void showDataPicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                createDateSetListener(calendar), calendar.get(Calendar.YEAR),
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH);
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener createDateSetListener(final Calendar calendar) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Locale locale = getResources().getConfiguration().locale;
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat visibleFormat = new SimpleDateFormat("d.MM.y", locale);
                SimpleDateFormat savedFormat = new SimpleDateFormat("y-MM-dd", new Locale("ru"));

                if (isFrom) {
                    tvDateFrom.setText(visibleFormat.format(calendar.getTimeInMillis()));
                    dateFrom = savedFormat.format(calendar.getTimeInMillis());
                } else {
                    tvDateTo.setText(visibleFormat.format(calendar.getTimeInMillis()));
                    dateTo = savedFormat.format(calendar.getTimeInMillis());
                }
                clearCount();
                logResult.clear();
                getLog();

            }
        };
    }
}
