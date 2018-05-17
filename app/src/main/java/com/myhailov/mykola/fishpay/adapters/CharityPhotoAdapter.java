package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.myhailov.mykola.fishpay.R;

import java.io.File;
import java.util.List;

public class CharityPhotoAdapter extends RecyclerView.Adapter<CharityPhotoAdapter.ViewHolder> {

    private static final String TAG = CharityPhotoAdapter.class.getSimpleName();
    private Context context;
    private List<String> list;
    private OnItemClickListener listener;

    public CharityPhotoAdapter(Context context, List<String> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String item = list.get(position);
        File imgFile = new File(item);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.photo.setImageBitmap(myBitmap);
        }


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listener.onItemClick();
            }
        });
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