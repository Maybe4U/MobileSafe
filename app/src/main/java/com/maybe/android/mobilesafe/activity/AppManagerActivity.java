package com.maybe.android.mobilesafe.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.VersionedPackage;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.engine.AppInfoProvider;
import com.maybe.android.mobilesafe.utils.AppInfo;
import com.maybe.android.mobilesafe.utils.ContactAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018\1\2 0002.
 */

public class AppManagerActivity extends AppCompatActivity {

    private RecyclerView lvAppManager;
    private LinearLayout llLoading;
    private List<AppInfo> appInfoList = new ArrayList<>();
    private List<AppInfo> useAppList;
    private List<AppInfo> sysAppList;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            adapter = new AppManagerAdapter(appInfoList);
            lvAppManager.setAdapter(adapter);
            llLoading.setVisibility(View.GONE);
        }
    };
    private LinearLayout ll_app_uninstall;
    private LinearLayout ll_app_start;
    private LinearLayout ll_app_share;
    private View content;
    private AppManagerAdapter adapter;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        TextView tvAvail = (TextView) findViewById(R.id.tv_avail);
        showCollapsingLayout();
        tvAvail.setText("当前可用空间：" + getAvailableSpace(Environment.getExternalStorageDirectory() + ""));
        lvAppManager = (RecyclerView) findViewById(R.id.lv_app_manager);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        lvAppManager.setLayoutManager(layoutManager);
        //获取数据
        fillData();


    }


    private void fillData() {
        llLoading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                appInfoList = AppInfoProvider.getAllAppInfo(AppManagerActivity.this);
                //划分数据
                useAppList = new ArrayList<>();
                sysAppList = new ArrayList<>();
                for (AppInfo appInfo : appInfoList) {
                    if (appInfo.isUser()) {
                        useAppList.add(appInfo);
                    } else {
                        sysAppList.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    private class AppManagerAdapter extends RecyclerView.Adapter<AppManagerAdapter.ViewHolder> implements PopupWindow.OnDismissListener, View.OnClickListener {
        private List<AppInfo> mAppInfoList;
        private Context mContext;
        private PopupWindow window;
        private AppInfo info;
        private Intent intent;


        public AppManagerAdapter(List<AppInfo> appInfoList) {
            mAppInfoList = appInfoList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mContext == null) {
                mContext = parent.getContext();
            }
            final View view = LayoutInflater.from(mContext).inflate(R.layout.app_info_item, parent, false);
            final ViewHolder holder = new ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            if (position < useAppList.size()) {
                //用户程序
                info = useAppList.get(position);
            } else {
                //系统程序
                info = sysAppList.get(position - useAppList.size());
            }

            holder.icon.setImageDrawable(info.getIcon());
            holder.name.setText(info.getName());

            Log.e("packageName", position + "");

            if (info.isUser()) {
                holder.isUser.setText("第三方应用");
            } else {
                holder.isUser.setText("系统自带应用");
            }

            lvAppManager.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    dismissPopupWindow();
                    //onDismiss();
                    //Log.e("onScrollStateChanged","状态改变");
                }
            });

            holder.appCardView.setOnLongClickListener(new View.OnLongClickListener() {
                //返回值 true 只执行长按监听 false 同时执行其他监听
                @Override
                public boolean onLongClick(View view) {
                    dismissPopupWindow();
                    //onDismiss();
                    int position = holder.getAdapterPosition();
                    if (position < useAppList.size()) {
                        //用户程序
                        info = useAppList.get(position);
                    } else {
                        //系统程序
                        info = sysAppList.get(position - useAppList.size());
                    }
//                    String packageName = info.getPackageName();
//                    Log.e("Name", info.getName());
//                    Log.e("getPackageName", info.getPackageName());
                    content = View.inflate(AppManagerActivity.this, R.layout.popupwindow_item, null);

                    popupClick();

                    window = new PopupWindow(content, RecyclerView.LayoutParams.WRAP_CONTENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT);
                    //加上背景popupwindow动画才播放
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    int x = 500;
                    window.showAtLocation(lvAppManager, Gravity.TOP + Gravity.LEFT, x, location[1]);
                    //Toast.makeText(AppManagerActivity.this,"长按",Toast.LENGTH_SHORT).show();
                    windowStart();


                    return false;
                }

                private void windowStart() {
                    //popupwindow启动动画
                    AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                    aa.setDuration(200);

                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(200);

                    AnimationSet set = new AnimationSet(false);
                    set.addAnimation(aa);
                    set.addAnimation(sa);

                    content.startAnimation(set);
                }
            });
        }

        private void windowOff() {
            //popupwindow关闭动画
            AlphaAnimation aa = new AlphaAnimation(1f, 0f);
            aa.setDuration(500);

            ScaleAnimation sa = new ScaleAnimation(1f, 0f, 1f, 0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setDuration(500);

            AnimationSet set = new AnimationSet(false);
            set.addAnimation(aa);
            set.addAnimation(sa);

            content.startAnimation(set);
        }

        private void dismissPopupWindow() {
            if (window != null && window.isShowing()) {
                window.setOnDismissListener(this);
                windowOff();
                window.dismiss();
                window = null;
            }
        }

        @Override
        public void onDismiss() {
            Log.e("dismiss", "enter");
            //dismissPopupWindow();
        }

        @Override
        public int getItemCount() {
            return sysAppList.size() + useAppList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView icon;
            private TextView name;
            private TextView isUser;
            private CardView appCardView;


            public ViewHolder(View itemView) {
                super(itemView);

                icon = (ImageView) itemView.findViewById(R.id.app_icon);
                name = (TextView) itemView.findViewById(R.id.app_name);
                isUser = (TextView) itemView.findViewById(R.id.app_location);
                appCardView = (CardView) itemView;

            }
        }

        //popupWindow 点击事件
        private void popupClick() {
            ll_app_uninstall = (LinearLayout) content.findViewById(R.id.ll_app_uninstall);
            ll_app_start = (LinearLayout) content.findViewById(R.id.ll_app_start);
            ll_app_share = (LinearLayout) content.findViewById(R.id.ll_app_share);

            ll_app_uninstall.setOnClickListener(this);
            ll_app_start.setOnClickListener(this);
            ll_app_share.setOnClickListener(this);
        }

        /**
         * 点击事件
         *
         * @param view
         */
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_app_uninstall:
                    adapter.dismissPopupWindow();
                    uninstallApp();
                    break;
                case R.id.ll_app_start:
                    adapter.dismissPopupWindow();
startApp();
                    break;
                case R.id.ll_app_share:
                    adapter.dismissPopupWindow();
                    shareApp();
                    break;
            }
        }
        //启动软件
        private void startApp() {

            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(info.getPackageName());
            startActivity(intent);
//            try {
//                PackageInfo packageInfo = pm.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
//                ActivityInfo[] infos = packageInfo.activities;
//                if(infos != null && infos.length > 0){
//                    ActivityInfo activityInfo = infos[0];
//                    String name = activityInfo.name;//全类名
//                    intent.setClassName(packageName,name);
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(AppManagerActivity.this,"这个页面不存在",Toast.LENGTH_SHORT).show();
//                }
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
        }

        //卸载软件
        private void uninstallApp() {
            if (info.isUser()) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + info.getPackageName()));

                //Log.e("uninstallApp",info.getPackageName());
                startActivityForResult(intent, 0);
            } else {
                Toast.makeText(AppManagerActivity.this, "需要root权限才能卸载", Toast.LENGTH_SHORT).show();
            }

        }
        //分享软件
        private void shareApp() {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,"推荐" + info.getName());
            startActivity(intent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //卸载应用后刷新列表
        fillData();
    }

    /**
     * 获得可用空间
     *
     * @param path
     * @return
     */
    private String getAvailableSpace(String path) {
        StatFs fs = new StatFs(path);
        long blocksLong = fs.getAvailableBlocksLong();
        long sizeLong = fs.getBlockSizeLong();
        return Formatter.formatFileSize(this, sizeLong * blocksLong);
    }


    /**
     * fun desc. :初始化可折叠标题栏
     * params.   :
     *
     * @return :
     */
    private void showCollapsingLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        ImageView funImageView = (ImageView) findViewById(R.id.fun_image_view);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("应用管理");
        Glide.with(this).load(R.drawable.app_manager_img).into(funImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
