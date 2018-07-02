package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mykola Myhailov  on 02.05.18.
 */
public class SpendDetailResult implements Serializable{

    @SerializedName("id")
    private long id;

    @SerializedName("creator")
    private long creatorId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("start_amount")
    private long startAmount;

    @SerializedName("status")
    private String status;

    @SerializedName("member_id")
    private long  memberId;

    @SerializedName("total_sum")
    private long sum;

    @SerializedName("members")
    private ArrayList<MemberDetails> members;

    @SerializedName("transactions")
    private ArrayList<Transaction> transactions;

    public void setId(long id) {
        this.id = id;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartAmount(long startAmount) {
        this.startAmount = startAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public void setMembers(ArrayList<MemberDetails> members) {
        this.members = members;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public long getId() {
        return id;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public String getTitle() {
        if (title == null) return "?";
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getStartAmount() {
        return startAmount;
    }

    public String getStatus() {
        return status;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getSum() {
        return sum;
    }

    public ArrayList<MemberDetails> getMembers() {
        return members;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

}
