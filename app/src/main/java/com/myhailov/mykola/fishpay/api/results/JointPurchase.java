package com.myhailov.mykola.fishpay.api.results;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mykola Myhailov  on 15.12.17.
 *
 "id":41,
 "title":"\u0433\u043e\u0430",
 "creator":127,
 "description":null,
 "date_to":"",
 "status":"VIEWED",
 "amount_to_pay":2266,
 "created_at":"2018-02-27 14:32:54",
 "contact_creator_first_name":"Kiev",
 "contact_creator_last_name":null,
 "creator_first_name":"Nichlas",
 "creator_last_name":"Oli"
 */

public class JointPurchase {
    private String id;
    @SerializedName("creator") private String creatorId;
    private String title;
    private String description;
    @SerializedName("date_to") private String to;
    private String status;
    @SerializedName("amount_to_pay") private int amountToPay;
    @SerializedName("creator_first_name") private String creatorFirstName;
    @SerializedName("creator_last_name") private String creatorLastName;
    private String amount;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTo() {
        if (to == null) return "-";
        else {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(to);
                return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return "-";
            }
            //return to;
        }
    }

    public String getStatus() {
        return status;
    }

    public int getAmountToPay() {
        return amountToPay;
        /*if (amount == null) {
            if (amountToPay == 0) return "-";
            if (amountToPay < 100) {
                if (amountToPay < 10) return "0.0" + amountToPay;
                return "0." + amountToPay;
            }
            char[] s = String.valueOf(amountToPay).toCharArray();
            int length = s.length;
            StringBuilder r = new StringBuilder(length + 1);
            int i = 0;
            for (char c : s) {
                r.append(c);
                if (length - i == 3) r.append(".");
                i++;
            }
            amount = r.toString();
        }
        return amount;*/
    }

    public String getCreatorFirstName() {
        return creatorFirstName;
    }

    public String getCreatorLastName() {
        return creatorLastName;
    }

    public String getCreatorName() {
        if (creatorLastName != null) return creatorFirstName + " " +creatorLastName;
        return creatorFirstName;
    }

    public String getCreatorId() {
        return creatorId;
    }
}
