package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.GoodsResults;
import com.myhailov.mykola.fishpay.api.results.InvoiceDetailsResult;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.List;

public class GoodsOutIncomAdapter extends RecyclerView.Adapter<GoodsOutIncomAdapter.ViewHolder> {

    private static final String TAG = GoodsOutIncomAdapter.class.getSimpleName();
    private Context context;
    private List<InvoiceDetailsResult.GoodsRequest> list;
    private OnItemClickListener listener;

    public GoodsOutIncomAdapter(Context context, List<InvoiceDetailsResult.GoodsRequest> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_goods_out_incom_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final InvoiceDetailsResult.GoodsRequest item = list.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvPrice.setText(Utils.pennyToUah(item.getPrice()));
        holder.tvCount.setText(context.getString(R.string.goods_count, item.getCount()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(long id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCount, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}