package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Created by Mykola Myhailov  on 02.05.18.
 */
public class SpendDetailResult {

    @SerializedName("id")
    private long id;

    @SerializedName("creator")
    private long creatorId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("start_amount")
    private double startAmount;

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

    public double getStartAmount() {
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

    public class Transaction {

        @SerializedName("id")
        private long id;

        @SerializedName("member_from")
        private long memberFromId;

        @SerializedName("member_to")
        private long memberToId;

        @SerializedName("amount")
        private long amount;

        @SerializedName("comment")
        private String comment;

        @SerializedName("transfer_id")
        private long transferId;

        @SerializedName("member_from_first_name")
        private String memberFromName;

        @SerializedName("member_from_second_name")
        private String memberFromSurname;

        @SerializedName("created_at")
        private String created_at;

        public String getDate() { return created_at; }

        public long getId() {
            return id;
        }

        public long getMemberFromId() {
            return memberFromId;
        }

        public long getMemberToId() {
            return memberToId;
        }

        public long getAmount() {
            return amount;
        }

        public String getComment() {
            return comment;
        }

        public long getTransferId() {
            return transferId;
        }

        public String getMemberFromName() {
            return memberFromName;
        }

        public String getMemberFromSurname() {
            return memberFromSurname;
        }
    }

}
