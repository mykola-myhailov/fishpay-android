package com.myhailov.mykola.fishpay.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by nicholas on 03.03.18.
 */

public class MoneyEditText extends android.support.v7.widget.AppCompatEditText {
    public MoneyEditText(Context context) {
        super(context);
        init();
    }

    public MoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoneyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                String string = s.toString();
                int afterDot = getAfterDot();
                if (afterDot > 2) {
                    int dot = string.indexOf('.');
                    s.delete(dot, dot+1);
                }
                if (string.equals(".")) s.insert(0, "0");
                if (string.equals("00")) s.insert(1, ".");
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    EditText et = (EditText) v;
                    String string = et.getText().toString();
                    int length = et.getText().length();
                    boolean hasDot = string.contains(".");
                    int afterDot = getAfterDot();
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_0:
                        case KeyEvent.KEYCODE_1:
                        case KeyEvent.KEYCODE_2:
                        case KeyEvent.KEYCODE_3:
                        case KeyEvent.KEYCODE_4:
                        case KeyEvent.KEYCODE_5:
                        case KeyEvent.KEYCODE_6:
                        case KeyEvent.KEYCODE_7:
                        case KeyEvent.KEYCODE_8:
                        case KeyEvent.KEYCODE_9:
                            if (!hasDot) {
                                if (length >= 10) return true;
                            } else {
                                if (getSelectionStart() <= length - (1+getAfterDot())) return false;
                                if (afterDot >= 2) return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    private int getAfterDot() {
        String string = getText().toString();
        int length = getText().length();
        boolean hasDot = string.contains(".");
        int afterDot = 0;
        if (hasDot) {
            afterDot = (length - 1) - string.indexOf('.');
        }
        return afterDot;
    }

    public int getCurrency() {
        String string = getText().toString();
        if (string.length() > 0) {
            if (string.contains(".")) {
                String s = string.replace(".", "");
                if (getAfterDot() == 1) s = s + "0";
                return Integer.parseInt(s);
            } else return Integer.parseInt(string)*100;
        }
        return 0;
    }
}
