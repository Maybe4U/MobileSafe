package com.maybe.android.mobilesafe.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.maybe.android.mobilesafe.db.dao.BlackNumberDAO;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/12/26.
 */
public class BlackNumberDBOpenhelperTest {

    private Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void onCreate() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
        BlackNumberDBOpenhelper openhelper = new BlackNumberDBOpenhelper(appContext);
        openhelper.getWritableDatabase();
        //assertEquals("com.maybe.android.mobilesafe", appContext.getPackageName());
    }
    @Test
    public void add(){
        BlackNumberDAO dao = new BlackNumberDAO(appContext);
        //13792923500
        Random random = new Random();
        for(int i=0;i<99;i++){
            dao.add("13792923540" + i,random.nextInt(3) + "");
        }

    }
    @Test
    public void delete(){
        BlackNumberDAO dao = new BlackNumberDAO(appContext);
        dao.delete("119");
    }
    @Test
    public void update(){
        BlackNumberDAO dao = new BlackNumberDAO(appContext);
        dao.update("119","0");
    }
    @Test
    public void query(){
        BlackNumberDAO dao = new BlackNumberDAO(appContext);
        boolean result = dao.query("119");
        assertEquals(result,true);
    }
    @Test
    public void queryMode(){
        BlackNumberDAO dao = new BlackNumberDAO(appContext);
        String result = dao.queryMode("119");
        assertEquals(result,"0");
    }
}