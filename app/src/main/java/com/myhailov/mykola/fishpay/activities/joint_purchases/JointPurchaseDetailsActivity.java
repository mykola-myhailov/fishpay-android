package com.myhailov.mykola.fishpay.activities.joint_purchases;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.activities.TransactionActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.requestBodies.Member;
import com.myhailov.mykola.fishpay.api.results.JointPurchase;
import com.myhailov.mykola.fishpay.api.results.JointPurchaseDetailsResult;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.myhailov.mykola.fishpay.utils.Keys.PURCHASE;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.ID;
import static com.myhailov.mykola.fishpay.utils.PrefKeys.USER_PREFS;
import static com.myhailov.mykola.fishpay.utils.Utils.pennyToUah;

public class JointPurchaseDetailsActivity extends BaseActivity {

    private final String STATE_PURCHASE = "state_purchase";

    private JointPurchaseDetailsResult purchaseDetails;
    private String id;
    private boolean isOwner;
    private boolean isClosed;
    private String title;
    private View llDescription;

    private TextView tvDescription;
    private View llCard;

    private TextView tvCardNumber;

    private TextView tvAmount;

    private View llClose, llPay, llConfirmation;
    private View tvClose, tvPay, tvReject, tvAccept;

    private View llClosed;
    private JointPurchase extraPurchase;

