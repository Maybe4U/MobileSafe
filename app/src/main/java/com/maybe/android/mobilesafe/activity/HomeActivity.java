package com.maybe.android.mobilesafe.activity;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.utils.HomeFun;
import com.maybe.android.mobilesafe.utils.HomeFunAdapter;
import com.maybe.android.mobilesafe.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGABannerUtil;

public class HomeActivity extends AppCompatActivity {

    private HomeFun[] homeFuns = {new HomeFun(R.drawable.mobile_safe,"手机防盗"),
            new HomeFun(R.drawable.comm_guard,"通讯卫士"),new HomeFun(R.drawable.app_manager,"应用管理"),
            new HomeFun(R.drawable.pid_control,"进程管理"),new HomeFun(R.drawable.data_count,"流量统计"),
            new HomeFun(R.drawable.mobile_virus,"手机杀毒"),new HomeFun(R.drawable.cache_clean,"缓存清理"),
            new HomeFun(R.drawable.advanced_tool,"高级工具"),new HomeFun(R.drawable.setting_center,"设置中心")};


    private List<HomeFun> funList = new ArrayList<>();

    private HomeFunAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //显示banner广告轮播
        BGABanner banner = (BGABanner)findViewById(R.id.banner_ads_pager);
        List<View> views = new ArrayList<>();
        views.add(BGABannerUtil.getItemImageView(this,R.drawable.banner_01));
        views.add(BGABannerUtil.getItemImageView(this,R.drawable.banner_02));
        views.add(BGABannerUtil.getItemImageView(this,R.drawable.banner_03));

        banner.setData(views);
        //监听广告 item 的单击事件，在 BGABanner 里已经帮开发者处理了防止重复点击事件
        banner.setDelegate(new BGABanner.Delegate<ImageView,String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                Toast.makeText(banner.getContext(), "点击了" + position, Toast.LENGTH_SHORT).show();
                if (position==2){
                    banner.stopAutoPlay();
                }
            }
        });



        //显示功能模块界面
        initFunRes();
        RecyclerView recyclerHomeList = (RecyclerView)findViewById(R.id.recycler_home);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        int RecyclerViewHeight = recyclerHomeList.getHeight();
        recyclerHomeList.setLayoutManager(layoutManager);
        adapter = new HomeFunAdapter(funList);
        recyclerHomeList.setAdapter(adapter);

    }


    /**
     * fun desc. :初始化功能模块文字和图片信息
     * params.   :
     * @return   :
     */
    private void initFunRes() {
        funList.clear();
        for(int i=0;i<homeFuns.length;i++){
            funList.add(homeFuns[i]);
        }
    }
}
