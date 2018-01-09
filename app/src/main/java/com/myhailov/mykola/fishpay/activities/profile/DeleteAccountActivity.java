package com.myhailov.mykola.fishpay.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.models.RemoveAccResult;
import com.myhailov.mykola.fishpay.api.models.RemoveReason;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;


public class DeleteAccountActivity extends BaseActivity {

    private ArrayList<RemoveReason> reasons;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private View.OnClickListener onClickListener = this;
    private String comment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "Удаление аккаунта";
        ((TextView) findViewById(R.id.tvToolBarTitle)).setText(title.toUpperCase());
        findViewById(R.id.vDelete).setOnClickListener(this);

        getReasonsRequest();
    }

    private void getReasonsRequest() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient()
                    .removeAccReasons(TokenStorage.getToken(context))
                    .enqueue(new BaseCallback<ArrayList<RemoveReason>>(context, true) {
                        @Override
                        protected void onResult(int code, ArrayList<RemoveReason> result) {
                            reasons = result;
                            initRecyclerView();
                        }
                    });

        } else Utils.noInternetToast(context);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ReasonAdapter adapter = new ReasonAdapter();
        recyclerView.setAdapter(adapter);

    }

    private class ReasonAdapter extends RecyclerView.Adapter{

        private static final int TYPE_FOOTER = 0;
        private static final int TYPE_ITEM = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView;
            if (viewType == TYPE_ITEM){
                itemView =  inflater.inflate(R.layout.rexason_item, parent, false);
                return new ReasonHolder(itemView);
            }
            else {
                itemView =  inflater.inflate(R.layout.reasons_footer, parent, false);
                return new FooterHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == TYPE_ITEM){
                RemoveReason reason = reasons.get(position);
                ReasonHolder reasonHolder = (ReasonHolder) holder;
                reasonHolder.tvDescription.setText(reason.getDescription());
                reasonHolder.reasonItemLayout.setTag(reason);
                reasonHolder.reasonItemLayout.setOnClickListener(onClickListener);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == reasons.size())
                return TYPE_FOOTER;
            return TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return reasons.size()+1;
        }

        class ReasonHolder extends RecyclerView.ViewHolder{

            TextView tvDescription;
            ImageView ivCheckMark;
            View reasonItemLayout;

            ReasonHolder(View itemView) {
                super(itemView);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                ivCheckMark = itemView.findViewById(R.id.ivCheckMark);
                reasonItemLayout = itemView.findViewById(R.id.vReasonItemLayout);
            }
        }

        class FooterHolder extends RecyclerView.ViewHolder{
            FooterHolder(View itemView) {
                super(itemView);
                final EditText etComment = itemView.findViewById(R.id.etComment);
                etComment.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        comment = etComment.getText().toString();
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.vReasonItemLayout:
                RemoveReason reason = (RemoveReason) view.getTag();
                ImageView ivCheckMark = view.findViewById(R.id.ivCheckMark);
                if(reason.isChecked()) {
                    ivCheckMark.setVisibility(View.GONE);
                    reason.setChecked(false);

                }
                else {
                    ivCheckMark.setVisibility(View.VISIBLE);
                    reason.setChecked(true);
                }
                view.setTag(reason);
                break;
            case R.id.vDelete:
                ArrayList<ReasonInRequestBody> selectedReasonInRequestBodies = new ArrayList<>();

                for (RemoveReason removeReason :
                        reasons) {

                    if (removeReason.isChecked())
                        selectedReasonInRequestBodies.add(new ReasonInRequestBody(removeReason.getKey(), removeReason.getId()));
                }
                if (selectedReasonInRequestBodies.size() == 0) Utils.toast(context, "Укажите хотя бы одну причину");
                else {
                    RemoveBody removeBody = new RemoveBody(comment, selectedReasonInRequestBodies);
                    deleteRequest(removeBody);
                }
                break;

        }
    }

    private void deleteRequest(RemoveBody removeBody) {
        if (Utils.isOnline(context)){
            ApiClient.getApiClient()
                    .removeAccount(TokenStorage.getToken(context), removeBody)
                    .enqueue(new BaseCallback<RemoveAccResult>(context, true) {
                        @Override
                        protected void onResult(int code, RemoveAccResult result) {
                            Intent intent = new Intent(context, DelAccConfirmActivity.class);
                            intent.putExtra(Keys.REQUEST_ID, result.getRequestId());
                            context.startActivity(intent);
                        }
                    });
        }
    }

    public class RemoveBody {

        RemoveBody(String comment, ArrayList<ReasonInRequestBody> reasonInRequestBodies) {
            this.comment = comment;
            this.reasonInRequestBodies = reasonInRequestBodies;
        }

        @SerializedName("user_comment")
                private String comment;

        @SerializedName("reasone")
                private ArrayList<ReasonInRequestBody> reasonInRequestBodies;



         }

    class ReasonInRequestBody {

        public ReasonInRequestBody(String key, String id) {
            this.key = key;
            this.id = id;
        }

        @SerializedName("key")
        private String key;

        @SerializedName("reasone_id")
        private String id;
    }
}
