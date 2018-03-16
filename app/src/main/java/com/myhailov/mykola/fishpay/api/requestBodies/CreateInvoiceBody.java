package com.myhailov.mykola.fishpay.api.requestBodies;

import java.util.ArrayList;

import retrofit2.http.Field;

/**
 * Created by Mykola Myhailov  on 16.03.18.
 */

public class CreateInvoiceBody {
    String phone;
    String card;
    String amount;
    String comment;
    String memberId;
    ArrayList<Goods> goods;

    public CreateInvoiceBody(String phone, String card, String amount, String comment, String memberId, ArrayList<Goods> goods) {
        this.phone = phone;
        this.card = card;
        this.amount = amount;
        this.comment = comment;
        this.memberId = memberId;
        this.goods = goods;
    }
}
