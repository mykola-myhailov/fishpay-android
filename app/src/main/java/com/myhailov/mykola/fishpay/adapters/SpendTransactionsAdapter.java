package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult.Transaction;
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
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transfer_item, parent, false);
        return new TransactionHolder(itemView);
    }

    class TransactionHolder extends RecyclerView.ViewHolder{
        private SwipeRevealLayout swipeRevealLayout;
        private TextView tvName, tvInitials, tvDelete, tvFrom, tvTo,  tvDate, tvDescription, tvSum, tvAmount;
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
        }
    }

    @Override
    public void onBindViewHolder(TransactionHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        if (transaction == null) return;
        Utils.setText(holder.tvAmount, transaction.getAmount() );
        Utils.setText(holder.tvDescription, transaction.getComment());
        Utils.setText(holder.tvFrom, transaction.getMemberFromName() + " " + transaction.getMemberFromSurname());

        Utils.setText(holder.tvDate, transaction.getDate());

        ;
      //  transaction.getM
    }


    @Override
    public int getItemCount() {
        return transactions.size();
    }




}
