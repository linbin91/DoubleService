package com.linbin.doubleservice.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.linbin.doubleservice.IMyDouble;

/**
 * Created by Administrator on 2016/7/31.
 */
public class LocalService extends Service {

    private MyBinder myBinder;
    private MyServiceConnect myServiceConnect;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (myBinder == null){
            myBinder = new MyBinder();
        }
        myServiceConnect = new MyServiceConnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("linbin","localservice");
        this.bindService(new Intent(this, RemoteService.class), myServiceConnect, Context.BIND_IMPORTANT);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
//        Notification.Builder builder1 = new Notification.Builder(LocalService.this);
//        builder1.setSmallIcon(R.mipmap.ic_launcher); //设置图标
//        builder1.setContentTitle("通知"); //设置标题
//        builder1.setContentText("点击查看详细内容"); //消息内容
//        Notification notification1;
//        notification1 = builder1.build();
//        startForeground(startId, notification1);
        return START_STICKY;
    }

    private class MyBinder extends IMyDouble.Stub {
        @Override
        public String getProcessName() throws RemoteException {
            return LocalService.class.getName();
        }
    }

    private class  MyServiceConnect implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("linbin","remote onServiceConnected");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("linbin","remote onServiceDisconnected");
            //远程服务服务掉了，重新来启动，然后连接
            LocalService.this.startService(new Intent(LocalService.this,RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this,RemoteService.class),myServiceConnect, Context.BIND_IMPORTANT);
        }
    }

}
