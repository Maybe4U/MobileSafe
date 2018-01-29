package com.maybe.android.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/12/26.
 */

public class AppLockDBOpenhelper extends SQLiteOpenHelper{

    //构造方法-把数据库已经创建了
    public AppLockDBOpenhelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    //创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * _id 主键 自动增长
         * number 黑名单电话号码
         * mode 拦截模式 0 短信拦截 1 电话拦截 2 全部拦截
         */
        db.execSQL("create table applock(_id integer primary key autoincrement,packageName varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
