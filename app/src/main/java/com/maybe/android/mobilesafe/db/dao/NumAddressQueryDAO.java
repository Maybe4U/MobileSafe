package com.maybe.android.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.maybe.android.mobilesafe.utils.LogUtil;

/**
 * Created by Administrator on 2017/12/16.
 */

public class NumAddressQueryDAO {
    private static String path = "data/data/com.maybe.android.mobilesafe/files/address.db";
    //手机
    private static String sql1 = "select location from data2 where id = (select outkey from data1 where id = ?)";
    //座机
    private static String sql2 = "select location from data2 where area = ?";

    /**
     * fun desc. :号码归属地查询
     * params.   :号码
     * @return   :号码归属地
     */
    public static String getAddress(String number){
        String address = number;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor;

        //正则表达式
        if(number.matches("^1[345678]\\d{9}$")){
            cursor = db.rawQuery(sql1,new String[]{number.substring(0,7)});
            while (cursor.moveToNext()){
                String location = cursor.getString(0);
                address = location;
            }
            cursor.close();
            db.close();
        }
        switch (number.length()){
            case 3://110,120,119
                address = "公共安全号码";
                break;
            case 4://
                address = "模拟器号码";
                break;
            case 5://
                address = "客服号码";
                break;
            case 6://
            case 7://
            case 8://
                address = "其他号码";
                break;
            default:
                if(number!=null&&number.startsWith("0")&&number.length()>=10){
                    cursor = db.rawQuery(sql2,new String[]{number.substring(1,3)});
                    while (cursor.moveToNext()){
                        String location = cursor.getString(0);
                        address = location;
                    }

                    cursor = db.rawQuery(sql2,new String[]{number.substring(1,4)});
                    while (cursor.moveToNext()){
                        String location = cursor.getString(0);
                        address = location;
                    }
                    cursor.close();
                    db.close();
                }
        }

        return address;
    }
}
