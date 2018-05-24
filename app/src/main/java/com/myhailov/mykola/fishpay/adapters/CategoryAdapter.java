package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.CategoryResult;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private static final String TAG = CategoryAdapter.class.getSimpleName();
    private Context context;
    private List<CategoryResult> list;
    private OnItemClickListener listener;

    public CategoryAdapter(Context context, List<CategoryResult> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CategoryResult item = list.get(position);

        holder.ctvCategory.setText(item.getCategory());
        holder.ctvCategory.setChecked(item.isChecked());

        holder.ctvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick((CheckedTextView) v, item.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CheckedTextView ctv, int id,int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckedTextView ctvCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            ctvCategory = itemView.findViewById(R.id.ctv_category);
        }
    }
}