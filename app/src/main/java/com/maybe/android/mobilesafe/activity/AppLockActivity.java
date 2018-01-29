package com.maybe.android.mobilesafe.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.db.dao.AppLockDAO;
import com.maybe.android.mobilesafe.engine.AppInfoProvider;
import com.maybe.android.mobilesafe.engine.TaskInfoProvider;
import com.maybe.android.mobilesafe.utils.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/24.
 */

public class AppLockActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_unlock;
    private TextView tv_locked;
    private LinearLayout ll_unlock;
    private LinearLayout ll_locked;
    private ListView lv_unlock;
    private ListView lv_locked;
    private List<AppInfo> appInfos;
    private List<AppInfo> unlockAppInfos;
    private List<AppInfo> lockedAppInfos;
    private AppLockDAO dao;
    private AppLockAdapter unlockAdapter;
    private AppLockAdapter lockedAdapter;
    private TextView tv_unlock_info;
    private TextView tv_locked_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_lock_layout);
        //初始化view
        initView();
        //设置事件
        setEvents();
    }

    /**
     * method desc.  :初始化view
     * params        :
     * return        :
     */
    private void initView() {
        tv_unlock = (TextView)findViewById(R.id.tv_unlock);
        tv_locked = (TextView)findViewById(R.id.tv_locked);
        ll_unlock = (LinearLayout)findViewById(R.id.ll_unlock);
        ll_locked = (LinearLayout)findViewById(R.id.ll_locked);

        tv_unlock_info = (TextView)findViewById(R.id.tv_unlock_info);
        tv_locked_info = (TextView)findViewById(R.id.tv_locked_info);

        lv_unlock = (ListView)findViewById(R.id.lv_unlock);
        lv_locked = (ListView)findViewById(R.id.lv_locked);
    }
    /**
     * method desc.  :setEvents 设置点击事件等
     * params        :[]
     * return        :void
     */
    public void setEvents() {
        //设置点击事件
        tv_unlock.setOnClickListener(AppLockActivity.this);
        tv_locked.setOnClickListener(AppLockActivity.this);
        //获取所有应用信息
        appInfos = AppInfoProvider.getAllAppInfo(this);
        lockedAppInfos = new ArrayList<>();
        unlockAppInfos = new ArrayList<>();
        dao = new AppLockDAO(this);
        for(AppInfo appInfo : appInfos){
            if(dao.query(appInfo.getPackageName())){
                lockedAppInfos.add(appInfo);
            }else {
                unlockAppInfos.add(appInfo);
            }
        }

        //设置未加锁listview
        unlockAdapter = new AppLockAdapter(false);
        lv_unlock.setAdapter(unlockAdapter);
        //设置加锁listview
        lockedAdapter = new AppLockAdapter(true);
        lv_locked.setAdapter(lockedAdapter);
    }
    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_unlock:
                unlockApp();
                break;
            case R.id.tv_locked:
                lockedApp();
                break;
            default:
                break;
        }
    }

    //未加密软件点击事件
    private void unlockApp(){
        tv_unlock.setBackgroundResource(R.drawable.wallpaper_button_down);
        tv_locked.setBackgroundResource(R.drawable.wallpaper_button_normal);
        ll_unlock.setVisibility(View.VISIBLE);
    }

    //已加密软件点击事件
    private void lockedApp(){
        tv_locked.setBackgroundResource(R.drawable.wallpaper_button_down);
        tv_unlock.setBackgroundResource(R.drawable.wallpaper_button_normal);
        ll_unlock.setVisibility(View.GONE);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            unlockAdapter.notifyDataSetChanged();
            lockedAdapter.notifyDataSetChanged();
        }
    };
    private class AppLockAdapter extends BaseAdapter{
        private boolean isFlag = false; //true 已加锁  false 未加锁  标志位

        public AppLockAdapter(boolean isFlag) {
            this.isFlag = isFlag;
        }

        @Override
        public int getCount() {
            if(isFlag){
                return lockedAppInfos.size();
            }else {
                return unlockAppInfos.size();
            }

        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final View view;
            ViewHolder holder;
            if(convertView != null){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                view = View.inflate(AppLockActivity.this,R.layout.app_lock_item,null);
                holder = new ViewHolder();
                holder.app_icon = (ImageView)view.findViewById(R.id.app_icon);
                holder.app_name = (TextView) view.findViewById(R.id.app_name);
                holder.lock_status = (ImageView)view.findViewById(R.id.lock_status);
                view.setTag(holder);
            }
            //得到应用信息
            final AppInfo app;
            if(isFlag){
                app = lockedAppInfos.get(position);
                holder.lock_status.setImageResource(R.drawable.app_locked);
                tv_locked_info.setText("加锁软件" + lockedAppInfos.size());
            }else {
                app = unlockAppInfos.get(position);
                holder.lock_status.setImageResource(R.drawable.app_unlock);
                tv_unlock_info.setText("未加锁软件" + unlockAppInfos.size());
            }

            holder.app_icon.setImageDrawable(app.getIcon());
            holder.app_name.setText(app.getName());
            holder.lock_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("onClick","位置是" + position);
                    if(isFlag){

                        //4.动画效果
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                                0,Animation.RELATIVE_TO_SELF,
                                -1.0f,Animation.RELATIVE_TO_SELF,
                                0,Animation.RELATIVE_TO_SELF,
                                0);
                        ta.setDuration(500);
                        view.setAnimation(ta);
                        new Thread(){
                            @Override
                            public void run() {
                                try {
                                    sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // 1.添加到已加锁数据库
                                dao.delete(app.getPackageName());
                                //2.添加到已加锁当前列表
                                unlockAppInfos.add(app);
                                //3.未加锁列表移除当前应用
                                lockedAppInfos.remove(app);
                                handler.sendEmptyMessage(0);
                            }
                        }.start();
                    }else {

                        //4.动画效果
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                                0,Animation.RELATIVE_TO_SELF,
                                1.0f,Animation.RELATIVE_TO_SELF,
                                0,Animation.RELATIVE_TO_SELF,
                                0);
                        ta.setDuration(500);
                        view.setAnimation(ta);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //1.添加到已加锁数据库
                                dao.add(app.getPackageName());
                                //2.添加到已加锁当前列表
                                lockedAppInfos.add(app);
                                //3.未加锁列表移除当前应用
                                unlockAppInfos.remove(app);
                                unlockAdapter.notifyDataSetChanged();
                                lockedAdapter.notifyDataSetChanged();
                            }
                        },500);
                    }
//                    unlockAdapter.notifyDataSetChanged();
//                    lockedAdapter.notifyDataSetChanged();
                }
            });
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    static class ViewHolder{
        ImageView app_icon;
        ImageView lock_status;
        TextView app_name;
    }
}
