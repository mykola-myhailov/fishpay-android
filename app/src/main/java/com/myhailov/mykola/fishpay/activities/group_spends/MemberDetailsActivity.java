package com.myhailov.mykola.fishpay.activities.group_spends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.BaseActivity;
import com.myhailov.mykola.fishpay.api.results.MemberDetails;
import com.myhailov.mykola.fishpay.utils.Keys;
import com.myhailov.mykola.fishpay.utils.Utils;

public class MemberDetailsActivity extends BaseActivity {

    private MemberDetails member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);

        member = getIntent().getExtras().getParcelable(Keys.MEMBER);
        initViews();
        initRecyclerView();
    }

    @Override
    public void onClick(View view) {

    }

    private void initViews() {
        String name = member.getName();
        String surname = member.getSurname();
        ((TextView) findViewById(R.id.tvName)).setText(String.format("%s %s", name, surname));
        String initials = Utils.extractInitials(name, surname);
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        String photo = member.getPhoto();
        Utils.displayAvatar(context, ivAvatar, photo, initials);
        Utils.setText((TextView) findViewById(R.id.tvSum), member.getSum());
        Utils.setText((TextView) findViewById(R.id.tvBalance), member.getRelativeBallance());
        Utils.setText((TextView) findViewById(R.id.tvForYou), member.getCommitment());
    }


    private void initRecyclerView() {

    }
}
