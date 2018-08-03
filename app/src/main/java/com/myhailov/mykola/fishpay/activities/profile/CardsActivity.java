package com.myhailov.mykola.fishpay.activities.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.ApiClient;
import com.myhailov.mykola.fishpay.api.BaseCallback;
import com.myhailov.mykola.fishpay.api.BaseResponse;
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import retrofit2.Call;


public class CardsActivity extends BaseActivity {


    public final static int REQUEST_CARD = 34;

    private RecyclerView rvCards;
    private ProgressBar progressBar;

    private boolean isForRequest;;

    private AlertDialog alertDeleteCard;

    private Card deleteCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        isForRequest = getIntent().getBooleanExtra(Keys.REQUEST, false);

        initViews();
        getCards();
    }

    private void initViews() {
        initToolbar();

        final View tvAddCard = findViewById(R.id.tv_add_card);
        tvAddCard.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_bar);
        rvCards = findViewById(R.id.rv_cards);

        rvCards.setHasFixedSize(true);
        rvCards.setLayoutManager(new LinearLayoutManager(context));

        if (isForRequest) findViewById(R.id.ll_without_card).setVisibility(View.GONE);
            else findViewById(R.id.ll_without_card).setOnClickListener(this);
    }


    private void initToolbar() {
        if (isForRequest) ((TextView) findViewById(R.id.tvToolBarTitle)).setText(getString(R.string.choose_card));
        ((TextView) findViewById(R.id.tvToolBarTitle)).setText(getResources().getString(R.string.public_card));
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    // requests
    private void getCards() {
        if (Utils.isOnline(context)) {
            rvCards.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            ApiClient.getApiInterface().getCards(TokenStorage.getToken(context))
                    .enqueue(new BaseCallback<ArrayList<Card>>(context, false) {

                        @Override
                        public void onFailure(@NonNull Call<BaseResponse<ArrayList<Card>>> call, @NonNull Throwable t) {
                            super.onFailure(call, t);
                            progressBar.setVisibility(View.GONE);
                        }

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


    private void setPublicCard(final Card card) {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().setPublicCard(TokenStorage.getToken(context), card.getId())
                    .enqueue(new BaseCallback<Object>(context, false) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 200) {
//                                getCards();
                                setResult(RESULT_OK, new Intent());
                                finish();
                            }
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private void setWithoutCard() {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().setWithoutCard(TokenStorage.getToken(context))
                    .enqueue(new BaseCallback<Object>(context, false) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 200) {
//                                getCards();

                                setResult(RESULT_OK, new Intent());
                                finish();
                            }
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private void deleteCard(Card card) {
        if (Utils.isOnline(context)) {
            ApiClient.getApiInterface().deleteCard(TokenStorage.getToken(context), card.getId())
                    .enqueue(new BaseCallback<Object>(context, false) {
                        @Override
                        protected void onResult(int code, Object result) {
                            getCards();
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private void showAlertDeleteCard(){
        TextView tvDelete, tvClose, tvTitle;

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_with_two_action, null);
            dialogBuilder.setView(dialogView);
            tvTitle = dialogView.findViewById(R.id.tv_title);
            tvClose = dialogView.findViewById(R.id.tv_first_action);
            tvDelete = dialogView.findViewById(R.id.tv_second_action);
            dialogView.findViewById(R.id.tv_description).setVisibility(View.INVISIBLE);

            tvClose.setText(getString(R.string.cancel));
            tvDelete.setText(getString(R.string.ok));
            tvTitle.setText(getString(R.string.alert_delete_card));
            tvDelete.setOnClickListener(this);
            tvClose.setOnClickListener(this);

            alertDeleteCard = dialogBuilder.create();
            alertDeleteCard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDeleteCard.show();
    }



    private void showConfirmation(final Card card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.alert_delete_card));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCard(card);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_add_card:
                context.startActivity(new Intent(context, AddCardActivity.class));
                break;
            case R.id.tv_delete:
//                showConfirmation((Card) view.getTag());
                deleteCard = (Card) view.getTag();
                showAlertDeleteCard();
                break;
            case R.id.tv_first_action:
                alertDeleteCard.cancel();
                break;
            case R.id.tv_second_action:
                alertDeleteCard.cancel();
                if (deleteCard != null) {
                    deleteCard(deleteCard);
                }
                break;
            case R.id.ll_without_card:
//                setWithoutCard();
//                break;
            case R.id.ll_card:
//                if (isForRequest) {
//                    setResult();
//                } else setPublicCard(((Card) view.getTag()).getPanMasked());
                chooseCard(((Card) view.getTag()));
                break;
        }
    }

    private void chooseCard(@Nullable Card card) {
        if (isForRequest) {
            setResult(RESULT_OK, new Intent().putExtra(Keys.CARD, card));
            finish();
        } else {
            Log.d("sss", "chooseCard: " + "not request");
            if (card != null) {
                setPublicCard(card);
            } else setWithoutCard();
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
                tvNumber.setText(card.getLastFourNumbers());
                llCard.setTag(card);
                llCard.setOnClickListener((View.OnClickListener) context);
                tvDelete.setTag(card);
                tvDelete.setOnClickListener((View.OnClickListener) context);
                ivCheck.setVisibility(card.getType().equals("PUBLIC") ? View.VISIBLE : View.GONE);
            }
        }
    }
}
