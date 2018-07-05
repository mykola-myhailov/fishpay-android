package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.api.results.Transaction;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Mykola Myhailov  on 15.05.18.
 */
public class SpendTransactionsAdapter extends RecyclerView.Adapter<SpendTransactionsAdapter.TransactionHolder>{

    private Context context;
    private ArrayList<Transaction> transactions;


    public SpendTransactionsAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
        Log.e("transactons.size", transactions.size() + "");
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transfer_item, parent, false);
        return new TransactionHolder(itemView);
    }

    class TransactionHolder extends RecyclerView.ViewHolder{
        private SwipeRevealLayout swipeRevealLayout;
        private TextView tvDelete, tvFrom, tvTo,  tvDate, tvDescription, tvAmount, tvToTitle, tvFromTitle;
        private View container;

        public TransactionHolder(View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            tvFrom = itemView.findViewById(R.id.tv_from);
            tvTo = itemView.findViewById(R.id.tv_to);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
            container = itemView.findViewById(R.id.container);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            tvToTitle = itemView.findViewById(R.id.tv_to1);
            tvFromTitle = itemView.findViewById(R.id.tv_from1);
        }
    }

    @Override
    public void onBindViewHolder(TransactionHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        if (transaction == null) return;
        Utils.setText(holder.tvAmount, Utils.pennyToUah(transaction.getAmount()));
        Utils.setText(holder.tvDescription, transaction.getComment());
        Utils.setText(holder.tvFrom, transaction.getMemberFromName() + " " + transaction.getMemberFromSurname());
        String memberToName = transaction.getMemberToName() + " " + transaction.getMemberToSurname();
        if (memberToName.length() < 1) holder.tvTo.setVisibility(View.GONE);
        else holder.tvTo.setText(memberToName);

        if (TextUtils.isEmpty(holder.tvFrom.getText().toString()) || holder.tvFrom.getText().toString().equals(" ")){
            holder.tvFromTitle.setVisibility(View.GONE);
        }else {
            holder.tvFromTitle.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(holder.tvTo.getText().toString())|| holder.tvTo.getText().toString().equals(" ")){
            holder.tvToTitle.setVisibility(View.GONE);
        }else {
            holder.tvToTitle.setVisibility(View.VISIBLE);
        }



        holder.tvDate.setText(Utils.checkDateIsToday(context, transaction.getDate()));
//        Utils.setText(holder.tvDate, transaction.getDate());
      //  transaction.getM
    }


    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
