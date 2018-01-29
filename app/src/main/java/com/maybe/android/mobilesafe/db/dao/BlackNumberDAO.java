package com.maybe.android.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;

import com.maybe.android.mobilesafe.db.BlackNumberDBOpenhelper;
import com.maybe.android.mobilesafe.utils.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class BlackNumberDAO {
    public static final String BLACKNUMBER = "blacknumber";

    private BlackNumberDBOpenhelper dbOpenhelper;

    public BlackNumberDAO(Context context) {
        dbOpenhelper = new BlackNumberDBOpenhelper(context);

    }

    /**
     * method desc.  :add 添加黑名单电话号码
     * params        :[number 黑名单电话号码, mode 拦截模式]
     * return        :void
     */
    public void add(String number,String mode){
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number",number);
        values.put("mode",mode);
        db.insert(BLACKNUMBER,null,values);
        db.close();
    }
    /**
     * method desc.  :delete 删除一条黑名单
     * params        :[number 要删除的黑名单电话号码]
     * return        :void
     */
    public void delete(String number){
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        db.delete(BLACKNUMBER,"number=?",new String[]{number});
        db.close();
    }
    /**
     * method desc.  :update 修改一条黑名单模式
     * params        :[number 要修改的黑名单电话号码, newmode 要修改的模式]
     * return        :void
     */
    public void update(String number,String newmode){
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",newmode);
        db.update(BLACKNUMBER,values,"number=?",new String[]{number});
        db.close();
    }
    /**
     * method desc.  :query  查询一条黑名单号码
     * params        :[number 要查询的号码]
     * return        :boolean 查询到返回true
     */
    public boolean query(String number){
        boolean result = false;
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        Cursor cursor = db.query(BLACKNUMBER,null,"number=?",new String[]{number},null,null,null);
        if(cursor.moveToNext()){
            result = true;
        }
        db.close();

        return result;
    }
    /**
     * method desc.  :queryMode 查询拦截模式
     * params        :[number 要查询的号码]
     * return        :String 返回mode
     */
    public String queryMode(String number){
        String result = "2";
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        Cursor cursor = db.query(BLACKNUMBER,new String[]{"mode"},"number=?",new String[]{number},null,null,null);
        if(cursor.moveToNext()){
            result = cursor.getString(0);
        }
        db.close();

        return result;
    }
    /**
     * method desc.  :queryAll 查询获取全部黑名单数据
     * params        :[]
     * return        :List<BlackNumberInfo> 黑名单集合
     */
    public List<BlackNumberInfo> queryAll(){
        List<BlackNumberInfo> result = new ArrayList<>();
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        Cursor cursor = db.query(BLACKNUMBER,new String[]{"number","mode"},null,null,null,null,null);
        while (cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            info.setNumber(number);
            String mode = cursor.getString(1);
            info.setMode(mode);

            result.add(info);
        }
        db.close();
        return result;
    }
    /**
     * method desc.  :queryPart  分段查询数据
     * params        :[index]    分段数量
     * return        :List<BlackNumberInfo>
     */
    public List<BlackNumberInfo> queryPart(int index){
        SystemClock.sleep(1000);
        final String SQL = "select number,mode from blacknumber order by _id desc limit 20 offset ?;";
        List<BlackNumberInfo> result = new ArrayList<>();
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL,new String[]{index + ""});
        while (cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            info.setNumber(number);
            String mode = cursor.getString(1);
            info.setMode(mode);

            result.add(info);
        }
        db.close();
        return result;
    }


    public int queryCount(){
        /**
         * method desc.  :queryCount 获取数据库全部数据数量
         * params        :[]
         * return        :int  数量
         */
        int result = 0;
        final String SQL = "select count(*) from blacknumber;";
        SQLiteDatabase db = dbOpenhelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL,null);
        while (cursor.moveToNext()){
            result = cursor.getInt(0);
        }
        db.close();
        return result;
    }
}
