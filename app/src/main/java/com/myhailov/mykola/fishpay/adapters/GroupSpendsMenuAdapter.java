package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.GroupSpend;

import java.util.List;

public class GroupSpendsMenuAdapter extends RecyclerView.Adapter<GroupSpendsMenuAdapter.ViewHolder> {

    private static final String TAG = GroupSpendsMenuAdapter.class.getSimpleName();
    private Context context;
    private List<GroupSpend> list;
    private OnItemClickListener listener;

    public GroupSpendsMenuAdapter(Context context, List<GroupSpend> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_group_spend_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GroupSpend item = list.get(position);

       holder.tvTitle.setText(item.getTitle());
       holder.tvAuthor.setText(item.getCreatorName() + " " + item.getCreatorSurname());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(GroupSpend item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
        }
    }
}