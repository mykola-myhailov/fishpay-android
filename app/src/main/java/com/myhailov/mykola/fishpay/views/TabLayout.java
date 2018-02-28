package com.myhailov.mykola.fishpay.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;

import java.util.ArrayList;

/**
 * Created by nicholas on 20.02.18.
 */

public class TabLayout extends LinearLayout implements View.OnClickListener {

    private Context context;
    private ArrayList<Tab> tabs;
    private int currentTab;
    public OnTabChangedListener tabChangedListener;

    public TabLayout(Context context) {
        super(context);
        init(context);
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(HORIZONTAL);
        tabs = new ArrayList<>();
    }

    public void addTab(Tab tab) {
        tabs.add(tab);
        build();
    }

    private void build() {
        removeAllViews();
        for (Tab tab : tabs) {
            int position = tab.getPosition();
            int back;
            if (position == 0) back = R.drawable.drawable_left_tab;
            else if (position == tabs.size()-1) back = R.drawable.drawable_right_tab;
            else back = R.drawable.drawable_center_tab;

            TextView view = (TextView) LayoutInflater.from(context).inflate(R.layout.item_tab, this,false);
            view.setBackground(getResources().getDrawable(back));
            view.setTag(tab);
            view.setOnClickListener(this);
            view.setText(tab.getTitle());
            view.setActivated(tab.isSelected());
            addView(view);
            tab.setView(view);
            if (position == 0) tab.setSelected(true);
            else tab.setSelected(false);
        }
        currentTab = 0;
    }

    private void notifyTabs() {
        for (Tab tab : tabs) {
            tab.getView().setActivated(tab.isSelected());
        }
    }

    public void setTabSelectedAt(int position) {
        if (position <= tabs.size()-1) {
            tabs.get(position).getView().performClick();
        }
    }

    @Override
    public void onClick(View v) {
        int position = ((Tab) v.getTag()).getPosition();
        if (currentTab != position) {
            for (Tab tab : tabs) {
                tab.setSelected(false);
            }
            tabs.get(position).setSelected(true);
            currentTab = position;
            notifyTabs();
            if (tabChangedListener != null) tabChangedListener.onTabChanged(position);
        }
    }

    public void setTabChangedListener(OnTabChangedListener tabChangedListener) {
        this.tabChangedListener = tabChangedListener;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public interface OnTabChangedListener {
        void onTabChanged(int position);
    }
}
