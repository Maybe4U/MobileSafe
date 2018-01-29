package com.maybe.android.mobilesafe.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.activity.ContactActivity;
import com.maybe.android.mobilesafe.activity.WizardActivity03;

import java.util.List;

/**
 * Created by Administrator on 2017/12/14.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> mContactList;
    private Context mContext;

    public ContactAdapter(List<Contact> contactList) {
        mContactList = contactList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private TextView textName;
        private TextView textNumber;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view;
            textName = (TextView) view.findViewById(R.id.text_name);
            textNumber = (TextView) view.findViewById(R.id.text_number);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        final View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"点击响应",Toast.LENGTH_SHORT).show();
                int position = holder.getAdapterPosition();
                Contact contact = mContactList.get(position);
                Intent intent = new Intent(mContext,WizardActivity03.class);
                LogUtil.e("name",contact.getName());
                intent.putExtra("name",contact.getName());
                intent.putExtra("number",contact.getNumber());
                mContext.startActivity(intent);
                //在适配器里将ContactActivity从栈里销毁防止其他页面点击返回时再次进入ContactActivity
                ((ContactActivity)mContext).finish();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = mContactList.get(position);
        holder.textName.setText(contact.getName());
        holder.textNumber.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }


}