    private AlertDialog alertClose;
    private String closeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchase_details);
        if (savedInstanceState == null) {
            id = getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(ID, "");
            extraPurchase = getIntent().getParcelableExtra(PURCHASE);
        } else {
            extraPurchase = savedInstanceState.getParcelable(STATE_PURCHASE);
        }

        isOwner = id.equals(extraPurchase.getCreatorId());
        title = extraPurchase.getTitle();

        initCustomToolbar(title);
        initViews();
        getJointPurchase(extraPurchase.getId());


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_PURCHASE, extraPurchase);
    }

    private void initViews() {
        llDescription = findViewById(R.id.ll_description);
        tvDescription = findViewById(R.id.tv_description);
        llCard = findViewById(R.id.ll_card);
        tvCardNumber = findViewById(R.id.tv_card_number);
        tvAmount = findViewById(R.id.tv_amount);

        llClose = findViewById(R.id.ll_close);
        llPay = findViewById(R.id.ll_pay);
        llConfirmation = findViewById(R.id.ll_confirmation);
        tvClose = findViewById(R.id.tv_close);
        tvPay = findViewById(R.id.tv_pay);
        tvReject = findViewById(R.id.tv_reject);
        tvAccept = findViewById(R.id.tv_accept);

        llClosed = findViewById(R.id.ll_closed);
    }

    private void getJointPurchase(String id) {
        ApiClient.getApiInterface().getJointPurchaseDetails(TokenStorage.getToken(context), id)
                .enqueue(new BaseCallback<JointPurchaseDetailsResult>(context, false) {
                    @Override
                    protected void onResult(int code, JointPurchaseDetailsResult result) {
                        hasResponse(result);
                    }
                });
    }

    private void acceptPurchase(final String id) {
        tvReject.setClickable(false);
        tvAccept.setClickable(false);
        ApiClient.getApiInterface().acceptJointPurchase(TokenStorage.getToken(context), id)
                .enqueue(getConfirmationCallback(id, true));
    }

    private void rejectPurchase(final String id) {
        tvReject.setClickable(false);
        tvAccept.setClickable(false);
        ApiClient.getApiInterface().rejectJointPurchase(TokenStorage.getToken(context), id)
                .enqueue(getConfirmationCallback(id, false));
    }

    private void closePurchase(final String id) {
        tvClose.setClickable(false);
        ApiClient.getApiInterface().closeJointPurchase(TokenStorage.getToken(context), id)
                .enqueue(new BaseCallback<Object>(context, false) {
                    @Override
                    protected void onResult(int code, Object result) {
                        if (code == 202) {
                            setResult(RESULT_OK);
                            finish();
                        } else tvClose.setClickable(true);
                    }

                    @Override
                    protected void onError(int code, String errorDescription) {
                        tvClose.setClickable(true);
                    }

                    @Override
                    public void onFailure(@NonNull Call<BaseResponse<Object>> call, @NonNull Throwable t) {
                        super.onFailure(call, t);
                        tvClose.setClickable(true);
                    }
                });
    }

    @NonNull
    private BaseCallback<Object> getConfirmationCallback(final String id, final boolean accept) {
        return new BaseCallback<Object>(context, false) {
            @Override
            protected void onResult(int code, Object result) {
                if (code == 202) {
                    setResult(RESULT_OK);
                    if (accept) getJointPurchase(id);
                    else finish();
                } else {
                    tvReject.setClickable(true);
                    tvAccept.setClickable(true);
                }
            }

            @Override
            protected void onError(int code, String errorDescription) {
                super.onError(code, errorDescription);
                tvReject.setClickable(false);
                tvAccept.setClickable(false);
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<Object>> call, @NonNull Throwable t) {
                super.onFailure(call, t);
                tvReject.setClickable(false);
                tvAccept.setClickable(false);
            }
        };
    }

    private void hasResponse(JointPurchaseDetailsResult response) {
        purchaseDetails = response;
        if (purchaseDetails.getDescription() != null) {
            llDescription.setVisibility(VISIBLE);
            tvDescription.setText(purchaseDetails.getDescription());
        } else llDescription.setVisibility(View.GONE);

        // card

        if (purchaseDetails.getPanMasked() != null) {
            llCard.setVisibility(VISIBLE);
            String cardNumber = "**** " + purchaseDetails.getPanMasked().substring(purchaseDetails.getPanMasked().length() - 4, purchaseDetails.getPanMasked().length());
            tvCardNumber.setText(cardNumber);
        } else llCard.setVisibility(View.GONE);

        tvAmount.setText(pennyToUah((purchaseDetails.getAmount())));

        if (purchaseDetails.getMembers() != null && purchaseDetails.getMembers().size() != 0) {
            MembersAdapter membersAdapter = new MembersAdapter(context, purchaseDetails.getMembers());
            RecyclerView rvMembers = findViewById(R.id.rv_members);
            rvMembers.setLayoutManager(new LinearLayoutManager(context));
            rvMembers.setHasFixedSize(true);
            rvMembers.setAdapter(membersAdapter);

            isClosed = purchaseDetails.getMembers().get(0)._getMemberStatus().equals("CLOSED");
            if (!isClosed) {
                llClosed.setVisibility(GONE);
                if (isOwner) {
                    llClose.setVisibility(VISIBLE);
                    llPay.setVisibility(View.GONE);
                    llConfirmation.setVisibility(View.GONE);

                    tvClose.setOnClickListener(this);
                    tvClose.setTag(purchaseDetails.getId());
                } else {
                    llClose.setVisibility(View.GONE);
                    Member memberI = getMemberById(purchaseDetails.getMembers(), id);
                    if (memberI != null) {
                        switch (memberI._getMemberStatus()) {
                            case "VIEWED":
                            case "NOT_VIEWED":
                                llPay.setVisibility(View.GONE);
                                llConfirmation.setVisibility(VISIBLE);

                                tvAccept.setOnClickListener(this);
                                tvReject.setOnClickListener(this);
                                tvAccept.setTag(purchaseDetails.getId());
                                tvReject.setTag(purchaseDetails.getId());
                                break;
                            case "ACCEPTED":
                                llPay.setVisibility(VISIBLE);
                                llConfirmation.setVisibility(View.GONE);

                                tvPay.setOnClickListener(this);
                                break;
                            default:
                                llPay.setVisibility(View.GONE);
                                llConfirmation.setVisibility(View.GONE);
                                break;
                        }
                    }
                }
            } else {
                tvAmount.setTextColor(getResources().getColor(R.color.grey2));
                llClosed.setVisibility(VISIBLE);
                llClose.setVisibility(View.GONE);
                llPay.setVisibility(View.GONE);
                llConfirmation.setVisibility(View.GONE);
            }
        }

    }

    @Nullable
    private Member getMemberById(ArrayList<Member> members, String id) {
        for (Member member : members) {
            if (member.getUserId().equals(id)) return member;
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ll_member:
                startMemberActivity(((Member) view.getTag()));
                break;
            case R.id.tv_close:
                closeId = (String) view.getTag();
                showConfirmation();
                break;
            case R.id.tv_reject:
                rejectPurchase((String) view.getTag());
                break;
            case R.id.tv_accept:
                acceptPurchase((String) view.getTag());
                break;
            case R.id.tv_pay:
                context.startActivity(new Intent(context, TransactionActivity.class)
                        .putExtra(Keys.TYPE, TransactionActivity.JOINT_PURCHASE)
                        .putExtra(Keys.PURCHASE, purchaseDetails)
                );
                break;
            case R.id.tv_first_action:
                alertClose.cancel();
                break;
            case R.id.tv_second_action:
                closePurchase(closeId);
                alertClose.cancel();
                break;
        }

    }

    private void showConfirmation() {

        TextView tvCancel, tvClose, tvDescription, tvTitle;

        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_with_two_action, null);
        dialogBuilder.setView(dialogView);
        tvCancel = dialogView.findViewById(R.id.tv_first_action);
        tvClose = dialogView.findViewById(R.id.tv_second_action);
        tvDescription = dialogView.findViewById(R.id.tv_description);
        tvTitle = dialogView.findViewById(R.id.tv_title);

        tvClose.setText(getString(R.string.ok));
        tvCancel.setText(getString(R.string.cancel));
        tvTitle.setText(getString(R.string.alert_close_purchase_title));
        tvDescription.setText(getString(R.string.alert_close_purchase_description));
        tvCancel.setOnClickListener(this);
        tvClose.setOnClickListener(this);

        alertClose = dialogBuilder.create();
        alertClose.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertClose.show();
    }

    private void startMemberActivity(Member member) {
        if (member != null) {
            startActivityForResult(new Intent(context, MembersPartActivity.class)
                    .putExtra(Keys.MEMBER, member)
                    .putExtra(Keys.TITLE, title)
                    .putExtra(Keys.OWNER, isOwner), 100);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            getJointPurchase(extraPurchase.getId());
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
                tvName.setText(member.getFullUserName());
                tvAmount.setText(pennyToUah(member.getAmountToPay()));

                if (member._getMemberStatus().equals("CLOSED")) {
                    tvName.setTextColor(getResources().getColor(R.color.grey2));
                    tvStatus.setTextColor(getResources().getColor(R.color.grey2));
                    tvAmount.setTextColor(getResources().getColor(R.color.grey2));
                }

                llMember.setTag(member);
            }
        }
    }
}
