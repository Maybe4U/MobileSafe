package com.maybe.android.mobilesafe.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.activity.AdvancedToolActivity;
import com.maybe.android.mobilesafe.activity.AppManagerActivity;
import com.maybe.android.mobilesafe.activity.CacheCleanActivtiy;
import com.maybe.android.mobilesafe.activity.GuardActivity;
import com.maybe.android.mobilesafe.activity.HomeActivity;
import com.maybe.android.mobilesafe.activity.MobileVirusActivity;
import com.maybe.android.mobilesafe.activity.SettingActivity;
import com.maybe.android.mobilesafe.activity.SafeActivity;
import com.maybe.android.mobilesafe.activity.TaskManagerActivity;
import com.maybe.android.mobilesafe.view.PwdDialog;

import java.util.List;

/**
 * Project Name:HomeFunAdapter
 * Class desc. :卡片式布局适配器
 *     date    : 2017/12/9
 *    author   : Maybe
 */

public class HomeFunAdapter extends RecyclerView.Adapter<HomeFunAdapter.ViewHolder>{

    private Context mContext;
    private List<HomeFun> mFunList;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private AlertDialog pwdDialog;
    private EditText textPwdUp;
    private EditText textPwddown;
    private AlertDialog dialog;
    private EditText textPwd;
    private Intent intent;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView funImageIv;
        TextView funNametv;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view;
            funImageIv = (ImageView)view.findViewById(R.id.iv_image);
            funNametv = (TextView)view.findViewById(R.id.tv_name);
        }
    }

    public HomeFunAdapter(List<HomeFun> funList) {
        mFunList = funList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_item,parent,false);
        int mRecyclerViewHeight = parent.getHeight();

        //适配不同屏幕，使得图片全部占满剩余空间
        view.getLayoutParams().height = ( mRecyclerViewHeight -100 ) / 3 ;
        final ViewHolder holder = new ViewHolder(view);
        //卡片式布局点击事件监听
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                switch(position){
                    case 0:

                        sp = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
                        String password = sp.getString("password",null);
                        if (TextUtils.isEmpty(password)) {
                            //如果密码为空，显示密码设置对话框
                            showPwdSetDialog();
                        }else{
                            //如果读取密码不为空，显示密码输入对话框
                            showPwdInputDialog(password);
                        }

                        break;
                    case 1:
                        intent = new Intent(mContext, GuardActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(mContext, AppManagerActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(mContext, TaskManagerActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 4:
                        break;
                    case 5:
                        intent = new Intent(mContext, MobileVirusActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(mContext, CacheCleanActivtiy.class);
                        mContext.startActivity(intent);
                        break;
                    case 7:
                        intent = new Intent(mContext, AdvancedToolActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 8:
                        intent = new Intent(mContext, SettingActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case 9:
                        break;
                }

            }
        });
        return holder;
    }

    /**
     * fun desc. :显示密码设置对话框
     * params.   :
     * @return   :
     */
    private void showPwdSetDialog() {

        View view = View.inflate(mContext,R.layout.pwd_set_layout,null);
        pwdDialog = new AlertDialog.Builder(mContext).setView(view).create();
        Window window = pwdDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.9f;
        window.setAttributes(lp);
        pwdDialog.show();
        textPwdUp = (EditText)view.findViewById(R.id.text_pwd_up);
        textPwddown = (EditText)view.findViewById(R.id.text_pwd_down);
        //EditText获得焦点时自动弹出数字软键盘
        textPwdUp.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        textPwddown.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        Button buttonOk = (Button)view.findViewById(R.id.ok_btn);
        Button buttonCancel = (Button)view.findViewById(R.id.cancel_btn);

        //确定键
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd1 = textPwdUp.getText().toString().trim();
                String pwd2 = textPwddown.getText().toString().trim();

                if(TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2)){
                    Toast.makeText(mContext,"密码不能为空...",Toast.LENGTH_SHORT).show();
                    return;
                }else if(pwd1.equals(pwd2)){
                    editor = sp.edit();

                    editor.putString("password",MD5Utils.encoder(pwd1));
                    editor.commit();
                    Intent intent0 = new Intent(mContext, SafeActivity.class);
                    mContext.startActivity(intent0);
                    pwdDialog.dismiss();
                }else {
                    Toast.makeText(mContext,"两次输入密码不一致...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //取消键
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdDialog.dismiss();
            }
        });
    }
    /**
     * fun desc. :显示密码输入对话框
     * params.   :
     * @return   :
     */
    private void showPwdInputDialog(final String password) {
        View view = View.inflate(mContext,R.layout.pwd_input_layout,null);
        pwdDialog = new AlertDialog.Builder(mContext).setView(view).create();
        Window window = pwdDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.9f;
        window.setAttributes(lp);
        pwdDialog.show();
        textPwd = (EditText)view.findViewById(R.id.text_pwd);
        //EditText获得焦点时自动弹出数字软键盘
        textPwd.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        Button buttonOk = (Button)view.findViewById(R.id.ok_btn);
        Button buttonCancel = (Button)view.findViewById(R.id.cancel_btn);
        //Button buttonCancel = null;
        //确定键
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = MD5Utils.encoder(textPwd.getText().toString().trim());

                if(TextUtils.isEmpty(pwd)){
                    Toast.makeText(mContext,"密码不能为空...",Toast.LENGTH_SHORT).show();
                    return;
                }else if(pwd.equals(password)){
                    Intent intent0 = new Intent(mContext, SafeActivity.class);
                    mContext.startActivity(intent0);
                }else {
                    Toast.makeText(mContext,"密码不正确...",Toast.LENGTH_SHORT).show();
                }
                pwdDialog.dismiss();
            }
        });
        //取消键
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdDialog.dismiss();
            }
        });
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeFun homeFun = mFunList.get(position);
        holder.funNametv.setText(homeFun.getFunName());
        Glide.with(mContext).load(homeFun.getFunImageId()).into(holder.funImageIv);
    }

    @Override
    public int getItemCount() {
        return mFunList.size();
    }

}
