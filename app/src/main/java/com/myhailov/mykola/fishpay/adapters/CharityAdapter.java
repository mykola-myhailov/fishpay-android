package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.CharityResult.CharityProgram;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.ViewHolder> {
    private static final String TAG = CharityAdapter.class.getSimpleName();
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private Context context;
    private List<CharityProgram> list;
    private OnItemClickListener listener;

    public CharityAdapter(Context context, List<CharityProgram> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_charity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CharityProgram item = list.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvName.setText(item.getAuthorName());
        if (item.getRequiredAmount() != 0) {
            double percent = ((double)item.getTotalAmount() / (double)item.getRequiredAmount()) * 100;
            holder.tvPercent.setText(new DecimalFormat("#0.00").format(percent) + " %");
            holder.tvPercent.setVisibility(View.VISIBLE);
        }else {
            holder.tvPercent.setVisibility(View.INVISIBLE);
        }
        holder.tvGoal.setText(Utils.pennyToUah(item.getTotalAmount()) + " |грн");

        holder.tvReport.setOnClickListener((View.OnClickListener) context);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item.getId().toString(), item);
            }
        });

        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(item.getId()));
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public void setList(List<CharityProgram> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String id, CharityProgram item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvName;
        private TextView tvGoal;
        private TextView tvPercent;
        private TextView tvReport;
        private View container;
        private SwipeRevealLayout swipeRevealLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            container = itemView.findViewById(R.id.charity_item);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvName = itemView.findViewById(R.id.tv_author_name);
            tvGoal = itemView.findViewById(R.id.tv_goal);
            tvPercent = itemView.findViewById(R.id.tv_percent);
            tvReport = itemView.findViewById(R.id.tv_report);
        }
    }
}