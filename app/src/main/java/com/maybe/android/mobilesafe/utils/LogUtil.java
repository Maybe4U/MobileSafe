package com.maybe.android.mobilesafe.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/11/13.
 */
public class LogUtil {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static int level = VERBOSE;

    public static void v(String Tag,String msg){
        if(level <= VERBOSE){
            Log.v(Tag,msg);
        }
    }

    public static void d(String Tag,String msg){
        if(level <= DEBUG){
            Log.d(Tag,msg);
        }
    }

    public static void i(String Tag,String msg){
        if(level <= INFO){
            Log.i(Tag,msg);
        }
    }

    public static void w(String Tag,String msg){
        if(level <= WARN){
            Log.w(Tag,msg);
        }
    }

    public static void e(String Tag,String msg){
        if(level <= ERROR){
            Log.e(Tag,msg);
        }
    }

    public static void show(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
