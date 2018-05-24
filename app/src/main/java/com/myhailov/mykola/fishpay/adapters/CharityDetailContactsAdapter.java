package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.CharityResultById;
import com.myhailov.mykola.fishpay.api.results.CharityResultById.Donation;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.List;

import okhttp3.internal.Util;

public class CharityDetailContactsAdapter extends RecyclerView.Adapter<CharityDetailContactsAdapter.ViewHolder> {

    private static final String TAG = CharityDetailContactsAdapter.class.getSimpleName();
    private Context context;
    private List<Donation> list;

    public CharityDetailContactsAdapter(Context context, List<Donation> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_charity_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Donation item = list.get(position);

        holder.tvName.setText(item.getFirstName() + " " + item.getSecondName());
        holder.tvAmount.setText(Utils.pennyToUah(item.getAmount()));
        String initials = Utils.extractInitials(item.getFirstName(), item.getSecondName());
        Utils.displayAvatar(context, holder.ivAvatar, "", initials);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    private TextView tvAmount;
    private ImageView ivAvatar;


        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}