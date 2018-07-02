package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.GoodsByIdResult;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EditPhotoAdapter extends RecyclerView.Adapter<EditPhotoAdapter.ViewHolder> {

    private static final String TAG = PhotoAdapter.class.getSimpleName();
    private Context context;
    private List<GoodsByIdResult.Photo> list;
    private OnItemClickListener listener;
    private Picasso picasso;

    public EditPhotoAdapter(Context context, List<GoodsByIdResult.Photo> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
        picasso = new Picasso.Builder(context).build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GoodsByIdResult.Photo item = list.get(position);
        holder.delete.setVisibility(View.GONE);
        Utils.displayGoods(picasso, holder.photo, item.getPhotoUrl(), item.getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;
        private ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.iv_photo);
            delete = itemView.findViewById(R.id.iv_delete);
        }
    }
}