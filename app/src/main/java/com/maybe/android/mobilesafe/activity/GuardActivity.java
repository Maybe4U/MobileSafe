package com.maybe.android.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;

/**
 * class Name  :GuardActivity  通讯卫士界面
 * Class desc. :
 *     date    : 2017/12/26
 *    author   : Maybe
 */
public class GuardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard);
        showCollapsingLayout();
        CardView cardView = (CardView)findViewById(R.id.black_number);
        /**
         * fun desc. :电话号码归属地查询按键
         * params.   :
         * @return   :
         */
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuardActivity.this,BlackNumActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * fun desc. :初始化可折叠标题栏
     * params.   :
     * @return   :
     */
    private void showCollapsingLayout(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        ImageView funImageView = (ImageView)findViewById(R.id.fun_image_view);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("通讯卫士");
        Glide.with(this).load(R.drawable.comm_guard_img).into(funImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
