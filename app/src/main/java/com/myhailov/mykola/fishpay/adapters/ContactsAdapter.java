package com.myhailov.mykola.fishpay.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mykola Myhailov  on 15.03.18.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private Context context;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public ContactsAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    private List<Contact> contacts;

     class ContactsViewHolder extends RecyclerView.ViewHolder{

         private SwipeRevealLayout swipeRevealLayout;
         private ImageView ivAvatar, ivInvite;
         private TextView tvName, tvInitials, tvDelete;
         private View container;

        ContactsViewHolder(View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            tvName = itemView.findViewById(R.id.tvName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivInvite = itemView.findViewById(R.id.ivInvite);
            container = itemView.findViewById(R.id.container);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvDelete = itemView.findViewById(R.id.tv_delete);
        }
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        if (contact == null) return;
        final String name = contact.getName();
        long userId = contact.getUserId();

        if (name != null) holder.tvName.setText(name.toUpperCase());
        String photo = contact.getPhoto();
        String phone = contact.getPhone();
        final String initials = Utils.extractInitials(name, "");
        if (userId == 0) {
            //this contact is not app user
        /*    Picasso picasso = new Picasso.Builder(context)
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            holder.tvInitials.setText(initials);
                        }
                    })
                    .build();*/
            if (photo != null && !photo.equals("")) {
                Uri photoUri = Uri.parse(contact.getPhoto());
                Picasso.with(context).load(photoUri).resize(50, 50).into(holder.ivAvatar);
                if (holder.ivAvatar.getDrawable() == null) holder.tvInitials.setText(initials);
            } else holder.tvInitials.setText(initials);


        } else {  //this contact is app user
            if (photo != null && !photo.equals("")) {
                Picasso.with(context).load(photo).resize(50, 50).into(holder.ivAvatar);
                if (holder.ivAvatar.getDrawable() == null) holder.tvInitials.setText(initials);
            } else holder.tvInitials.setText(initials);
            holder.container.setTag(contact);
            holder.container.setOnClickListener((View.OnClickListener) context);


            if (contact.isActiveUser()) {
                holder.tvDelete.setOnClickListener((View.OnClickListener) context);
                viewBinderHelper.bind(holder.swipeRevealLayout,
                        String.valueOf(contacts.get(position).getId()));

            }
        }
    }


    @Override
    public int getItemCount() {
        if (contacts == null) return 0;
        return contacts.size();
    }
}
