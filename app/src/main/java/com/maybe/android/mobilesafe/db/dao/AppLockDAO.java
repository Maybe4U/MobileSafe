package com.maybe.android.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.SystemClock;

import com.maybe.android.mobilesafe.db.AppLockDBOpenhelper;
import com.maybe.android.mobilesafe.db.BlackNumberDBOpenhelper;
import com.maybe.android.mobilesafe.utils.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class AppLockDAO {
    public static final String APPLOCK = "applock";

    private AppLockDBOpenhelper dbOpenhelper;
    private Context context;
    private Uri uri = Uri.parse("content://com.maybe.android.mobilesafe.datachange");;

    public AppLockDAO(Context context) {
        dbOpenhelper = new AppLockDBOpenhelper(context);
        this.context = context;
    }

    /**
     * method desc.  :add 添加一条应用到应用锁里
     * params        :[packageName 应用包名]
     * return        :void
     */
    public void add(String packageName){
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packageName",packageName);
        db.insert(APPLOCK,null,values);
        db.close();
        //利用内容观察者查看数据是否变化，一旦变化发送出去
        context.getContentResolver().notifyChange(uri,null);
    }
    /**
     * method desc.  :delete 从应用锁里删除一条已经加锁的应用
     * params        :[packageName 要删除的应用包名]
     * return        :void
     */
    public void delete(String packageName){
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        db.delete(APPLOCK,"packageName=?",new String[]{packageName});
        db.close();
        //利用内容观察者查看数据是否变化，一旦变化发送出去
        context.getContentResolver().notifyChange(uri,null);
    }
    /**
     * method desc.  :query  查询一条应用包名
     * params        :[packageName 应用包名]
     * return        :boolean 查询到返回true
     */
    public boolean query(String packageName){
        boolean result = false;
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        Cursor cursor = db.query(APPLOCK,null,"packageName=?",new String[]{packageName},null,null,null);
        if(cursor.moveToNext()){
            result = true;
        }
        db.close();

        return result;
    }
    /**
     * method desc.  :queryAll 查询获取全部应用的包名
     * params        :[]
     * return        :List<BlackNumberInfo> 黑名单集合
     */
    public List<String> queryAll(){
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        Cursor cursor = db.query(APPLOCK,new String[]{"packageName"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String number = cursor.getString(0);
            result.add(number);
        }
        db.close();
        return result;
    }
}
