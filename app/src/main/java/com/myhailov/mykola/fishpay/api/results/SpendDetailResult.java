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

    public class MemberDetails {

        @SerializedName("id")
        private long id;

        @SerializedName("member_part")
        private double part;

        @SerializedName("member_status")
        private String status;

        @SerializedName("type")
        private String type;

        @SerializedName("role")
        private String role;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("phone")
        private String phone;

        @SerializedName("first_name")
        private String name;

        @SerializedName("second_name")
        private String surname;

        @SerializedName("photo_link")
        private String photo;

        @SerializedName("total_member_sum")
        private long sum;

        @SerializedName("size_commitment")
        private long commitment;

        @SerializedName("overpayment")
        private long overpaiment;

        @SerializedName("part_in_overpaiment")
        private double partInOverpaiment;

        @SerializedName("relative_balance")
        private double relativeBallance;

        public long getId() {
            return id;
        }

        public double getPart() {
            return part;
        }

        public String getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }

        public String getRole() {
            return role;
        }

        public String getUserId() {
            return userId;
        }

        public String getPhone() {
            return phone;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public String getPhoto() {
            return photo;
        }

        public long getSum() {
            return sum;
        }

        public long getCommitment() {
            return commitment;
        }

        public long getOverpaiment() {
            return overpaiment;
        }

        public double getPartInOverpaiment() {
            return partInOverpaiment;
        }

        public double getRelativeBallance() {
            return relativeBallance;
        }
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
