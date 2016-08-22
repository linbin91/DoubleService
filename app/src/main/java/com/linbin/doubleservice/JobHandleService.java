package com.linbin.doubleservice;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.linbin.doubleservice.service.LocalService;
import com.linbin.doubleservice.service.RemoteService;

import java.util.List;

/**
 * Created by Administrator on 2016/8/23.
 */

@SuppressLint("NewApi")
public class JobHandleService extends JobService{
    private  int kJobId = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("linbin","onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("linbin","onStartCommand");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    /**
     * 获取任务
     * @return
     */
    private JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++,new ComponentName(this,JobHandleService.class));
        //任何网络都可以
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        //手机关机也能够启动起来
        builder.setPersisted(true);
        //不受充电影响
        builder.setRequiresCharging(true);
        //不受手机闲置影响
        builder.setRequiresDeviceIdle(true);
        //提高重启概率
        builder.setPeriodic(10);
        return  builder.build();
    }

    /*
    *注册任务
     */
    public void  scheduleJob(JobInfo info){
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(info);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e("linbin","onStartJob");
        boolean isLocalServiceWork = isServiceWork(this,"com.linbin.doubleservice.service.LocalService");
        boolean isRemoteServiceWork = isServiceWork(this,"com.linbin.doubleservice.service.RemoteService");
        if (!isLocalServiceWork || !isRemoteServiceWork){
            this.startService(new Intent(this, LocalService.class));
            this.startService(new Intent(this, RemoteService.class));
            Toast.makeText(this,"process start",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("linbin","onStopJob");
        scheduleJob(getJobInfo());
        return true;
    }

    /**
     *
     * @param context
     * @param serviceName 包名+类名
     * @return  true 正在运行，false 服务没有运行
     */
    public boolean isServiceWork(Context context, String serviceName){
        boolean isWork = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = am.getRunningServices(100);
        if (myList.size() <= 0){
            isWork = false;
        }
        for (int i = 0; i < myList.size(); i++){
            String name = myList.get(i).service.getClassName().toString();
            if (name.equals(serviceName)){
                isWork = true;
                break;
            }
        }
        return  isWork;
    }

    @Override
    public void onDestroy() {
        Log.e("linbin","onDestroy");
        super.onDestroy();
    }
}
