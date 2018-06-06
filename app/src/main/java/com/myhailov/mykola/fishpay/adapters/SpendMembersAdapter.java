package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Mykola Myhailov  on 01.06.18.
 */
public class SpendMembersAdapter extends RecyclerView.Adapter<SpendMembersAdapter.MemberHolder>{

    private Context context;
    private ArrayList<MemberDetails> members;
    private final static String ROLE_MEMBER = "member", ROLE_CREATOR = "creator";

    public SpendMembersAdapter(Context context, ArrayList<MemberDetails> members) {
        this.context = context;
        this.members = members;
    }

    class MemberHolder extends RecyclerView.ViewHolder{

        private View rlMemberItem;
        private ImageView ivAvatar;
        private TextView tvName, tvRole, tvShare, tvTotalCost, tvAmount;

        MemberHolder(View itemView) {
            super(itemView);
            rlMemberItem = itemView;
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvShare = itemView.findViewById(R.id.tvShare);
            tvTotalCost = itemView.findViewById(R.id.tvTotalCost);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }

    @Override
    public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spend_members_item, parent, false);
        return new MemberHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemberHolder holder, int position){
        MemberDetails member = members.get(position);
        if (member == null) return;
        String name = member.getName() + " " + member.getSurname();
        holder.tvName.setText(name);
        holder.tvAmount.setText(Utils.pennyToUah(member.getOverpaiment()));
        holder.tvTotalCost.setText(Utils.pennyToUah(member.getSum()));
        holder.tvShare.setText(Utils.toPercentage(member.getPart()));
        String initials = Utils.extractInitials(member.getName(), member.getSurname());
        Utils.displayAvatar(context, holder.ivAvatar, member.getPhoto(), initials);
        String role = member.getRole();
        switch (role.toLowerCase()){
            case ROLE_CREATOR:
                holder.tvRole.setText(R.string.creator);
                break;
            case ROLE_MEMBER:
                holder.tvRole.setText(R.string.member);
                break;
            default:
                holder.tvRole.setText(R.string.no_account);
                break;
        }
        holder.rlMemberItem.setTag(member);
        holder.rlMemberItem.setOnClickListener((View.OnClickListener) context);
    }

    @Override
    public int getItemCount() {
        if (members == null) return 0;
        return members.size();
    }


}
