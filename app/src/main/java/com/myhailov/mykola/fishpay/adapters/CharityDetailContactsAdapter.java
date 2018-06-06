package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.CharityResultById.Donation;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.List;

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

        if (item.isAnonymous()) {
            holder.tvName.setText(context.getString(R.string.anonymous));
            String initials = Utils.extractInitials(context.getString(R.string.anonymous), "");
            Utils.displayAvatar(context, holder.ivAvatar, "", initials);
        } else {
            String name = "";
            String secondName = "";
            if (!TextUtils.isEmpty(item.getFirstName()) && !item.getFirstName().equals("null")) {
                name = item.getFirstName();
            }
            if (!TextUtils.isEmpty(item.getSecondName()) && !item.getSecondName().equals("null")) {
                secondName = item.getSecondName();
            }

            holder.tvName.setText(name + " " + secondName);
            String initials = Utils.extractInitials(name, secondName);
            Utils.displayAvatar(context, holder.ivAvatar, "", initials);
        }

        holder.tvAmount.setText(Utils.pennyToUah(item.getAmount()));

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