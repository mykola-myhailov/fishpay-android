package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.CharityResult.CharityProgram;
import com.myhailov.mykola.fishpay.utils.Utils;

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

        if (item.getStatus().equals("CLOSED")){
            setClosed(holder);
        }else setActive(holder);

        holder.tvTitle.setText(item.getTitle());
        if (TextUtils.isEmpty(item.getPseudonym())) {
            holder.tvName.setText(item.getAuthorName());
        }else {
            holder.tvName.setText(item.getPseudonym());
        }
        if (item.getRequiredAmount() != 0) {

            holder.tvPercent.setText(item.getExecution() + "");
            holder.tvPercent.setVisibility(View.VISIBLE);
            holder.tvPercentChar.setVisibility(View.VISIBLE);
        } else {
            holder.tvPercent.setVisibility(View.INVISIBLE);
            holder.tvPercentChar.setVisibility(View.INVISIBLE);
        }
        holder.tvGoal.setText(Utils.pennyToUah(item.getTotalAmount()));

        holder.tvReport.setOnClickListener((View.OnClickListener) context);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item.getId().toString(), item);
            }
        });

        // TODO: 12.07.2018 charity report
        holder.swipeRevealLayout.setLockDrag(true);
        holder.swipeRevealLayout.close(false);
//        viewBinderHelper.bind(holder.swipeRevealLayout, item.getId().toString());

    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    private void setClosed(ViewHolder holder){
        holder.tvName.setTextColor(context.getResources().getColor(R.color.grey_disabled));
        holder.tvTitle.setTextColor(context.getResources().getColor(R.color.grey_disabled));
        holder.tvGoal.setTextColor(context.getResources().getColor(R.color.grey_disabled));
        holder.tvPercent.setTextColor(context.getResources().getColor(R.color.grey_disabled));
        holder.tvPercentChar.setTextColor(context.getResources().getColor(R.color.grey_disabled));
        holder.tvCurency.setTextColor(context.getResources().getColor(R.color.grey_disabled));
        holder.tvName.setAlpha(0.50f);
        holder.tvTitle.setAlpha(0.50f);
        holder.tvGoal.setAlpha(0.50f);
        holder.tvPercent.setAlpha(0.50f);
        holder.tvPercentChar.setAlpha(0.50f);
        holder.tvCurency.setAlpha(0.50f);
    }

    private void setActive(ViewHolder holder){
        holder.tvName.setTextColor(context.getResources().getColor(R.color.grey_text_secondary));
        holder.tvTitle.setTextColor(context.getResources().getColor(R.color.black_text));
        holder.tvGoal.setTextColor(context.getResources().getColor(R.color.grey_text_secondary));
        holder.tvPercent.setTextColor(context.getResources().getColor(R.color.black_text));
        holder.tvPercentChar.setTextColor(context.getResources().getColor(R.color.black_text));
        holder.tvCurency.setTextColor(context.getResources().getColor(R.color.grey_text_secondary));
        holder.tvName.setAlpha(1);
        holder.tvTitle.setAlpha(1);
        holder.tvGoal.setAlpha(1);
        holder.tvPercent.setAlpha(1);
        holder.tvPercentChar.setAlpha(1);
        holder.tvCurency.setAlpha(1);
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
        private TextView tvPercentChar;
        private TextView tvReport;
        private TextView tvCurency;
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
            tvPercentChar = itemView.findViewById(R.id.textView23);
            tvCurency = itemView.findViewById(R.id.textView25);
        }
    }
}