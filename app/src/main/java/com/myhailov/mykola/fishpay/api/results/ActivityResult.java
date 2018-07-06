package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActivityResult {
    @SerializedName("result")
    private List<LogResult> result;

    public List<LogResult> getResult() {
        return result;
    }

    public void setResult(List<LogResult> result) {
        this.result = result;
    }

    public class LogResult{

    @SerializedName("create_at")
    private String createAt;
    @SerializedName("text")
    private String text;

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    }
}
