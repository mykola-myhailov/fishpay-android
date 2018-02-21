package com.myhailov.mykola.fishpay.views;

import android.widget.TextView;

/**
 * Created by nicholas on 20.02.18.
 */

public class Tab {
    private boolean selected;
    private String title;
    private int position;
    private TextView view;

    public Tab(boolean selected, String title, int position, TextView view) {
        this.selected = selected;
        this.title = title;
        this.position = position;
        this.view = view;
    }

    public Tab(String title, int position) {
        this.title = title;
        this.position = position;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    public TextView getView() {
        return view;
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setView(TextView view) {
        this.view = view;
    }
}