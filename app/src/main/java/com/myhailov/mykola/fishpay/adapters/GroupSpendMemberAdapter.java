package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.List;

public class GroupSpendMemberAdapter extends RecyclerView.Adapter<GroupSpendMemberAdapter.ViewHolder> {

    private static final String TAG = GroupSpendMemberAdapter.class.getSimpleName();
    private Context context;
    private List<MemberDetails> list;
    private OnItemClickListener listener;

    public GroupSpendMemberAdapter(Context context, List<MemberDetails> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MemberDetails item = list.get(position);
        String name = item.getName() + " " + item.getSurname();
        holder.tvName.setText(name);
        String photo = item.getPhoto();
        String initials = Utils.extractInitials(name, "");

        if (TextUtils.isEmpty(photo)){
            holder.tvInitials.setText(initials);
            holder.tvInitials.setBackground(context.getResources().getDrawable(R.drawable.contact_grey_rounded));
        }else {
            holder.tvInitials.setText("");
            holder.tvInitials.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            Utils.displayAvatar(context, holder.ivAvatar, photo, initials);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
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
        void onItemClick(MemberDetails details);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeRevealLayout swipeRevealLayout;
        private ImageView ivAvatar, ivInvite;
        private TextView tvName, tvInitials;
        private View container;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            swipeRevealLayout.setLockDrag(true);
            tvName = itemView.findViewById(R.id.tvName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivInvite = itemView.findViewById(R.id.ivInvite);
            container = itemView.findViewById(R.id.container);
            tvInitials = itemView.findViewById(R.id.tvInitials);
        }
    }
}