package com.myhailov.mykola.fishpay.activities.joint_purchases;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.api.results.JointPurchase;
import com.myhailov.mykola.fishpay.api.results.JointPurchaseDetailsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.myhailov.mykola.fishpay.utils.Keys.CONTACT;
import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.ID;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.USER_PREFS;
import static com.myhailov.mykola.fishpay.utils.Utils.pennyToUah;

public class JointPurchaseDetailsActivity extends BaseActivity {

    private JointPurchaseDetailsResult purchase;
    private boolean isOwner;
    private String title;
    private View llDescription;

    private TextView tvDescription;
    private View llCard;

    private TextView tvCardNumber;

    private TextView tvAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchase_details);
        String id = getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(ID, "");
        JointPurchase purchase = getIntent().getParcelableExtra(PURCHASE);
        isOwner = id.equals(purchase.getCreatorId());
        title = purchase.getTitle();

        initCustomToolbar(title);
        initViews();
        getJointPurchase(purchase.getId());


    }

    private void initViews() {
        llDescription = findViewById(R.id.ll_description);
        tvDescription = findViewById(R.id.tv_description);
        llCard = findViewById(R.id.ll_card);
        tvCardNumber = findViewById(R.id.tv_card_number);
        tvAmount = findViewById(R.id.tv_amount);

        View llClose = findViewById(R.id.ll_close);
        llClose.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        llClose.setOnClickListener(this);
    }

    private void getJointPurchase(String id) {
        ApiClient.getApiClient().getJointPurchaseDetails(TokenStorage.getToken(context), id)
                .enqueue(new BaseCallback<JointPurchaseDetailsResult>(context, false) {
                    @Override
                    protected void onResult(int code, JointPurchaseDetailsResult result) {
                        hasResponse(result);
                    }
                });
    }

    private void hasResponse(JointPurchaseDetailsResult response) {
        purchase = response;
        if (purchase.getDescription() != null) {
            llDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(purchase.getDescription());
        } else llDescription.setVisibility(View.GONE);
        if (purchase.getCardNumber() != null) {
            llCard.setVisibility(View.VISIBLE);
            tvCardNumber.setText(purchase.getLastFourNumbers());
        } else llCard.setVisibility(View.GONE);

        tvAmount.setText(pennyToUah(((float) purchase.getAmount())));

        MembersAdapter membersAdapter = new MembersAdapter(context, purchase.getMembers());
        RecyclerView rvMembers = findViewById(R.id.rv_members);
        rvMembers.setLayoutManager(new LinearLayoutManager(context));
        rvMembers.setHasFixedSize(true);
        rvMembers.setAdapter(membersAdapter);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.ll_member:
                startMemberActivity(((Member) view.getTag()));
                break;
            case R.id.ll_close:
                showConfirmation();
                break;
        }

    }

    private void showConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Вы уверенны, что хотите закрыть эту покупку?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                closePurchaseRequest();
//                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }

    private void startMemberActivity(Member member) {
        if (member != null) {
            startActivity(new Intent(context, MembersPartActivity.class)
                    .putExtra(Keys.MEMBER, member)
                    .putExtra(Keys.TITLE, title)
                    .putExtra(Keys.OWNER, isOwner));
        }
    }

    class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

        private Context context;
        private ArrayList<Member> members;

        MembersAdapter(Context context, ArrayList<Member> members) {
            this.context = context;
            this.members = members;
        }

        public void setMembers(ArrayList<Member> members) {
            this.members = members;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_member, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(members.get(position));
        }

        @Override
        public int getItemCount() {
            if (members != null) return members.size();
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View llMember;
            ImageView ivAvatar;
            TextView tvInitials;
            TextView tvName;
            TextView tvStatus;
            TextView tvAmount;

            ViewHolder(View itemView) {
                super(itemView);
                llMember = itemView.findViewById(R.id.ll_member);
                ivAvatar = itemView.findViewById(R.id.iv_avatar);
                tvInitials = itemView.findViewById(R.id.tv_initials);
                tvName = itemView.findViewById(R.id.tv_name);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvAmount = itemView.findViewById(R.id.tv_amount);

                llMember.setOnClickListener((View.OnClickListener) context);
            }

            void bind(Member member) {
                String initials = Utils.extractInitials(member.getFirstName(), member.getSecondName());
                if (member.getPhoto() != null && !member.getPhoto().equals("") && !member.getPhoto().equals("null")) {
                    Uri photoUri = Uri.parse(ApiClient.BASE_API_URL + "api/resources/photo/" + member.getPhoto());
                    Picasso.with(context).load(photoUri).into(ivAvatar);
                    tvInitials.setText("");
                } else {
                    ivAvatar.setImageDrawable(null);
                    tvInitials.setText(initials);
                }
                tvStatus.setText(member.getMemberStatus());
                tvName.setText(member.getFullName());
                tvAmount.setText(pennyToUah(Float.valueOf(member.getAmountToPay())));

                llMember.setTag(member);
            }
        }
    }
}
