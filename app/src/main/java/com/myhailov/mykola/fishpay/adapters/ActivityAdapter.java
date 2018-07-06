package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.ActivityResult;

import java.util.List;

import static com.myhailov.mykola.fishpay.utils.Utils.convertStringToDateWithCustomFormat;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private static final String TAG = ActivityAdapter.class.getSimpleName();
    private Context context;
    private List<ActivityResult.LogResult> list;
    private OnItemClickListener listener;

    public ActivityAdapter(Context context, List<ActivityResult.LogResult> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActivityResult.LogResult item = list.get(position);
        holder.tvDate.setText(convertStringToDateWithCustomFormat(context, item.getCreateAt(), "d.MM.y"));
        holder.tvTime.setText(convertStringToDateWithCustomFormat(context, item.getCreateAt(), "H:mm:ss"));
        holder.tvText.setText(item.getText());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listener.onItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvText = itemView.findViewById(R.id.tv_text);
        }
    }
}