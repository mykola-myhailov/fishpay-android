package com.myhailov.mykola.fishpay.activities.group_spends;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.PrefKeys;
import com.myhailov.mykola.fishpay.utils.Utils;

import java.util.ArrayList;

import static com.myhailov.mykola.fishpay.activities.profile.CardsActivity.REQUEST_CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.CARD;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBER;
import static com.myhailov.mykola.fishpay.utils.Keys.MEMBERS;
import static com.myhailov.mykola.fishpay.utils.Keys.TITLE;

public class ManualTransferActivity extends BaseActivity {
    public static final int CHOOSE_RECUEST = 51;

    private TextView tvNameFrom, tvPhoneFrom, tvNameTo, tvPhoneTo;
    private EditText etComment, etAmount;
    private ImageView ivAvatarFrom, ivAvatarTo;

    private boolean isFrom = false;
    private long myUserId;
    private ArrayList<MemberDetails> members;
    private MemberDetails member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_transfer);
        initCustomToolbar(getString(R.string.manual_transaction));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            members = getIntent().getExtras().getParcelableArrayList(Keys.MEMBERS);
            member = getIntent().getExtras().getParcelable(MEMBER);
        }

        myUserId = Long.valueOf(getSharedPreferences(PrefKeys.USER_PREFS, MODE_PRIVATE)
                .getString(PrefKeys.ID, "0"));
        initViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tv_create:
                createTransaction();
                break;
            case R.id.iv_menu_to:
                isFrom = false;
                startActivityForResult(new Intent(context, ChooseMemberActivity.class)
                        .putExtra(TITLE, context.getString(R.string.to_members))
                        .putExtra(MEMBERS, members), CHOOSE_RECUEST);
                break;
            case R.id.iv_menu:
                isFrom = true;
                startActivityForResult(new Intent(context, ChooseMemberActivity.class)
                        .putExtra(TITLE, context.getString(R.string.from_members))
                        .putExtra(MEMBERS, members), CHOOSE_RECUEST);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_RECUEST && resultCode == RESULT_OK) {
            MemberDetails memberDetails = data.getParcelableExtra(MEMBER);
            if (isFrom){
                String initials = Utils.extractInitials(memberDetails.getName(), memberDetails.getSurname());
                String photo = memberDetails.getPhoto();
                Utils.displayAvatar(context, ivAvatarFrom, photo, initials);
                tvNameFrom.setText(memberDetails.getName() + " " + memberDetails.getSurname());
                tvPhoneFrom.setText("+" + memberDetails.getPhone());
            }else {
                String initials = Utils.extractInitials(memberDetails.getName(), member.getSurname());
                String photo = memberDetails.getPhoto();
                Utils.displayAvatar(context, ivAvatarTo, photo, initials);
                tvNameTo.setText(memberDetails.getName() + " " + memberDetails.getSurname());
                tvPhoneTo.setText("+" + memberDetails.getPhone());
            }

        }
    }

    private void initViews(){
        tvNameFrom = findViewById(R.id.tv_name);
        tvPhoneFrom = findViewById(R.id.tv_phone);
        tvNameTo = findViewById(R.id.tv_name_to);
        tvPhoneTo = findViewById(R.id.tv_phone_to);
        ivAvatarFrom = findViewById(R.id.ivAvatar);
        ivAvatarTo = findViewById(R.id.ivAvatar_to);

        etAmount = findViewById(R.id.et_amount);
        etComment = findViewById(R.id.et_comment);
        setValue();

        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.iv_menu_to).setOnClickListener(this);
        findViewById(R.id.iv_menu).setOnClickListener(this);
        findViewById(R.id.tv_create).setOnClickListener(this);
    }

    private void createTransaction(){
        if (isValid()){
            sendTransaction();
        }
    }

    private void sendTransaction(){

    }

    private boolean isValid(){
        if (tvPhoneTo.getText().toString().equals(tvPhoneFrom.getText().toString())){
            toast(getString(R.string.fields_must_filled));
            return false;
        }
        if (TextUtils.isEmpty(etComment.getText().toString())){
            toast(getString(R.string.enter_comment));
            return false;
        }
        if (TextUtils.isEmpty(etAmount.getText().toString())){
            toast(getString(R.string.enter_amount));
            return false;
        }


        return true;
    }

    private void setValue(){
        for (MemberDetails member : members) {
            if (member.getUserId().equals(myUserId + "")){
                String initials = Utils.extractInitials(member.getName(), member.getSurname());
                String photo = member.getPhoto();
                Utils.displayAvatar(context, ivAvatarFrom, photo, initials);
                tvNameFrom.setText(member.getName() + " " + member.getSurname());
                tvPhoneFrom.setText("+" + member.getPhone());
                break;
            }
        }
        String initials = Utils.extractInitials(member.getName(), member.getSurname());
        String photo = member.getPhoto();
        Utils.displayAvatar(context, ivAvatarTo, photo, initials);
        tvNameTo.setText(member.getName() + " " + member.getSurname());
        tvPhoneTo.setText("+" + member.getPhone());
    }
}
