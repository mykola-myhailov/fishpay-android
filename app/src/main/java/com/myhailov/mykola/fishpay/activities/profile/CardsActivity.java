package com.myhailov.mykola.fishpay.activities.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.myhailov.mykola.fishpay.api.results.Card;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.TokenStorage;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import tw.henrychuang.lib.AutoAddTextWatcher;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class CardsActivity extends BaseActivity {


    public final static int REQUEST_CARD = 34;

    private TextView tvNameError;
    private EditText etCardName;

    private TextView tvNumberError;
    private TextView tvCardNumber;

    private TextView tvDateError;
    private EditText etDateEnd;

    private RecyclerView rvCards;
    private ProgressBar progressBar;

    private boolean isForRequest;
    private String expiresAt = "";

    private EditText etCardNumber1, etCardNumber2, etCardNumber3, etCardNumber4;
    private View llCardNumber;


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
        tvNameError = findViewById(R.id.tv_name_error);
        etCardName = findViewById(R.id.et_card_name);
        tvNumberError = findViewById(R.id.tv_number_error);
        tvCardNumber = findViewById(R.id.tv_card_number);
        llCardNumber = findViewById(R.id.ll_card_number);
        etCardNumber1 = findViewById(R.id.et_card_number_1);
        etCardNumber2 = findViewById(R.id.et_card_number_2);
        etCardNumber3 = findViewById(R.id.et_card_number_3);
        etCardNumber4 = findViewById(R.id.et_card_number_4);
        etCardNumber1.addTextChangedListener(new FocusSwitchingTextWatcher(etCardNumber2));
        etCardNumber2.addTextChangedListener(new FocusSwitchingTextWatcher(etCardNumber3));
        etCardNumber3.addTextChangedListener(new FocusSwitchingTextWatcher(etCardNumber4));
        tvCardNumber.setOnClickListener(this);
        final View tvAddCard = findViewById(R.id.tv_add_card);
        tvAddCard.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_bar);
        rvCards = findViewById(R.id.rv_cards);


        /*tvCardNumber.addTextChangedListener(new AutoAddTextWatcher(tvCardNumber, " ", 4, 8, 12));
        tvCardNumber.setImeOptions(IME_ACTION_DONE);
        final InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        tvCardNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == IME_ACTION_DONE) {
                    tvAddCard.performClick();
                    if (imm != null)
                        imm.toggleSoftInputFromWindow(tvCardNumber.getWindowToken(), 0, 0);
                    return true;
                }
                return false;
            }
        });*/

        tvDateError = findViewById(R.id.tv_date_end);
        etDateEnd = findViewById(R.id.et_date_end);
        etDateEnd.setOnClickListener(this);

        rvCards.setHasFixedSize(true);
        rvCards.setLayoutManager(new LinearLayoutManager(context));

        if (isForRequest) findViewById(R.id.ll_without_card).setVisibility(View.GONE);
            else findViewById(R.id.ll_without_card).setOnClickListener(this);
    }

    private static class FocusSwitchingTextWatcher implements TextWatcher {

        private final View nextViewToFocus;

        FocusSwitchingTextWatcher(View nextViewToFocus) {
            this.nextViewToFocus = nextViewToFocus;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 4) {
                nextViewToFocus.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void initToolbar() {
        if (isForRequest) ((TextView) findViewById(R.id.tvToolBarTitle)).setText("Выберите карту");
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

    private void addCard(String name, String number, String expiresAt) {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().createCard(TokenStorage.getToken(context), name, number, expiresAt)
                    .enqueue(new BaseCallback<Object>(context, false) {
                        @Override
                        protected void onResult(int code, Object result) {
                            if (code == 201) {
                                etCardName.getText().clear();
                        //        tvCardNumber.getText().clear();
                                getCards();
                            }
                        }
                    });
        } else Utils.noInternetToast(context);
    }

    private void setPublicCard(String cardId) {
        if (Utils.isOnline(context)) {
            ApiClient.getApiClient().setPublicCard(TokenStorage.getToken(context), cardId)
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

    private void showDataPicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                createDateSetListener(calendar), calendar.get(Calendar.YEAR),
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH);
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener createDateSetListener(final Calendar calendar) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat visibleFormat = new SimpleDateFormat("MM / yy", new Locale("ru"));
                etDateEnd.setText(visibleFormat.format(calendar.getTimeInMillis()));
                SimpleDateFormat savedFormat = new SimpleDateFormat("yyyy-MM", new Locale("ru"));
                expiresAt = savedFormat.format(calendar.getTimeInMillis());
            }
        };
    }

    private boolean isDataValid() {
        String number = etCardNumber1.getText().toString()
                + etCardNumber2.getText().toString()
                + etCardNumber3.getText().toString()
                + etCardNumber4.getText().toString();
        boolean isNameValid = etCardName.getText().toString().length() >= 4;
        boolean isNumberValid = number.length() == 16 && number.matches("[0-9]+");
        boolean isDateValid = !expiresAt.equals("");
        tvNameError.setVisibility(isNameValid ? View.GONE : View.VISIBLE);
        tvNumberError.setVisibility(isNumberValid ? View.GONE : View.VISIBLE);
        tvDateError.setVisibility(isDateValid ? View.GONE : View.VISIBLE);
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
                if (isDataValid()) {
                    String cardNumber = etCardNumber1.getText().toString()
                            + etCardNumber2.getText().toString()
                            + etCardNumber3.getText().toString()
                            + etCardNumber4.getText().toString();
                    addCard(etCardName.getText().toString(),
                            cardNumber,
                            expiresAt);
                }
                break;
            case R.id.tv_delete:
                showConfirmation((Card) view.getTag());
                break;
            case R.id.ll_without_card:
//                setWithoutCard();
//                break;
            case R.id.ll_card:
//                if (isForRequest) {
//                    setResult();
//                } else setPublicCard(((Card) view.getTag()).getCardNumber());
                chooseCard(((Card) view.getTag()));
                break;
            case R.id.et_date_end:
                showDataPicker();
                break;
            case R.id.tv_card_number:
                tvCardNumber.setVisibility(View.GONE);
                llCardNumber.setVisibility(View.VISIBLE);
                (findViewById(R.id.vDivider)).setVisibility(View.GONE);
                (findViewById(R.id.llDividers)).setVisibility(View.VISIBLE);
                break;
        }
    }

    private void chooseCard(@Nullable Card card) {
        if (isForRequest) {
            setResult(RESULT_OK, new Intent().putExtra(Keys.CARD, card));
            finish();
        } else {
            if (card != null) setPublicCard((card.getId()));
            else setWithoutCard();
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
