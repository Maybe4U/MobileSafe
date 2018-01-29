package com.maybe.android.mobilesafe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.db.dao.BlackNumberDAO;
import com.maybe.android.mobilesafe.utils.BlackNumberInfo;
import com.maybe.android.mobilesafe.utils.LogUtil;
import com.maybe.android.mobilesafe.view.SwitchView;


import java.util.ArrayList;
import java.util.List;

/**
 * class Name  :BlackNumActivity
 * Class desc. :黑名单号码显示
 * date    : 2017/12/26
 * author   : Maybe
 */

public class BlackNumActivity extends AppCompatActivity {
    private RecyclerView lvBlackNumber;
    private List<BlackNumberInfo> infos;
    private int index = 0;
    private LinearLayout llLoading;
    private BlackNumberAdapter adapter;
    private BlackNumberDAO dao;
    //数据是否请求成功标志位
    boolean isloading = false;

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            //加载时过渡
            if(adapter == null){
                adapter = new BlackNumberAdapter(infos);
                lvBlackNumber.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
            isloading = false;
            llLoading.setVisibility(View.INVISIBLE);
        }
    };
    private int dbResult;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_number_layout);

        infos = new ArrayList<BlackNumberInfo>();

        lvBlackNumber = (RecyclerView) findViewById(R.id.lv_blacknumber);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        lvBlackNumber.setLayoutManager(layoutManager);
        llLoading = (LinearLayout)findViewById(R.id.ll_loading);
        //加载黑名单
        loadBlackNumber();
        //滑动监听
        lvBlackNumber.addOnScrollListener(new RecyclerView.OnScrollListener() {

            //滚动状态变化时回调
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(isloading){
                    return;
                }
                if(index >= dbResult){
                    Toast.makeText(BlackNumActivity.this,"我也是有底线的...",Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isUp = lvBlackNumber.canScrollVertically(1);
                boolean isDown = lvBlackNumber.canScrollVertically(-1);
                if(!isUp && isDown) {
                    isloading = true;
                    Toast.makeText(BlackNumActivity.this,"数据加载中...",Toast.LENGTH_SHORT).show();
                    index += 20;
                    loadBlackNumber();
                }

            }

            //滚动时回调
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //设置点击事件并添加黑名单
        ImageView addBlackNum = (ImageView)findViewById(R.id.add_blacknum);
        addBlackNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBlackNumber();
            }
        });

    }

    private void addBlackNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this,R.layout.blacknum_input_layout,null);
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button ok = (Button)view.findViewById(R.id.ok_btn);
        final Button cancel = (Button)view.findViewById(R.id.cancel_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取电话号码，拦截模式
                String mode = "2";
                EditText etBlackNum = (EditText)view.findViewById(R.id.text_black_num);
                //自动弹出数字键盘
                etBlackNum.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                String number = etBlackNum.getText().toString().trim();
                RadioGroup group = (RadioGroup)view.findViewById(R.id.rg_mode);
                int checkedId = group.getCheckedRadioButtonId();
                switch(checkedId){
                    case R.id.rb_call:
                        mode = "0";
                        break;
                    case R.id.rb_sms:
                        mode = "1";
                        break;
                    case R.id.rb_all:
                        mode = "2";
                        break;
                }
                //2.判断是否为空
                if(TextUtils.isEmpty(number)){
                    Toast.makeText(BlackNumActivity.this,"电话号码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                //3.保存到数据库

                dao.add(number,mode);
                //4.保存到当前列表
                BlackNumberInfo object = new BlackNumberInfo();
                object.setNumber(number);
                object.setMode(mode);

                infos.add(0,object);
                adapter.notifyDataSetChanged();
                //5.关闭对话框
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /**
     * method desc.  :loadBlackNumber  加载黑名单数据
     * params        :[]
     * return        :void
     */
    private void loadBlackNumber() {
        dao = new BlackNumberDAO(getApplicationContext());
        llLoading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                //SystemClock.sleep(3000);
                LogUtil.e("index",index + "");
                dbResult = dao.queryCount();
                if(infos == null){
                    infos = dao.queryPart(index);
                }else {
                    infos.addAll(dao.queryPart(index));
                }

                handler.sendEmptyMessage(0);
            }
        }.start();


    }
    /**
     * class Name  :BlackNumActivity 黑名单适配器
     * Class desc. :
     *     date    : 2017/12/26
     *    author   : Maybe
     */
    public class BlackNumberAdapter extends RecyclerView.Adapter<BlackNumberAdapter.ViewHolder> {

        private List<BlackNumberInfo> mInfoList;
        private Context mContext;

        public BlackNumberAdapter(List<BlackNumberInfo> infoList) {
            mInfoList = infoList;
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvNumber;
            private final TextView tvMode;
            private final CardView cardView;
            private final ImageView deleteIv;

            public ViewHolder(View view) {
                super(view);
                cardView = (CardView)view;
                tvNumber = (TextView) view.findViewById(R.id.tv_number);
                tvMode = (TextView) view.findViewById(R.id.tv_mode);
                deleteIv = (ImageView)view.findViewById(R.id.black_num_icon);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(mContext == null){
                mContext = parent.getContext();
            }
            final View view = LayoutInflater.from(mContext).inflate(R.layout.black_number_item,parent,false);
            final BlackNumberAdapter.ViewHolder holder = new BlackNumberAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(BlackNumberAdapter.ViewHolder holder, final int position) {
            final BlackNumberInfo info = mInfoList.get(position);
            holder.tvNumber.setText(info.getNumber());
            String mode = info.getMode();
            if("0".equals(mode)){
                //电话拦截
                holder.tvMode.setText("电话拦截");
            }else if("1".equals(mode)){
                //短信拦截
                holder.tvMode.setText("短信拦截");
            }else {
                //电话+短信拦截
                holder.tvMode.setText("电话+短信拦截");
            }

            //删除黑名单
            holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(BlackNumActivity.this,"点击响应",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumActivity.this);
                    builder.setTitle("确定要删除此条黑名单号码？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //1.删除数据库的数据
                            dao.delete(info.getNumber());
                            //2.删除列表中的数据
                            infos.remove(info);
                            //3.刷新
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mInfoList.size();
        }
    }
}
