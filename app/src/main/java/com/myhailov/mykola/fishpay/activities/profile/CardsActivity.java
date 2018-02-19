package com.myhailov.mykola.fishpay.activities.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.TokenStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CardsActivity extends BaseActivity {

    private TextView tvNameError;
    private EditText etCardName;

    private TextView tvNumberError;
    private EditText etCardNumber;

    private TextView tvAddCard;

    private RecyclerView rvCards;
    private ProgressBar progressBar;

    private View llWithoutCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);


        initViews();
        getCards();
    }

    private void initViews() {
        initToolbar();
        tvNameError = findViewById(R.id.tv_name_error);
        etCardName = findViewById(R.id.et_card_name);
        tvNumberError = findViewById(R.id.tv_number_error);
        etCardNumber = findViewById(R.id.et_card_number);
        tvAddCard  = findViewById(R.id.tv_add_card);
        progressBar = findViewById(R.id.progress_bar);
        rvCards = findViewById(R.id.rv_cards);
        llWithoutCard = findViewById(R.id.ll_without_card);

        tvAddCard.setOnClickListener(this);

        rvCards.setHasFixedSize(true);
        rvCards.setLayoutManager(new LinearLayoutManager(context));
    }

    private void initToolbar() {
        ((TextView) findViewById(R.id.tvToolBarTitle)).setText(getResources().getString(R.string.public_card));
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private void getCards() {
        rvCards.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiClient().getCards(TokenStorage.getToken(context))
                .enqueue(new BaseCallback<ArrayList<Card>>(context, false) {
                    @Override
                    protected void onResult(int code, ArrayList<Card> result) {
                        if (code == 200) {
                            progressBar.setVisibility(View.GONE);
                            rvCards.setVisibility(View.VISIBLE);
                            rvCards.setAdapter(new CardsAdapter(context, result));
                        }
                    }

                    @Override
                    protected void onError(int code, String errorDescription) {
                        if (code == 404) {
                            progressBar.setVisibility(View.GONE);
                            rvCards.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void addCard(String name, String number) {
        ApiClient.getApiClient().createCard(TokenStorage.getToken(context), name, number)
                .enqueue(new BaseCallback<Object>(context, false) {
                    @Override
                    protected void onResult(int code, Object result) {
                        if (code == 201) {
                            etCardName.getText().clear();
                            etCardNumber.getText().clear();
                            getCards();
                        }
                    }
                });
    }

    private void setPublicCard(String cardNumber) {
        ApiClient.getApiClient().setPublicCard(TokenStorage.getToken(context), cardNumber)
                .enqueue(new BaseCallback<Object>(context, false) {
                    @Override
                    protected void onResult(int code, Object result) {
                        if (code == 200) {
                            getCards();
                        }
                    }
                });
    }

    private boolean isDataInvalid() {
        boolean isNameValid = etCardName.getText().toString().length() >= 4;
        boolean isNumberValid = etCardNumber.getText().toString().length() == 16;
        tvNameError.setVisibility(isNameValid ? View.GONE : View.VISIBLE);
        tvNumberError.setVisibility(isNumberValid ? View.GONE : View.VISIBLE);
        return isNameValid && isNumberValid;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof Card) {
            setPublicCard(((Card) view.getTag()).getCardNumber());
        }
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_add_card:
                if (isDataInvalid()) {
                    addCard(etCardName.getText().toString(), etCardNumber.getText().toString());
                }
                break;
        }
    }

    private class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

        private Context context;
        private ArrayList<Card> cards;

        CardsAdapter(Context context, ArrayList<Card> cards) {
            this.context = context;
            this.cards = cards;
            Collections.sort(this.cards);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_card, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(cards.get(position));
        }

        @Override
        public int getItemCount() {
            return cards.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvName, tvNumber;
            private View ivCheck;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_card_name);
                tvNumber = itemView.findViewById(R.id.tv_card_number);
                ivCheck = itemView.findViewById(R.id.iv_check);
            }

            void bind(Card card) {
                tvName.setText(card.getName());
                tvNumber.setText("**** "+card.getCardNumber().substring(12, 16));
                itemView.setTag(card);
                itemView.setOnClickListener((View.OnClickListener) context);
                ivCheck.setVisibility(card.getType().equals("PUBLIC") ? View.VISIBLE : View.GONE);
            }
        }
    }
}
