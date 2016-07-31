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
public class RemoteService extends Service {

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
        Log.e("linbin","remoteservice");
        this.bindService(new Intent(this,LocalService.class),myServiceConnect, Context.BIND_IMPORTANT);
        return super.onStartCommand(intent, flags, startId);
    }

    private class  MyServiceConnect implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("linbin","local onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("linbin","local onServiceDisconnected");
            //本地服务掉了，重新来启动，然后连接
            RemoteService.this.startService(new Intent(RemoteService.this,LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this,LocalService.class),myServiceConnect, Context.BIND_IMPORTANT);
        }
    }


    private class MyBinder extends IMyDouble.Stub {
        @Override
        public String getProcessName() throws RemoteException {
            return RemoteService.class.getName();
        }
    }


}
