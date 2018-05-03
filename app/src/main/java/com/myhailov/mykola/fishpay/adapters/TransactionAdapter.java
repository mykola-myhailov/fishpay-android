package com.myhailov.mykola.fishpay.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.myhailov.mykola.fishpay.api.results.SpendDetailResult.Transaction;

import java.util.ArrayList;

import com.myhailov.mykola.fishpay.R;
/**
 * Created by Mykola Myhailov  on 03.05.18.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {

    private ArrayList<Transaction> transactions;


    public TransactionAdapter(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transfer_item, parent, false);
        return new TransactionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class TransactionHolder extends RecyclerView.ViewHolder{

        private final SwipeRevealLayout swipeRevealLayout;
       // private final TextView tvDescription, tvSum, tvFrom, tvDate, tvDelete;

        public TransactionHolder(View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
           // tvDescription = itemView.findViewById(R.id.tv_description);
        //    tvFrom = itemView.findViewById(r)
        }
    }
}
