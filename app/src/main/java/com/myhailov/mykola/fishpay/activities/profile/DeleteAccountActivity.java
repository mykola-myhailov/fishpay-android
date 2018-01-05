package com.myhailov.mykola.fishpay.activities.profile;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.models.RemoveReason;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

public class DeleteAccountActivity extends BaseActivity {

    private ArrayList<RemoveReason> reasons;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private View.OnClickListener onClickListener = this;
    private EditText etComment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "Удаление аккаунта";
        ((TextView) findViewById(R.id.tvToolBarTitle)).setText(title.toUpperCase());

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

        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
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
                reasonItemLayout = itemView;
            }
        }

        class FooterHolder extends RecyclerView.ViewHolder{
            FooterHolder(View itemView) {
                super(itemView);
                etComment = findViewById(R.id.etComment);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvDescription:
                RemoveReason reason = (RemoveReason) view.getTag();

                break;
            case R.id.etComment:
                break;
        }
    }

}
