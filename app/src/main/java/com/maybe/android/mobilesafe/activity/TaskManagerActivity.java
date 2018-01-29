package com.maybe.android.mobilesafe.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.engine.TaskInfoProvider;
import com.maybe.android.mobilesafe.utils.SystemInfoUtil;
import com.maybe.android.mobilesafe.utils.TaskInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018\1\2 0002.
 */

public class TaskManagerActivity extends AppCompatActivity {
    //用户进程文本信息
    private static final int USER_PROCESS_TEXT = 1;
    private static final int SYS_PROCESS_TEXT = 1;

    private List<TaskInfo> taskInfos;

    private List<TaskInfo> userTaskInfos;

    private List<TaskInfo> sysTaskInfos;

    private TaskAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new TaskAdapter();
            lv_task_manager.setAdapter(adapter);
            setListViewHeightBasedOnChildren(lv_task_manager);
            ll_loading.setVisibility(View.GONE);
        }
    };
    private ListView lv_task_manager;
    private LinearLayout ll_loading;
    private ActivityManager am;
    private int totalCount;
    private long availRam;
    private TextView tv_run_process;
    private TextView tv_avail_ram;
    private long totalRam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        showCollapsingLayout();

        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        initView();

        fillData();
        //listview条目点击事件
        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = lv_task_manager.getItemAtPosition(position);
                if (object != null) {
                    TaskInfo taskInfo = (TaskInfo) object;
                    CheckBox cb_status = (CheckBox) view.findViewById(R.id.cb_status);
                    if (getPackageName().equals(taskInfo.getPackageName())) {
                        return;
                    }

                    if (taskInfo.isChecked()) {
                        taskInfo.setChecked(false);
                        cb_status.setChecked(false);
                    } else {
                        taskInfo.setChecked(true);
                        cb_status.setChecked(true);
                    }
                }
            }
        });
    }

    private class TaskAdapter extends BaseAdapter {

        private TaskInfo taskInfo;


        @Override
        public int getCount() {
            SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
            boolean showSys = sp.getBoolean("showSys",true);
            //Log.e("getCount",taskInfos.size() + "");
            if(showSys){
                return userTaskInfos.size() + sysTaskInfos.size() + USER_PROCESS_TEXT + SYS_PROCESS_TEXT;
            }else {
                return userTaskInfos.size() + USER_PROCESS_TEXT ;
            }

        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v;
            ViewHolder holder;
            if (i == 0) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setText("用户进程" + "(" + userTaskInfos.size() + ")");
                tv.setBackgroundColor(Color.parseColor("#8839b3a9"));
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (i == userTaskInfos.size() + USER_PROCESS_TEXT) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setText("系统进程" + "(" + sysTaskInfos.size() + ")");
                tv.setBackgroundColor(Color.parseColor("#8839b3a9"));
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (i <= userTaskInfos.size()) {
                taskInfo = userTaskInfos.get(i - USER_PROCESS_TEXT);
            } else {
                taskInfo = sysTaskInfos.get(i - userTaskInfos.size() - USER_PROCESS_TEXT - SYS_PROCESS_TEXT);
            }
            if (view != null && view instanceof android.support.design.widget.CoordinatorLayout) {
                v = view;
                holder = (ViewHolder) v.getTag();
            } else {
                v = View.inflate(TaskManagerActivity.this, R.layout.task_info_item, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) v.findViewById(R.id.task_icon);
                holder.name = (TextView) v.findViewById(R.id.task_name);
                holder.memosize = (TextView) v.findViewById(R.id.task_memosize);
                holder.cb_status = (CheckBox) v.findViewById(R.id.cb_status);
                v.setTag(holder);
            }

            if (getPackageName().equals(taskInfo.getPackageName())) {
                holder.cb_status.setVisibility(View.GONE);
            }

            //TaskInfo taskInfo = taskInfos.get(i);

            holder.name.setText(taskInfo.getName());
            if (taskInfo.getIcon() == null) {
                holder.icon.setImageResource(R.mipmap.ic_launcher);
            } else {
                holder.icon.setImageDrawable(taskInfo.getIcon());
            }
            holder.memosize.setText(Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemoinfosize()));
            if (taskInfo.isChecked()) {
                holder.cb_status.setChecked(true);
            } else {
                holder.cb_status.setChecked(false);
            }
            return v;
        }

        @Override
        public Object getItem(int i) {
            if (i == 0) {
                return null;
            } else if (i == userTaskInfos.size() + USER_PROCESS_TEXT) {
                return null;
            } else if (i <= userTaskInfos.size()) {
                taskInfo = userTaskInfos.get(i - USER_PROCESS_TEXT);
            } else {
                taskInfo = sysTaskInfos.get(i - userTaskInfos.size() - USER_PROCESS_TEXT - SYS_PROCESS_TEXT);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView memosize;
        CheckBox cb_status;
    }

    //解决滑动冲突
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();//获取Adapter
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;  //定义总高度
        //根据listAdapter.getCount()获取当前拥有多少个item项，
        // 然后进行遍历对每一个item获取高度再相加最终获得总的高度。
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //获取到list的布局属性
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }

    /**
     * fun desc. :加载数据
     * params.   :
     *
     * @return :
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                taskInfos = TaskInfoProvider.getAllTaskInfos(TaskManagerActivity.this);
                userTaskInfos = new ArrayList<>();
                sysTaskInfos = new ArrayList<>();

                for (TaskInfo taskInfo : taskInfos) {
                    if (taskInfo.isUser()) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        sysTaskInfos.add(taskInfo);
                    }

                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * fun desc. :初始化view
     * params.   :
     *
     * @return :
     */
    private void initView() {
        tv_run_process = (TextView) findViewById(R.id.tv_run_process);
        tv_avail_ram = (TextView) findViewById(R.id.tv_avail_ram);
        totalRam = SystemInfoUtil.getTotalRam(this);
        availRam = SystemInfoUtil.getAvailRam(this);
        totalCount = SystemInfoUtil.getRunningProcess(this);
        tv_run_process.setText("当前进程：" + SystemInfoUtil.getRunningProcess(this));

        tv_avail_ram.setText("总内存/剩余内存：" + Formatter.formatFileSize(this, totalRam) +
                "/" + Formatter.formatFileSize(this, availRam));

        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
    }

    /**
     * button点击事件监听
     * @param view
     */
    //全选
    public void selectAll(View view) {
        for (TaskInfo taskInfo : userTaskInfos) {
            if (getPackageName().equals(taskInfo.getPackageName())) {
                continue;
            }
            taskInfo.setChecked(true);
        }

        for (TaskInfo taskInfo : sysTaskInfos) {
            taskInfo.setChecked(true);
        }
        //刷新数据
        adapter.notifyDataSetChanged();
    }

    //反选
    public void unSelect(View view) {
        for (TaskInfo taskInfo : userTaskInfos) {
            if (getPackageName().equals(taskInfo.getPackageName())) {
                continue;
            }
            taskInfo.setChecked(!taskInfo.isChecked());
        }

        for (TaskInfo taskInfo : sysTaskInfos) {
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        //刷新数据
        adapter.notifyDataSetChanged();
    }

    //杀进程
    public void killAll(View view) {
        int killedCount = 0;
        long releaseMemoSize = 0;
        List<TaskInfo> killedTask = new ArrayList<>();
        for (TaskInfo taskInfo : userTaskInfos) {
            if (taskInfo.isChecked()) {
                am.killBackgroundProcesses(taskInfo.getPackageName());
                killedTask.add(taskInfo);
                killedCount++;
                releaseMemoSize += taskInfo.getMemoinfosize();
            }
        }

        for (TaskInfo taskInfo : sysTaskInfos) {
            if (taskInfo.isChecked()) {
                am.killBackgroundProcesses(taskInfo.getPackageName());
                killedTask.add(taskInfo);
                killedCount++;
                releaseMemoSize += taskInfo.getMemoinfosize();
            }
        }
        //将要杀死的进程放到新的列表里重新遍历然后移除，防止并发异常错误
        for (TaskInfo info : killedTask) {
            if (info.isUser()) {
                userTaskInfos.remove(info);
            } else {
                sysTaskInfos.remove(info);
            }
        }
        totalCount -= killedCount;
        availRam += releaseMemoSize;
        tv_run_process.setText("当前进程：" + totalCount);

        tv_avail_ram.setText("总内存/剩余内存：" + Formatter.formatFileSize(this, totalRam) +
                "/" + Formatter.formatFileSize(this, availRam));

        //刷新数据
        adapter.notifyDataSetChanged();
        //fillData();
    }
    //启动进程设置界面
    public void setting(View view){
        Intent intent = new Intent(TaskManagerActivity.this,TaskManagerSetting.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //fillData();
        adapter.notifyDataSetChanged();
    }

    /**
     * fun desc. :初始化可折叠标题栏
     * params.   :
     *
     * @return :
     */
    private void showCollapsingLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        ImageView funImageView = (ImageView) findViewById(R.id.fun_image_view);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("进程管理");
        Glide.with(this).load(R.drawable.pid_control_img).into(funImageView);
    }
}
