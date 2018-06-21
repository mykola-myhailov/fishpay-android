package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.utils.Utils;

import org.w3c.dom.Text;

import java.util.List;

public class GoodsSelectAdapter extends RecyclerView.Adapter<GoodsSelectAdapter.ViewHolder> {

    private static final String TAG = GoodsSelectAdapter.class.getSimpleName();
    private Context context;
    private List<GoodsResults> list;
    private OnItemClickListener listener;

    public GoodsSelectAdapter(Context context, List<GoodsResults> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_goods_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GoodsResults item = list.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvPrice.setText(Utils.pennyToUah(item.getPrice()));
        if (item.getCount() != 0) {
            holder.tvCount.setVisibility(View.VISIBLE);
            holder.tvCount.setText(item.getCount() + "");
        }else {
            holder.tvCount.setVisibility(View.INVISIBLE);
        }
        Utils.displayGoods(context, holder.ivPhoto, item.getMainPhoto(), item.getId());

        holder.ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(false, position, holder.tvCount);
            }
        });
        holder.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(true, position, holder.tvCount);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(boolean isPlus, int position, TextView textView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPrice, tvTitle, tvCount;
        private ImageView ivPhoto, ivPlus, ivMinus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCount = itemView.findViewById(R.id.tv_count_goods);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            ivPlus = itemView.findViewById(R.id.iv_plus);
            ivMinus = itemView.findViewById(R.id.iv_minus);
        }
    }
}