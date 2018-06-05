package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.CharityResult;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Utils.convertStringToDate;

public class CharityListAdapter extends RecyclerView.Adapter<CharityListAdapter.ViewHolder> {

    private static final String TAG = CharityListAdapter.class.getSimpleName();
    private Context context;
    private List<CharityResult.Donation> list;

    public CharityListAdapter(Context context, List<CharityResult.Donation> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_charity_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CharityResult.Donation item = list.get(position);

        holder.tvDescription.setText(item.getTitle());
        holder.tvAuthor.setText(item.getAuthorName());
        holder.tvAmount.setText(Utils.pennyToUah(item.getAmount()));
        holder.tvDate.setText(convertStringToDate(item.getCreatedAt()));

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
        TextView tvDescription;
        TextView tvAmount;
        TextView tvAuthor;
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}