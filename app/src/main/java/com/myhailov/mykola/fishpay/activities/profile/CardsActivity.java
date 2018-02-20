package com.myhailov.mykola.fishpay.activities.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.internal.Util;
import tw.henrychuang.lib.AutoAddTextWatcher;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class CardsActivity extends BaseActivity {

    private TextView tvNameError;
    private EditText etCardName;

    private TextView tvNumberError;
    private EditText etCardNumber;

    private RecyclerView rvCards;
    private ProgressBar progressBar;

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
        final View tvAddCard = findViewById(R.id.tv_add_card);
        tvAddCard.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_bar);
        rvCards = findViewById(R.id.rv_cards);
        findViewById(R.id.ll_without_card).setOnClickListener(this);

        etCardNumber.addTextChangedListener(new AutoAddTextWatcher(etCardNumber, " ", 4, 8, 12));
        etCardNumber.setImeOptions(IME_ACTION_DONE);
        final InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        etCardNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == IME_ACTION_DONE) {
                    tvAddCard.performClick();
                    if (imm != null)
                        imm.toggleSoftInputFromWindow(etCardNumber.getWindowToken(), 0, 0);
                    return true;
                }
                return false;
            }
        });
        rvCards.setHasFixedSize(true);
        rvCards.setLayoutManager(new LinearLayoutManager(context));
    }

    private void initToolbar() {
        ((TextView) findViewById(R.id.tvToolBarTitle)).setText(getResources().getString(R.string.public_card));
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    // requests
    private void getCards() {
        if (Utils.isOnline(context)) {
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
        } else Utils.noInternetToast(context);
    }

    private void addCard(String name, String number) {
        if (Utils.isOnline(context)) {
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
        } else Utils.noInternetToast(context);
    }

    private void setPublicCard(String cardNumber) {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().setPublicCard(TokenStorage.getToken(context), cardNumber)
                    .enqueue(new BaseCallback<Object>(context, false) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 200) {
                                getCards();
                            }
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private void setWithoutCard() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().setWithoutCard(TokenStorage.getToken(context))
                    .enqueue(new BaseCallback<Object>(context, false) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 200) getCards();
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private void deleteCard(Card card) {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().deleteCard(TokenStorage.getToken(context), card.getCardNumber())
                    .enqueue(new BaseCallback<Object>(context, false) {
                        @Override
                        protected void onResult(int code, Object result) {
                            getCards();
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private boolean isDataInvalid() {
        String number = etCardNumber.getText().toString().replaceAll(" ","");
        boolean isNameValid = etCardName.getText().toString().length() >= 4;
        boolean isNumberValid = number.length() == 16 && number.matches("[0-9]+");
        tvNameError.setVisibility(isNameValid ? View.GONE : View.VISIBLE);
        tvNumberError.setVisibility(isNumberValid ? View.GONE : View.VISIBLE);
        return isNameValid && isNumberValid;
    }

    private void showConfirmation(final Card card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Вы действительно хотите удалить эту карту?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCard(card);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_add_card:
                if (isDataInvalid()) {
                    addCard(etCardName.getText().toString(), etCardNumber.getText().toString().replaceAll(" ", ""));
                }
                break;
            case R.id.ll_without_card:
                setWithoutCard();
                break;
            case R.id.tv_delete:
                showConfirmation((Card) view.getTag());
                break;
            case R.id.ll_card:
                setPublicCard(((Card) view.getTag()).getCardNumber());
                break;
        }
    }

    private class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
        private Context context;
        private ArrayList<Card> cards;

        CardsAdapter(Context context, ArrayList<Card> cards) {
            this.context = context;
            this.cards = cards;
            Collections.sort(this.cards);
            viewBinderHelper.setOpenOnlyOne(true);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_card, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Card card = cards.get(position);
            holder.bind(card);

            viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return cards.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            SwipeRevealLayout swipeRevealLayout;
            View tvDelete;
            View llCard;
            TextView tvName, tvNumber;
            View ivCheck;

            ViewHolder(View itemView) {
                super(itemView);
                swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
                tvDelete = itemView.findViewById(R.id.tv_delete);
                llCard = itemView.findViewById(R.id.ll_card);
                tvName = itemView.findViewById(R.id.tv_card_name);
                tvNumber = itemView.findViewById(R.id.tv_card_number);
                ivCheck = itemView.findViewById(R.id.iv_check);
            }

            void bind(Card card) {
                tvName.setText(card.getName());
                tvNumber.setText("**** " + card.getCardNumber().substring(12, 16));
                llCard.setTag(card);
                llCard.setOnClickListener((View.OnClickListener) context);
                tvDelete.setTag(card);
                tvDelete.setOnClickListener((View.OnClickListener) context);
                ivCheck.setVisibility(card.getType().equals("PUBLIC") ? View.VISIBLE : View.GONE);
            }
        }
    }
}
