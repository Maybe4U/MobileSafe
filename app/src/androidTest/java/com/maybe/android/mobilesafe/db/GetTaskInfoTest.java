package com.maybe.android.mobilesafe.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.maybe.android.mobilesafe.engine.AppInfoProvider;
import com.maybe.android.mobilesafe.engine.TaskInfoProvider;
import com.maybe.android.mobilesafe.utils.AppInfo;
import com.maybe.android.mobilesafe.utils.TaskInfo;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by Administrator on 2018\1\5 0005.
 */
@RunWith(AndroidJUnit4.class)
public class GetTaskInfoTest {
    private static final String TAG = "GetAppInfoTest";

    @Test
    public void getTaskTest(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<TaskInfo> infos = TaskInfoProvider.getAllTaskInfos(appContext);
        for(TaskInfo info : infos){
            Log.d(TAG,"TaskInfo = " +info.toString());
        }
    }
}
