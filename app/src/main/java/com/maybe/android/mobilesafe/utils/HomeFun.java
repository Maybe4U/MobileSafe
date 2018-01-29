package com.maybe.android.mobilesafe.utils;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;

/**
 * Created by Administrator on 2017/12/9.
 */

public class HomeFun {

    public HomeFun(int funImageId, String funName) {
        this.funImageId = funImageId;
        this.funName = funName;
    }

    private int funImageId;
    private String funName;

    public int getFunImageId() {
        return funImageId;
    }

    public void setFunImageId(int funImageId) {
        this.funImageId = funImageId;
    }

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }
}
