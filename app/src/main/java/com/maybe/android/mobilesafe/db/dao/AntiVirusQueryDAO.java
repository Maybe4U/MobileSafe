package com.maybe.android.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/12/16.
 */

public class AntiVirusQueryDAO {
    private static String path = "data/data/com.maybe.android.mobilesafe/files/antivirus.db";
    //手机
    private static String sql1 = "select desc from datable where md5 = ?";

    /**
     * fun desc. :返回有值就是病毒
     * params.   :
     * @return   :
     */
    public static String getDesc(String md5){
        String result = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery(sql1,new String[]{md5});
        if(cursor.moveToNext()){
            String desc = cursor.getString(0);
            result = desc;
        }

        return result;
    }
}
