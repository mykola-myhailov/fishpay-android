package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;

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

    private class MemberDetails {

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
    }

    private class Transaction {

        @SerializedName("id")
        private long id;


/*
        "id": 101,
                "member_from": 136,
                "member_to": null,
                "amount": 200000,
                "request_id": null,
                "comment": "Тесты",
                "created_at": "2018-04-13 12:05:17",
                "transfer_id": null,
                "member_from_first_name": "Bogonos",
                "member_from_second_name": null*/
    }
}
