package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.CharityResult;

import java.util.List;

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.ViewHolder> {
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    private static final String TAG = CharityAdapter.class.getSimpleName();
    private Context context;
    private List<CharityResult.CharityProgram> list;

    public CharityAdapter(Context context, List<CharityResult.CharityProgram> list) {
        this.context = context;
        this.list = list;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_charity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CharityResult.CharityProgram item = list.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvName.setText(item.getAuthorName());
        holder.tvPercent.setText(item.getExecution() + "%");
        holder.tvGoal.setText(item.getTotalAmount().toString() + " |грн");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }else {
            return 0;
        }
    }

    public void setList(List<CharityResult.CharityProgram> list){
        this.list = list;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvName;
        private TextView tvGoal;
        private TextView tvPercent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvName = itemView.findViewById(R.id.tv_author_name);
            tvGoal = itemView.findViewById(R.id.tv_goal);
            tvPercent = itemView.findViewById(R.id.tv_percent);
        }
    }
}